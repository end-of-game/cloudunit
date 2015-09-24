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