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

package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.utils.CheckUtils;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by nicolas on 31/07/2014.
 */
public class JsonInput
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicationName;

    private String jvmMemory;

    private String jvmOptions;

    private String serverName;

    private String imageName;

    private String login;

    private String location;

    private String alias;

    private String jvmRelease;

    private String tag;

    private String description;

    private String clientSource;

    private String moduleName;

    private String portToOpen;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJvmRelease() {
        return jvmRelease;
    }

    public void setJvmRelease(String jvmRelease) {
        this.jvmRelease = jvmRelease;
    }

    @Override
    public String toString() {
        return "JsonInput{" + "applicationName='" + applicationName + '\''
            + ", jvmMemory='" + jvmMemory + '\'' + ", jvmOptions='"
            + jvmOptions + '\'' + ", serverName='" + serverName + '\''
            + ", imageName='" + imageName + '\'' + ", login='" + login
            + '\'' + ", location='" + location + '\'' + ", moduleName='"
            + moduleName + '\'' + ", alias='" + alias + '\'' + '}';
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getJvmMemory() {
        return jvmMemory;
    }

    public void setJvmMemory(String jvmMemory) {
        this.jvmMemory = jvmMemory;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientSource() {
        return clientSource;
    }

    public void setClientSource(String clientSource) {
        this.clientSource = clientSource;
    }

    public String getPortToOpen() {
        return portToOpen;
    }

    public void setPortToOpen(String portToOpen) {
        this.portToOpen = portToOpen;
    }

    public void validateCreateApp(MessageSource messageSource) throws CheckException {
        CheckUtils.validateInput(applicationName, messageSource.getMessage("check.app.name", null, Locale.ENGLISH));
        CheckUtils.validateInput(serverName, messageSource.getMessage("check.server.name", null, Locale.ENGLISH));
    }

}