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

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.model.PortToOpen;
import fr.treeptik.cloudunit.utils.CheckUtils;

import java.io.Serializable;

/**
 * Created by nicolas on 31/07/2014.
 */
public class PortResource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String applicationName;

	private String alias;

	private String portToOpen;

	private String portNature;

	public PortResource() {
	}

	public PortResource(PortToOpen portToOpen) {
		this.applicationName = portToOpen.getApplication().getName();
		this.portNature = portToOpen.getNature();
		this.portToOpen = String.valueOf(portToOpen.getPort());
		this.alias = portToOpen.getAlias();
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPortToOpen() {
		return portToOpen;
	}

	public void setPortToOpen(String portToOpen) {
		this.portToOpen = portToOpen;
	}

	public String getPortNature() {
		return portNature;
	}

	public void setPortNature(String portNature) {
		this.portNature = portNature;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public void validatePublishPort() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}

	public void validateDetail() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}

	public void validateClone() throws CheckException {
		CheckUtils.validateSyntaxInput(applicationName, "check.app.name");
	}
}