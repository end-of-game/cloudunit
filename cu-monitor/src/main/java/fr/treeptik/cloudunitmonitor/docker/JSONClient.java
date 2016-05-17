package fr.treeptik.cloudunitmonitor.docker;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import fr.treeptik.cloudunitmonitor.json.ui.JsonResponse;
import fr.treeptik.cloudunitmonitor.utils.KeyStoreUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Component
public class JSONClient {

	private Logger logger = Logger.getLogger(JSONClient.class);

	private String certsDirPath = System.getenv("HOME") + "/cloudunit/conf/cert/server";

	public JsonResponse sendGet(URI uri) throws IOException {
		StringBuilder builder = new StringBuilder();
		CloseableHttpClient httpclient = build();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpGet);
		LineIterator iterator = IOUtils.lineIterator(response.getEntity().getContent(), "UTF-8");
		while (iterator.hasNext()) {
			builder.append(iterator.nextLine());
		}
		JsonResponse jsonResponse = new JsonResponse(response.getStatusLine().getStatusCode(), builder.toString(),
				null);
		return jsonResponse;
	}

	public int sendPost(URI uri, String body, String contentType) throws ClientProtocolException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("POST : uri  " + uri + " - body  :  " + body + " - contentype : " + contentType);
		}

		/**
		 * TODO
		 */
		logger.info("POST : uri " + uri + " - body  :  " + body + " - contentype : " + contentType);

		CloseableHttpClient httpclient = build();
		HttpPost httpPost = new HttpPost(uri);
		httpPost.addHeader("content-type", contentType);
		httpPost.setEntity(new StringEntity(body));

		StatusLine statusLine = httpclient.execute(httpPost).getStatusLine();
		if (logger.isDebugEnabled()) {
			logger.debug("POST : uri " + uri + " returns " + statusLine.getStatusCode());
		}

		logger.info("POST : uri " + uri + " returns " + statusLine.getStatusCode());

		return statusLine.getStatusCode();
	}

	public int sendDelete(URI uri) throws ClientProtocolException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("DELETE : uri " + uri);
		}
		CloseableHttpClient httpclient = build();
		HttpDelete httpDelete = new HttpDelete(uri);
		StatusLine statusLine = httpclient.execute(httpDelete).getStatusLine();
		if (logger.isDebugEnabled()) {
			logger.debug("DELETE : uri " + uri + " returns " + statusLine.getStatusCode());
		}
		logger.info("POST : uri " + uri + " returns " + statusLine.getStatusCode());

		return statusLine.getStatusCode();
	}

	public CloseableHttpClient build() throws IOException {
		if (ApplicationEntryPoint.MODE.equalsIgnoreCase("https")) {
			org.apache.http.impl.client.HttpClientBuilder builder = HttpClients.custom();
			HttpClientConnectionManager manager = getConnectionFactory(certsDirPath, 10);
			builder.setConnectionManager(manager);
			return builder.build();
		}
		return HttpClients.createDefault();

	}

	private static HttpClientConnectionManager getConnectionFactory(String certPath, int maxConnections)
			throws IOException {
		PoolingHttpClientConnectionManager ret = new PoolingHttpClientConnectionManager(
				getSslFactoryRegistry(certPath));
		ret.setDefaultMaxPerRoute(maxConnections);
		return ret;
	}

	private static Registry<ConnectionSocketFactory> getSslFactoryRegistry(String certPath) throws IOException {
		try {
			KeyStore keyStore = KeyStoreUtils.createDockerKeyStore(certPath);

			SSLContext sslContext = SSLContexts.custom().useTLS().loadKeyMaterial(keyStore, "docker".toCharArray())
					.loadTrustMaterial(keyStore).build();

			SSLConnectionSocketFactory sslsf =

					new SSLConnectionSocketFactory(sslContext);
			return RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslsf).build();
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}
}
