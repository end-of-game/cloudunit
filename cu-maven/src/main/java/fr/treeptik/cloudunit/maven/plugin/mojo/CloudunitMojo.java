/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
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

package fr.treeptik.cloudunit.maven.plugin.mojo;

import fr.treeptik.cloudunit.maven.plugin.exception.CheckException;
import fr.treeptik.cloudunit.maven.plugin.utils.JsonConverter;
import fr.treeptik.cloudunit.maven.plugin.utils.RestUtils;
import fr.treeptik.cloudunit.maven.plugin.utils.UtilFactory;
import fr.treeptik.cloudunit.model.Application;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class CloudunitMojo
        extends AbstractMojo {
    private final static String DEFAULT_MANAGER_LOCATION = "127.0.0.1";

    private final static String DEFAULT_MANAGER_PORT = "8080";

    /*
     * Cloudunit manager global paramaters
     */

    @Parameter(defaultValue = DEFAULT_MANAGER_LOCATION)
    protected String managerLocation;

    @Parameter(defaultValue = DEFAULT_MANAGER_PORT)
    protected String managerPort;

    private String managerURL;

    /*
     * User account parameters
     */

    @Parameter(required = true)
    protected String username;

    @Parameter(required = true)
    protected String password;

    @Parameter(required = true)
    protected String applicationName;

    @Parameter(defaultValue = "false")
    protected boolean snapshotOnDeploy;

    /*
     * Settings for automate app creation
     */

    @Parameter(defaultValue = "false")
    protected boolean createIfNotExists;

    @Parameter
    protected String server;

    @Parameter
    protected String[] modules;

    /*
     * Utils for communicate with Cloudunit REST API
     */

    private RestUtils restUtils = UtilFactory.getRestUtils();

    protected void connect()
            throws MojoExecutionException {
        if (!restUtils.isConnected) {
            if (managerURL == null) {
                managerURL = "http://" + managerLocation + ":" + managerPort;
            }

            Map<String, Object> connectionParameters = new HashMap<String, Object>();
            connectionParameters.put("login", username);
            connectionParameters.put("password", password);
            restUtils.connect(managerURL + "/user/authentication", connectionParameters, getLog());
        }

    }

    protected void deploy(String path)
            throws MojoExecutionException, IOException {

        restUtils.sendPostForUpload(managerURL + "/application/" + applicationName + "/deploy", path, getLog());

    }

    protected void createSnapshot()
            throws MojoExecutionException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("applicationName", applicationName);
        parameters.put("tag", (applicationName.substring(0, 1) + new Date().getTime()).substring(0, 13));
        parameters.put("description",
                "Snapshot from maven plugin at " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        try {

            getLog().info("Starting snapshot");
            restUtils.sendPostCommand(managerURL + "/snapshot", parameters, getLog());
            getLog().info("Snapshot has finished");
        } catch (CheckException e) {
            getLog().warn("The application snapshot failed due to " + e.getLocalizedMessage());

        }

    }

    protected boolean isApplicationExists()
            throws MojoExecutionException {

        Application application = null;
        try {
            application =
                    JsonConverter.getApplication(restUtils.sendGetCommand(managerURL + "/application/" + applicationName,
                            getLog()).get("body"));
        } catch (CheckException e) {
            getLog().warn("This app does not exists on the current CloudUnit platform");
        }

        return application != null;
    }

    protected void create()
            throws CheckException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("applicationName", applicationName);
        parameters.put("serverName", server);
        try {
            restUtils.sendPostCommand(managerURL + "/application/", parameters, getLog());
        } catch (CheckException e) {
            getLog().warn("An error has occured on application creating");
            throw new CheckException("An error has occured on application creating " + e.getMessage(), e);
        }
    }

    protected void addModule()
            throws CheckException {
        Map<String, String> parameters = new HashMap<String, String>();
        try {
            for (String imageName : modules) {
                parameters.put("imageName", imageName);
                parameters.put("applicationName", applicationName);
                restUtils.sendPostCommand(managerURL + "/module", parameters, getLog());
                parameters.clear();
            }
        } catch (CheckException e) {
            getLog().warn("An error has occured on module adding. Check module names in your plugin configuration");
            throw new CheckException("An error has occured on module adding." +
                    " Check module names in your plugin configuration " + e, e);
        }
    }

    protected String getAbsolutePathWarFile() {
        MavenProject mavenProject = (MavenProject) getPluginContext().get("project");
        String directory = mavenProject.getBuild().getDirectory();
        String finalName = mavenProject.getBuild().getFinalName();
        String packaging = mavenProject.getPackaging();
        String fullPath = directory + "/" + finalName + "." + packaging;
        getLog().debug("absolutePathWarFile : " + fullPath);
        return fullPath;
    }
}
