/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.docker;

import fr.treeptik.cloudunit.json.ui.JsonResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class JSONClient {

    private Logger logger = LoggerFactory.getLogger(JSONClient.class);

    public JsonResponse sendGet(URI uri)
        throws IOException {
        StringBuilder builder = new StringBuilder();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = httpclient.execute(httpGet);
        LineIterator iterator = IOUtils.lineIterator(response.getEntity()
            .getContent(), "UTF-8");
        while (iterator.hasNext()) {
            builder.append(iterator.nextLine());
        }
        JsonResponse jsonResponse = new JsonResponse(response.getStatusLine()
            .getStatusCode(), builder.toString(), null);
        return jsonResponse;
    }

    public int sendPost(URI uri, String body, String contentType)
        throws ClientProtocolException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri  " + uri + " - body  :  " + body
                + " - contentype : " + contentType);
        }

        /**
         * TODO
         */
        logger.info("POST : uri " + uri + " - body  :  " + body
            + " - contentype : " + contentType);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("content-type", contentType);

        httpPost.setEntity(new StringEntity(body));

        StatusLine statusLine = httpclient.execute(httpPost).getStatusLine();
        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri " + uri + " returns "
                + statusLine.getStatusCode());
        }

        logger.info("POST : uri " + uri + " returns "
            + statusLine.getStatusCode());

        return statusLine.getStatusCode();
    }

    public int sendPostForStart(URI uri, String body, String contentType)
        throws ClientProtocolException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri  " + uri + " - body  :  " + body
                + " - contentype : " + contentType);
        }

        /**
         * TODO
         */
        logger.info("POST : uri " + uri + " - body  :  " + body
            + " - contentype : " + contentType);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("content-type", contentType);

        httpPost.setEntity(new StringEntity(body));

        CloseableHttpResponse execute = httpclient.execute(httpPost);
        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri " + uri + " returns "
                + execute.getStatusLine().getStatusCode());
        }

        StringWriter writer = new StringWriter();

        if (execute.getEntity() != null)

            IOUtils.copy(execute.getEntity().getContent(), writer, "UTF-8");

        if (writer.toString().contains("address")) {
            this.sendPostForStart(uri, body, contentType);
            return execute.getStatusLine().getStatusCode();
        }

        logger.info("POST : uri " + uri + " returns "
            + execute.getStatusLine().getStatusCode());

        return execute.getStatusLine().getStatusCode();
    }

    public Map<String, Object> sendPostAndGetImageID(URI uri, String body,
                                                     String contentType)
        throws ClientProtocolException, IOException {

        Map<String, Object> response = new HashMap<>();

        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri  " + uri + " - body  :  " + body
                + " - contentype : " + contentType);
        }

        /**
         * TODO
         */
        logger.info("POST : uri " + uri + " - body  :  " + body
            + " - contentype : " + contentType);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("content-type", contentType);

        httpPost.setEntity(new StringEntity(body));

        CloseableHttpResponse execute = httpclient.execute(httpPost);

        StatusLine statusLine = execute.getStatusLine();

        StringWriter writer = new StringWriter();
        IOUtils.copy(execute.getEntity().getContent(), writer, "UTF-8");

        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri " + uri + " returns "
                + statusLine.getStatusCode());
        }

        logger.info("POST : uri " + uri + " returns "
            + statusLine.getStatusCode());
        response.put("code", statusLine.getStatusCode());
        response.put("body", writer.toString());
        return response;
    }

    public Map<String, Object> sendPostWithRegistryHost(URI uri, String body,
                                                        String contentType)
        throws ClientProtocolException, IOException {

        Map<String, Object> response = new HashMap<String, Object>();

        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri  " + uri + " - body  :  " + body
                + " - contentype : " + contentType);
        }

        /**
         * TODO
         */
        logger.info("POST : uri " + uri + " - body  :  " + body
            + " - contentype : " + contentType);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom()
            .setSocketTimeout(1000 * 60 * 5)
            .setConnectTimeout(1000 * 60 * 5).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(config);
        httpPost.addHeader("content-type", contentType);
        httpPost.addHeader("X-Registry-Auth", "1234");

        httpPost.setEntity(new StringEntity(body));

        CloseableHttpResponse execute = httpclient.execute(httpPost);

        StatusLine statusLine = execute.getStatusLine();

        StringWriter writer = new StringWriter();

        IOUtils.copy(execute.getEntity().getContent(), writer, "UTF-8");

        if (logger.isDebugEnabled()) {
            logger.debug("POST : uri " + uri + " returns "
                + statusLine.getStatusCode());
        }

        logger.info("POST : uri " + uri + " returns "
            + statusLine.getStatusCode());
        response.put("code", statusLine.getStatusCode());
        response.put("body", writer.toString());
        return response;
    }

    public int sendDelete(URI uri)
        throws ClientProtocolException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("DELETE : uri " + uri);
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(uri);
        StatusLine statusLine = httpClient.execute(httpDelete).getStatusLine();
        if (logger.isDebugEnabled()) {
            logger.debug("DELETE : uri " + uri + " returns "
                + statusLine.getStatusCode());
        }
        logger.info("DELETE : uri " + uri + " returns "
            + statusLine.getStatusCode());

        return statusLine.getStatusCode();
    }
}
