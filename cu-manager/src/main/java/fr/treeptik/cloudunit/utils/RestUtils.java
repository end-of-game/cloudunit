package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestUtils {

	private static Logger logger = LoggerFactory.getLogger(RestUtils.class);

	public static String sendPutCommand(String url, Server server) {
		String body = null;

		return body;
	}

	public static String sendGetCommand(String url) {

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<?> result = restTemplate.getForEntity(url, String.class);
		String body = result.getBody().toString();
		MediaType contentType = result.getHeaders().getContentType();
		HttpStatus statusCode = result.getStatusCode();
		logger.info("REST PUT COMMAND " + contentType + " " + statusCode);
		return body;
	}

	@SuppressWarnings("rawtypes")
	public static String sendPostCommand(String url, String volume) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Accept", "text/plain");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("Binds", volume);
		@SuppressWarnings("unchecked")
		HttpEntity request = new HttpEntity(params, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url,
				request, String.class);
		return response.toString();
	}

}