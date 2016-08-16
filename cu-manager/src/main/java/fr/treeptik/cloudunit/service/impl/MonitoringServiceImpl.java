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

package fr.treeptik.cloudunit.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunit.dao.MetricDAO;
import fr.treeptik.cloudunit.model.Metric;
import fr.treeptik.cloudunit.service.MonitoringService;

/**
 * Created by nicolas on 25/08/2014.
 */
@Service
public class MonitoringServiceImpl implements MonitoringService {

	private Logger logger = LoggerFactory.getLogger(MonitoringServiceImpl.class);

	@Inject
	private MetricDAO metricDAO;

	@Value("${cadvisor.url}")
	private String cAdvisorURL;

	@Value("${cloudunit.instance.name}")
	private String cuInstanceName;

	@Override
	public String getJsonFromCAdvisor(String containerId) {
		String result = "";
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(cAdvisorURL + "/api/v1.3/containers/docker/" + containerId);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				result = EntityUtils.toString(response.getEntity());
				if (logger.isDebugEnabled()) {
					logger.debug(result);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error(containerId, e);
		}
		return result;
	}

	@Override
	public String getJsonMachineFromCAdvisor() {
		String result = "";
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(cAdvisorURL + "/api/v1.3/machine");
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				result = EntityUtils.toString(response.getEntity());
				if (logger.isDebugEnabled()) {
					logger.debug(result);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("" + e);
		}
		return result;
	}

	@Override
	public List<Metric> findByServer(String serverName) {
		return metricDAO.findAllByServer(serverName);
	}
}
