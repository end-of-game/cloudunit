/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.cli.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.cli.exception.CustomResponseErrorHandler;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.utils.AuthentificationUtils;

@Component
public class RestUtils {

    public static final String CONTENT_TYPE = "content-type";
    public static final String STATUS_CODE = "statusCode";
    public static final String BODY = "body";

    public HttpClientContext localContext;
    @Autowired
    private AuthentificationUtils authentificationUtils;

    public Map<String, String> connect(String url, Map<String, Object> parameters) throws ManagerResponseException {

        Map<String, String> response = new HashMap<String, String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", (String) parameters.get("login")));
        nvps.add(new BasicNameValuePair("j_password", (String) parameters.get("password")));
        localContext = HttpClientContext.create();
        localContext.setCookieStore(new BasicCookieStore());
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost, localContext);
            ResponseHandler<String> handler = new CustomResponseErrorHandler();
            String body = handler.handleResponse(httpResponse);
            response.put(BODY, body);
            httpResponse.close();
        } catch (Exception e) {
            authentificationUtils.getMap().clear();
            throw new ManagerResponseException(e.getMessage(), e);
        }

        return response;
    }

    /**
     * sendGetCommand
     *
     * @param url
     * @param parameters
     * @return
     */
    public Map<String, String> sendGetCommand(String url, Map<String, Object> parameters)
            throws ManagerResponseException {
        Map<String, String> response = new HashMap<String, String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse httpResponse = httpclient.execute(httpget, localContext);
            ResponseHandler<String> handler = new CustomResponseErrorHandler();
            String body = handler.handleResponse(httpResponse);
            response.put(BODY, body);
            httpResponse.close();

        } catch (Exception e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }

        return response;
    }

    public Map<String, String> sendGetFileCommand(String url, String filePath, Map<String, Object> parameters)
            throws ManagerResponseException {
        Map<String, String> response = new HashMap<String, String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse httpResponse = httpclient.execute(httpget, localContext);
            InputStream inputStream = httpResponse.getEntity().getContent();
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            int inByte;
            while ((inByte = inputStream.read()) != -1)
                fos.write(inByte);
            inputStream.close();
            fos.close();
            httpResponse.close();

        } catch (Exception e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }

        return response;
    }

    /**
     * sendDeleteCommand
     *
     * @param url
     * @return
     */
    public Map<String, String> sendDeleteCommand(String url, Map<String, Object> credentials)
            throws ManagerResponseException {
        Map<String, String> response = new HashMap<String, String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpDelete httpDelete = new HttpDelete(url);
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpclient.execute(httpDelete, localContext);
            ResponseHandler<String> handler = new CustomResponseErrorHandler();
            String body = handler.handleResponse(httpResponse);
            response.put("body", body);
            httpResponse.close();
        } catch (Exception e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }

        return response;
    }

    /**
     * sendPostCommand
     *
     * @param url
     * @param credentials
     * @param parameters
     * @return
     * @throws ClientProtocolException
     */
    public Map<String, Object> sendPostCommand(String url, Map<String, Object> credentials,
            Map<String, String> parameters) throws ManagerResponseException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String entity = mapper.writeValueAsString(parameters);
            return sendPostCommand(url, credentials, entity);
        } catch (Exception e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }
    }

    /**
     * sendPostCommand
     *
     * @param url
     * @param credentials
     * @param entity
     * @return
     * @throws ClientProtocolException
     */
    public Map<String, Object> sendPostCommand(String url, Map<String, Object> credentials, String entity)
            throws ManagerResponseException {
        Map<String, Object> response = new HashMap<String, Object>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try {
            StringEntity stringEntity = new StringEntity(entity);
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost, localContext);
            ResponseHandler<String> handler = new CustomResponseErrorHandler();
            String body = handler.handleResponse(httpResponse);
            response.put(BODY, body);
            httpResponse.close();
        } catch (Exception e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }

        return response;
    }

    /**
     * sendPutCommand
     *
     * @param url
     * @param parameters
     * @return
     * @throws ClientProtocolException
     */
    public Map<String, Object> sendPutCommand(String url, Map<String, Object> credentials,
            Map<String, String> parameters) throws ManagerResponseException {
        Map<String, Object> response = new HashMap<String, Object>();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");

        try {
            ObjectMapper mapper = new ObjectMapper();
            StringEntity entity = new StringEntity(mapper.writeValueAsString(parameters));
            httpPut.setEntity(entity);
            CloseableHttpResponse httpResponse = httpclient.execute(httpPut, localContext);
            ResponseHandler<String> handler = new CustomResponseErrorHandler();
            String body = handler.handleResponse(httpResponse);
            response.put(BODY, body);

            httpResponse.close();
        } catch (Exception e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }

        return response;
    }

    /**
     * 
     * /** sendPostCommand
     *
     * @param url
     * @param parameters
     * @return
     * @throws ClientProtocolException
     */
    public Map<String, Object> sendPostForUpload(String url, Map<String, Object> parameters) {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        mc.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(mc);
        MultiValueMap<String, Object> postParams = new LinkedMultiValueMap<String, Object>();
        postParams.setAll(parameters);
        Map<String, Object> response = new HashMap<String, Object>();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        headers.set("Accept", "application/json");
        headers.add("Cookie", "JSESSIONID=" + localContext.getCookieStore().getCookies().get(0).getValue());
        org.springframework.http.HttpEntity<Object> request = new org.springframework.http.HttpEntity<Object>(
                postParams, headers);
        ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String body = result.getBody().toString();
        MediaType contentType = result.getHeaders().getContentType();
        HttpStatus statusCode = result.getStatusCode();
        response.put(CONTENT_TYPE, contentType);
        response.put(STATUS_CODE, statusCode);
        response.put(BODY, body);

        return response;

    }
}
