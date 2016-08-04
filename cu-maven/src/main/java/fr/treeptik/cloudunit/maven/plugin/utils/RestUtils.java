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

package fr.treeptik.cloudunit.maven.plugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import fr.treeptik.cloudunit.maven.plugin.exception.CheckException;
import fr.treeptik.cloudunit.maven.plugin.handler.ResponseErrorHandler;

public class RestUtils
{

    public HttpClientContext localContext;

    public boolean isConnected = false;

    /**
     * @param url
     * @param parameters
     * @param log
     * @return
     * @throws MojoExecutionException
     */
    public Map<String, String> connect( String url, Map<String, Object> parameters, Log log )
        throws MojoExecutionException
    {

        Map<String, String> response = new HashMap<String, String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add( new BasicNameValuePair( "j_username", (String) parameters.get( "login" ) ) );
        nvps.add( new BasicNameValuePair( "j_password", (String) parameters.get( "password" ) ) );
        localContext = HttpClientContext.create();
        localContext.setCookieStore( new BasicCookieStore() );
        HttpPost httpPost = new HttpPost( url );

        try
        {
            httpPost.setEntity( new UrlEncodedFormEntity( nvps ) );
            CloseableHttpResponse httpResponse = httpclient.execute( httpPost, localContext );
            ResponseHandler<String> handler = new ResponseErrorHandler();
            String body = handler.handleResponse( httpResponse );
            response.put( "body", body );
            httpResponse.close();
            isConnected = true;
            log.info( "Connection successful" );
        }
        catch ( Exception e )
        {
            log.error( "Connection failed!  : " + e.getMessage() );
            isConnected = false;
            throw new MojoExecutionException(
                                              "Connection failed, please check your manager location or your credentials" );
        }

        return response;
    }

    /**
     * sendGetCommand
     * 
     * @param url
     * @param parameters
     * @return
     * @throws MojoExecutionException
     * @throws CheckException
     */
    public Map<String, String> sendGetCommand( String url, Log log )
        throws CheckException
    {
        Map<String, String> response = new HashMap<String, String>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet( url );
        try
        {
            CloseableHttpResponse httpResponse = httpclient.execute( httpget, localContext );
            ResponseHandler<String> handler = new ResponseErrorHandler();
            String body = handler.handleResponse( httpResponse );
            response.put( "body", body );
            httpResponse.close();

        }
        catch ( Exception e )
        {
            log.warn( "GET request failed!" );

            throw new CheckException( "Send GET to server failed!", e );
        }

        return response;
    }

    /**
     * @param url
     * @param parameters
     * @return
     * @throws MojoExecutionException
     * @throws CheckException
     */
    public Map<String, Object> sendPostCommand( String url, Map<String, String> parameters, Log log )
        throws CheckException
    {
        Map<String, Object> response = new HashMap<String, Object>();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost( url );
        httpPost.setHeader( "Accept", "application/json" );
        httpPost.setHeader( "Content-type", "application/json" );

        try
        {
            ObjectMapper mapper = new ObjectMapper();
            StringEntity entity = new StringEntity( mapper.writeValueAsString( parameters ) );
            httpPost.setEntity( entity );
            CloseableHttpResponse httpResponse = httpclient.execute( httpPost, localContext );
            ResponseHandler<String> handler = new ResponseErrorHandler();
            String body = handler.handleResponse( httpResponse );
            response.put( "body", body );
            httpResponse.close();
        }
        catch ( Exception e )
        {
            log.warn( "POST request failed!" );

            throw new CheckException( "Send POST to server failed!", e );
        }

        return response;
    }

    public Map<String, Object> sendPostForUpload( String url, String path, Log log )
        throws IOException
    {
        File file = new File( path );
        FileInputStream fileInputStream = new FileInputStream( file );
        fileInputStream.available();
        fileInputStream.close();
        FileSystemResource resource = new FileSystemResource( file );
        Map<String, Object> params = new HashMap<>();
        params.put( "file", resource );
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> postParams = new LinkedMultiValueMap<String, Object>();
        postParams.setAll( params );
        Map<String, Object> response = new HashMap<String, Object>();
        HttpHeaders headers = new HttpHeaders();
        headers.set( "Content-Type", "multipart/form-data" );
        headers.set( "Accept", "application/json" );
        headers.add( "Cookie", "JSESSIONID=" + localContext.getCookieStore().getCookies().get( 0 ).getValue() );
        org.springframework.http.HttpEntity<Object> request =
            new org.springframework.http.HttpEntity<Object>( postParams, headers );
        ResponseEntity<?> result = restTemplate.exchange( url, HttpMethod.POST, request, String.class );
        String body = result.getBody().toString();
        MediaType contentType = result.getHeaders().getContentType();
        HttpStatus statusCode = result.getStatusCode();
        response.put( "content-type", contentType );
        response.put( "statusCode", statusCode );
        response.put( "body", body );

        return response;
    }

}
