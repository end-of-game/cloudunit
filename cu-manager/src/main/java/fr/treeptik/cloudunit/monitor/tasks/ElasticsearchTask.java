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

package fr.treeptik.cloudunit.monitor.tasks;

import fr.treeptik.cloudunit.monitor.Task;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by nicolas on 27/11/2015.
 */
@Component
public class ElasticsearchTask implements Task {

    private final static Logger logger = LoggerFactory.getLogger(ElasticsearchTask.class);

    private static final Integer DEFAULT_ES_PORT = 9300;

    @Value("${elasticsearch.ip}")
    private String ipES;

    @Override
    public void run() {
        isRunning(true);
    }

    @Scheduled(fixedDelay = 5000)
    private void monitor() {
        isRunning(false);
    }

    /**
     * To evaluate if service is runnning or not
     * If needed, we stop the jvm or send an email
     *
     * @param exit
     */
    private void isRunning(boolean exit) {
        TransportClient client = new TransportClient();
        try {
            client.addTransportAddress(new InetSocketTransportAddress(ipES, DEFAULT_ES_PORT));
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch();
            searchRequestBuilder.setSize(1);
            SearchResponse response = searchRequestBuilder.execute().actionGet();
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder(1024);
            msgError.append("\n****************************************************************");
            msgError.append("\n****************************************************************");
            msgError.append("\n**                                                            **");
            msgError.append("\n   ELASTICSEARCH NOT RUNNING : " + ipES + ":" + DEFAULT_ES_PORT);
            if (exit) {
                System.exit(-1);
                msgError.append("\n**   FATAL ERROR : JVM IS KILLED                          **");
            } else {
                msgError.append("\n**   WARNING ERROR : MANUAL ACTION REQUIRED               **");
            }
            msgError.append("\n**                                                            **");
            msgError.append("\n****************************************************************");
            msgError.append("\n****************************************************************");
            logger.error(msgError.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }

    }


}
