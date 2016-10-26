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

import fr.treeptik.cloudunit.dao.ScriptingDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Script;
import fr.treeptik.cloudunit.service.ScriptingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.util.List;

@Service
public class ScriptingServiceImpl implements ScriptingService {

    private static String CONNECT_CMD = "connect --login #USER --password #PASSWORD --host #HOST";
    private static String DISCONNECT_CMD = "disconnect";

    @Inject
    private ScriptingDAO scriptingDAO;

    private final Logger logger = LoggerFactory.getLogger(ScriptingServiceImpl.class);

    @Value("${cloudunit.cli.path}")
    private String pathCLI;

    @Value("${cloudunit.instance.name}")
    private String instanceEnv;

    private String host;

    @PostConstruct
    public void init() {
        if ("DEV".equalsIgnoreCase(instanceEnv)) {
            host = "http://localhost:8080";
        } else {
            host = "http://cuplatform_mysql_1.mysql.cloud.unit:8080";
        }
    }

    public void execute(String scriptContent, String login, String password) throws ServiceException {
        logger.info(scriptContent);

        File tmpFile = null;
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        ProcessBuilder processBuilder = null;
        try {
            tmpFile = File.createTempFile(login, ".cmdFile");
            fileWriter = new FileWriter(tmpFile);
            writer = new BufferedWriter(fileWriter);
            String commandConnect = CONNECT_CMD.replace("#USER", login).replace("#PASSWORD", password).replace("#HOST", host);
            logger.debug(commandConnect);
            writer.append(commandConnect);
            writer.newLine();
            writer.append(scriptContent);
            writer.newLine();
            writer.append(DISCONNECT_CMD);
            writer.flush();
            logger.debug(writer.toString());

            File fileCLI = new File(pathCLI);
            if (!fileCLI.exists()) {
                System.out.println("Error ! ");
                StringBuilder msgError = new StringBuilder(512);
                msgError.append("\nPlease run manually (1) : mkdir -p " + pathCLI.substring(0, pathCLI.lastIndexOf("/")));
                msgError.append("\nPlease run manually (2) : wget https://github.com/Treeptik/cloudunit/releases/download/1.0/CloudUnitCLI.jar -O " + pathCLI);
                throw new ServiceException(msgError.toString());
            }

            processBuilder = new ProcessBuilder("java","-jar", pathCLI, "--cmdfile", tmpFile.getAbsolutePath());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            try {
                while((line = reader.readLine()) != null) {
                    logger.info(line);
                    if (line.contains("not found"))
                        throw new ServiceException("Syntax error : " + line);
                    if(line.contains("Invalid or corrupt jarfile"))
                        throw new ServiceException("Invalid or corrupt jarfile");
                }
            } finally {
                reader.close();
            }

        } catch(IOException e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append("login=").append(login);
            msgError.append(", password=").append(password);
            msgError.append(", scriptContent=").append(scriptContent);
            logger.error(msgError.toString(), e);
        } finally {
            try { fileWriter.close(); } catch (Exception ignore) {}
            try { writer.close(); } catch (Exception ignore) {}
        }
    }

    @Override
    public void save(Script script) throws ServiceException {
        scriptingDAO.save(script);
    }

    public Script load(Integer id) throws ServiceException {
        return scriptingDAO.findById(id);
    }

    @Override
    public List<Script> loadAllScripts() throws ServiceException {
        return scriptingDAO.findAllScripts();
    }

    public void delete(Script script) throws ServiceException {
        scriptingDAO.delete(script);
    }

}
