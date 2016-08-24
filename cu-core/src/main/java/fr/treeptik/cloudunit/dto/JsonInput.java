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

package fr.treeptik.cloudunit.dto;

import java.io.Serializable;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.utils.CheckUtils;

/**
 * Created by nicolas on 31/07/2014.
 */
public class JsonInput implements Serializable {

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

	private String portNature;

	private String volumeName;

	private String mountPath;

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
		return "JsonInput{" + "applicationName='" + applicationName + '\'' + ", jvmMemory='" + jvmMemory + '\''
				+ ", jvmOptions='" + jvmOptions + '\'' + ", serverName='" + serverName + '\'' + ", imageName='"
				+ imageName + '\'' + ", login='" + login + '\'' + ", location='" + location + '\'' + ", moduleName='"
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
		if (jvmOptions != null) {
			return jvmOptions.replace("\\n", " ");
		} else
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

	public String getPortNature() {
		return this.portNature;
	}

	public void setPortNature(String portNature) {
		this.portNature = portNature;
	}

	// VALIDATIONS

	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

	public String getMountPath() {
		return mountPath;
	}

	public void setMountPath(String mountPath) {
		this.mountPath = mountPath;
	}

	public void validateCreateApp() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
		CheckUtils.validateInput(serverName, "check.server.name");
	}

	public void validateStartApp() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}

	public void validateStopApp() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}

	public void validateRemoveApp() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}

	public void validateAddModule() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
		CheckUtils.validateInput(imageName, "check.image.name");
	}

	public void validateRemoveModule() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
		CheckUtils.validateInputNotEmpty(moduleName, "check.module.name");
	}

	public void validateDetail() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}

	public void validateCreateSnapshot() throws CheckException {
		CheckUtils.validateSyntaxInput(tag, "check.snapshot.name");
	}

	public void validateClone() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}
}