/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the GPL is right for you,
 * you can always test our software under the GPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the GPL is right for you,
 * you can always test our software under the GPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.modules.postgre;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.treeptik.cloudunit.dto.EnvUnit;
import fr.treeptik.cloudunit.modules.AbstractModuleControllerTestIT;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by nicolas on 04/10/15.
 */
public class Tomcat8Postgres93ModuleControllerTestIT extends AbstractModuleControllerTestIT {

    public Tomcat8Postgres93ModuleControllerTestIT() {
        super.server = "tomcat-8";
        super.module = "postgresql-9-3";
        super.managerPrefix = "phppgadmin";
        super.managerSuffix = "phppgadmin";
        super.managerPageContent = "phpPgAdmin";
    }

    protected void assertPortIsReallyOpen() {
        new CheckPostgreConnection().invoke();
    }


    public class CheckPostgreConnection {
        public void invoke() {
            String url = null;
            String user = null;
            String password = null;
            Connection connection = null;

            try {
                String urlToCall = "/application/" + applicationName + "/container/"+getContainerName()+"/env";
                ResultActions resultats = mockMvc.perform(get(urlToCall).session(session).contentType(MediaType.APPLICATION_JSON));
                String content = resultats.andReturn().getResponse().getContentAsString();
                String contentResult = resultats.andReturn().getResponse().getContentAsString();
                List<EnvUnit> envs = objectMapper.readValue(contentResult, new TypeReference<List<EnvUnit>>(){});
                user = envs.stream().filter(e -> e.getKey().equals("CU_USER")).findFirst().get().getValue();
                password = envs.stream().filter(e -> e.getKey().equals("CU_PASSWORD")).findFirst().get().getValue();
                url = envs.stream().filter(e -> e.getKey().equals("CU_DNS")).findFirst().get().getValue();
                System.out.println(envs);

                String urlJDBC = "jdbc:postgresql://"+url+"/"+applicationName;
                Properties props = new Properties();
                props.setProperty("user", user);
                props.setProperty("password", password);
                props.setProperty("ssl", "false");
                connection = DriverManager.getConnection(urlJDBC, props);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) { connection.close(); }
                } catch (Exception ignore){}
            }
        }
    }
}
