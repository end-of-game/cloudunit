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

package fr.treeptik.cloudunit.docker.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fr.treeptik.cloudunit.docker.model.Network;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.docker.builders.ImageBuilder;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.Image;
import fr.treeptik.cloudunit.docker.model.Volume;
import fr.treeptik.cloudunit.dto.DockerResponse;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.JSONClientException;
import fr.treeptik.cloudunit.utils.JSONClient;

public class SimpleDockerDriver implements DockerDriver {

	private static Logger logger = LoggerFactory.getLogger(SimpleDockerDriver.class);

	private JSONClient client;

	private String protocol;

	private ObjectMapper objectMapper;

	private boolean isTLSActivated;

	private String certPathDir;

	private String host;

	public SimpleDockerDriver(String host, String certPathDir, boolean isTLSActivated) {
		client = new JSONClient(certPathDir, isTLSActivated);
		this.isTLSActivated = isTLSActivated;
		this.certPathDir = certPathDir;
		this.host = host;
		protocol = isTLSActivated ? "https" : "http";
		objectMapper = new ObjectMapper();
	}

	@Override
	public DockerResponse find(DockerContainer container) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host)
					.setPath("/containers/" + container.getName() + "/json").build();
			dockerResponse = client.sendGet(uri);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for find a container request due to " + e.getMessage(), e);
		}

		return dockerResponse;
	}

	@Override
	public DockerResponse findAll() throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/containers/json").build();
			dockerResponse = client.sendGet(uri);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for find all containers request due to " + e.getMessage(), e);
		}

		return dockerResponse;
	}

	@Override
	public DockerResponse create(DockerContainer container) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/containers/create")
					.setParameter("name", container.getName()).build();
			body = objectMapper.writeValueAsString(container.getConfig());
			dockerResponse = client.sendPost(uri, body, "application/json");
		} catch (URISyntaxException | IOException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for create container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse start(DockerContainer container) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host)
					.setPath("/containers/" + container.getName() + "/start").build();
			dockerResponse = client.sendPost(uri, body, "application/json");
		} catch (Exception e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for start container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse stop(DockerContainer container) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host)
					.setPath("/containers/" + container.getName() + "/stop").setParameter("t", "10").build();
			dockerResponse = client.sendPost(uri, body, "application/json");
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for stop container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse kill(DockerContainer container) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host)
					.setPath("/containers/" + container.getName() + "/kill").build();
			dockerResponse = client.sendPost(uri, "", "application/json");
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for kill container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse remove(DockerContainer container) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/containers/" + container.getName())
					.setParameter("v", "1").setParameter("force", "true").build();
			dockerResponse = client.sendDelete(uri, false);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException("An error has occurred for remove request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse findAnImage(Image image) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/images/" + image.getName() + "/json")
					.build();
			dockerResponse = client.sendGet(uri);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for find a container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse commit(DockerContainer container, String tag, String repository)
			throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			DockerResponse response = findAnImage(
					ImageBuilder.anImage().withName(container.getConfig().getImage() + ":" + tag).build());
			Image image = objectMapper.readValue(response.getBody(), Image.class);
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/commit")
					.setParameter("container", container.getName()).setParameter("tag", tag)
					.setParameter("repo", repository).build();
			dockerResponse = client.sendPost(uri, "", "application/json");
			if (dockerResponse.getStatus() == 201 && image != null) {
				removeImage(image);
			}
		} catch (Exception e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException("An error has occurred for commit request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse pull(String tag, String repository) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/images/create")
					.setParameter("fromImage", repository).setParameter("tag", tag.toLowerCase()).build();
			dockerResponse = client.sendPostToRegistryHost(uri, "", "application/json");
			dockerResponse = client.sendPost(uri, "", "application/json");
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException("An error has occurred for pull request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse removeImage(Image image) throws FatalDockerJSONException {

		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/images/" + image.getId()).build();
			dockerResponse = client.sendDelete(uri, false);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException("An error has occurred for removeImage request due to " + e.getMessage(),
					e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse createVolume(Volume volume) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/volumes/create").build();
			body = objectMapper.writeValueAsString(volume);
			dockerResponse = client.sendPost(uri, body, "application/json");
		} catch (URISyntaxException | IOException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for create container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse findVolume(Volume volume) throws FatalDockerJSONException {
		URI uri = null;
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/volumes/" + volume.getName()).build();
			dockerResponse = client.sendGet(uri);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for create container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse removeVolume(Volume volume) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/volumes/" + volume.getName()).build();
			dockerResponse = client.sendDelete(uri, false);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException("An error has occurred for removeImage request due to " + e.getMessage(),
					e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse createNetwork(Network network) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/networks/create").build();
			body = objectMapper.writeValueAsString(network);
			dockerResponse = client.sendPost(uri, body, "application/json");
		} catch (URISyntaxException | IOException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for create container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse findNetwork(Network network) throws FatalDockerJSONException {
		URI uri = null;
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/networks/" + network.getId()).build();
			dockerResponse = client.sendGet(uri);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException(
					"An error has occurred for create container request due to " + e.getMessage(), e);
		}
		return dockerResponse;
	}

	@Override
	public DockerResponse removeNetwork(Network network) throws FatalDockerJSONException {
		URI uri = null;
		String body = new String();
		DockerResponse dockerResponse = null;
		try {
			uri = new URIBuilder().setScheme(protocol).setHost(host).setPath("/networks/" + network.getId()).build();
			dockerResponse = client.sendDelete(uri, false);
		} catch (URISyntaxException | JSONClientException e) {
			StringBuilder contextError = new StringBuilder(256);
			contextError.append("uri : " + uri + " - ");
			contextError.append("request body : " + body + " - ");
			contextError.append("server response : " + dockerResponse);
			logger.error(contextError.toString());
			throw new FatalDockerJSONException("An error has occurred for removeImage request due to " + e.getMessage(),
					e);
		}
		return dockerResponse;
	}

	public JSONClient getClient() {
		return client;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public boolean isTLSActivated() {
		return isTLSActivated;
	}

	public void setTLSActivated(boolean isTLSActivated) {
		this.isTLSActivated = isTLSActivated;
	}

	public String getCertPathDir() {
		return certPathDir;
	}

	public void setCertPathDir(String certPathDir) {
		this.certPathDir = certPathDir;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
