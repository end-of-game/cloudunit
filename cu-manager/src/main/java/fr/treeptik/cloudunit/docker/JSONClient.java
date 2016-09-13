/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */


package fr.treeptik.cloudunit.docker;

import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.utils.KeyStoreUtils;
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
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

@Component
public class JSONClient {

    private static Logger logger = LoggerFactory.getLogger(JSONClient.class);

    // We don't use profil spring or anything else because api must have no dependency
    private boolean withTLS = false;

    @Value("${certs.dir.path}")
    private String certsDirPath;

    @Value("${docker.endpoint.mode}")
    private String dockerEndpointMode;

    @PostConstruct
    public void initDockerEndPointMode() {
        if ("http".equalsIgnoreCase(dockerEndpointMode)) {
            logger.warn("Docker TLS mode is disabled");
            withTLS = false;
        } else {
            withTLS = true;
        }
    }

    public JsonResponse sendGet(URI uri)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        CloseableHttpClient httpclient = build();
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

        logger.info("POST : uri " + uri + " - body  :  " + body
                + " - contentype : " + contentType);

        CloseableHttpClient httpclient = build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("content-type", contentType);

        httpPost.setEntity(new StringEntity(body));
        HttpResponse response = httpclient.execute(httpPost);
        /*
        if (logger.isDebugEnabled()) {
            InputStreamReader reader = null;
            reader = new InputStreamReader(response.getEntity()
                    .getContent());
            LineIterator lineIterator = new LineIterator(reader);
            StringBuilder jsonStringBuilder = new StringBuilder();

            while (lineIterator.hasNext()) {
                jsonStringBuilder.append(lineIterator.nextLine());
            }
            logger.debug(jsonStringBuilder.toString());
        }
        */
        StatusLine statusLine = response.getStatusLine();
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

        CloseableHttpClient httpclient = build();
        ;
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

        CloseableHttpClient httpclient = build();
        ;
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

        CloseableHttpClient httpclient = build();
        ;
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
        CloseableHttpClient httpClient = build();
        ;
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

    public int sendDeleteForRegistry(URI uri)
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

    public CloseableHttpClient build() throws IOException {

        if (withTLS) {
            org.apache.http.impl.client.HttpClientBuilder builder = HttpClients.custom();
            HttpClientConnectionManager manager = getConnectionFactory(certsDirPath, 10);
            builder.setConnectionManager(manager);
            return builder.build();
        } else {
            return HttpClients.createDefault();
        }
    }

    private static HttpClientConnectionManager getConnectionFactory(String certPath, int maxConnections) throws IOException {
        PoolingHttpClientConnectionManager ret = new PoolingHttpClientConnectionManager(getSslFactoryRegistry(certPath));
        ret.setDefaultMaxPerRoute(maxConnections);
        return ret;
    }

    private static Registry<ConnectionSocketFactory> getSslFactoryRegistry(String certPath) throws IOException {
        try {
            KeyStore keyStore = KeyStoreUtils.createDockerKeyStore(certPath);

            SSLContext sslContext =
                    SSLContexts.custom()
                            .useTLS()
                            .loadKeyMaterial(keyStore, "docker".toCharArray())
                            .loadTrustMaterial(keyStore)
                            .build();

            SSLConnectionSocketFactory sslsf =

                    new SSLConnectionSocketFactory(sslContext);
            return RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf).build();
        } catch (GeneralSecurityException e) {
            // this isn't ideal but the net effect is the same
            throw new IOException(e);
        }
    }
}

