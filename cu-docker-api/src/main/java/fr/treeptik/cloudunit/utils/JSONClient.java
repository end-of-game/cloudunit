/*
 * Copyright (c) 2015
 *
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : CloudUnit is a registered trademark of Treeptik and cannot be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import com.spotify.docker.client.ApacheUnixSocket;
import fr.treeptik.cloudunit.dto.DockerResponse;
import fr.treeptik.cloudunit.exception.JSONClientException;
import jnr.unixsocket.UnixSocketAddress;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class JSONClient  {

    private Logger logger = LoggerFactory.getLogger(JSONClient.class);

    private Boolean isUnixSocket;
    private String certPathDirectory;
    private File socketFile;

    public JSONClient(Boolean isUnixSocket, String location, String certPathDirectory) {
        this.certPathDirectory = certPathDirectory;
        this.isUnixSocket = isUnixSocket;
        if(isUnixSocket && location !=null) {
            try {
                URI uri = new URI(location);
                final String filename = location.toString()
                        .replaceAll("^unix:///", "unix://localhost/")
                        .replaceAll("^unix://localhost", "");
                this.socketFile = new File(filename);
            } catch (URISyntaxException e) {
                logger.error(isUnixSocket + " " + location, e);
            }
        }
    }

    public DockerResponse sendGet(URI uri) throws JSONClientException {

        if (logger.isDebugEnabled()) {
            logger.debug("Send a get request to : " + uri);
        }
        StringBuilder builder = new StringBuilder();


        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = null;
        try {
            CloseableHttpClient httpclient = buildSecureHttpClient();
            response = httpclient.execute(httpGet);
            LineIterator iterator = IOUtils.lineIterator(response.getEntity()
                    .getContent(), "UTF-8");
            while (iterator.hasNext()) {
                builder.append(iterator.nextLine());
            }
        } catch (IOException e) {
            throw new JSONClientException("Error in sendGet method due to : " + e.getMessage(), e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Status code : " + response.getStatusLine().getStatusCode());
            logger.debug("Server response : " + builder.toString());
        }

        return new DockerResponse(response.getStatusLine().getStatusCode(), builder.toString());

    }

    public DockerResponse sendPost(URI uri, String body, String contentType) throws JSONClientException {

        if (logger.isDebugEnabled()) {
            logger.debug("Send a post request to : " + uri);
            logger.debug("Body content : " + body);
            logger.debug("Content type : " + contentType);
        }


        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("content-type", contentType);
        HttpResponse response = null;
        StringWriter writer = new StringWriter();
        try {
            CloseableHttpClient httpclient = buildSecureHttpClient();
            httpPost.setEntity(new StringEntity(body));
            response = httpclient.execute(httpPost);
            if (response.getEntity() != null) {
                IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
            }
        } catch (IOException e) {
            throw new JSONClientException("Error in sendPost method due to : " + e.getMessage(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Status code : " + response.getStatusLine().getStatusCode());
            logger.debug("Server response : " + writer.toString());
        }

        return new DockerResponse(response.getStatusLine().getStatusCode(), writer.toString());
    }

    public DockerResponse sendPostToRegistryHost(URI uri, String body, String contentType) throws JSONClientException {

        if (logger.isDebugEnabled()) {
            logger.debug("Send a post request to : " + uri);
            logger.debug("Body content : " + body);
            logger.debug("Content type : " + contentType);
        }


        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(1000 * 60 * 50)
                .setConnectTimeout(1000 * 60 * 50).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(config);
        httpPost.addHeader("content-type", contentType);
        httpPost.addHeader("X-Registry-Auth", "123");
        HttpResponse response = null;
        StringWriter writer = new StringWriter();
        try {
            CloseableHttpClient httpclient = buildSecureHttpClient();
            httpPost.setEntity(new StringEntity(body));
            response = httpclient.execute(httpPost);
            IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
        } catch (IOException e) {
            throw new JSONClientException("Error in sendPostToRegistryHost method due to : " + e.getMessage(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Status code : " + response.getStatusLine().getStatusCode());
            logger.debug("Server response : " + writer.toString());
        }

        return new DockerResponse(response.getStatusLine().getStatusCode(), writer.toString());
    }

    public DockerResponse sendDelete(URI uri, Boolean httpRequired) throws JSONClientException {

        if (logger.isDebugEnabled()) {
            logger.debug("Send a delete request to : " + uri);
        }
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpClient = buildSecureHttpClient();
            HttpDelete httpDelete = new HttpDelete(uri);
            response = httpClient.execute(httpDelete);
        } catch (IOException e) {
            throw new JSONClientException("Error in sendDelete method due to : " + e.getMessage(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Status code : " + response.getStatusLine().getStatusCode());
        }

        return new DockerResponse(response.getStatusLine().getStatusCode(), "");
    }

    public CloseableHttpClient buildSecureHttpClient() throws IOException {
        if(isUnixSocket){
            HttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(getUnixSocketFactoryRegistry());
            HttpClientBuilder builder = HttpClients.custom();
            builder.setConnectionManager(manager);
            return builder.build();
        } else if (certPathDirectory != null && !certPathDirectory.isEmpty()) {
            org.apache.http.impl.client.HttpClientBuilder builder = HttpClients.custom();
            HttpClientConnectionManager manager = getConnectionFactory(this.certPathDirectory, 10);
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

    private Registry<ConnectionSocketFactory> getUnixSocketFactoryRegistry() throws IOException {
        UnixSocketFactory socketFactory = new UnixSocketFactory();
        return RegistryBuilder.<ConnectionSocketFactory>create().register("unix", socketFactory).build();
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
            throw new IOException(e);
        }
    }

    private class UnixSocketFactory implements ConnectionSocketFactory{

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            return new ApacheUnixSocket();
        }

        @Override
        public Socket connectSocket(final int connectTimeout,
                                    final Socket socket,
                                    final HttpHost host,
                                    final InetSocketAddress remoteAddress,
                                    final InetSocketAddress localAddress,
                                    final HttpContext context) throws IOException {
            try {
                socket.connect(new UnixSocketAddress(socketFile), connectTimeout);
            } catch (SocketTimeoutException e) {
                throw new ConnectTimeoutException(e, null, remoteAddress.getAddress());
            }

            return socket;
        }
    }


}
