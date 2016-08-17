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
package fr.treeptik.cloudunit.cli.model;

import fr.treeptik.cloudunit.cli.rest.JsonDateDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container
        implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer id;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    protected Date startDate;

    protected String name;

    protected String containerID;
    protected Long memorySize;
    protected String containerIP;
    protected List<PortToOpen> portsToOpen;

    /**
     * This is the applicative status of the server START, STOP, PENDING when it's being modified and not yet in
     * operational state or FAIL if a problem has been detected on this server.
     */

    protected Status status;
    protected Image image;
    /**
     * port8080 for port 8080, port9990 for port 9990
     */

    protected Map<String, String> listPorts = new HashMap<>();
    private String containerFullID;
    private String sshPort;

    private String internalDNSName;

    private String sshdStatus;

    public Container() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public Long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Long memorySize) {
        this.memorySize = memorySize;
    }

    public String getContainerIP() {
        return containerIP;
    }

    public void setContainerIP(String containerIP) {
        this.containerIP = containerIP;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Map<String, String> getListPorts() {
        return listPorts;
    }

    public void setListPorts(Map<String, String> listPorts) {
        this.listPorts = listPorts;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
        this.sshPort = sshPort;
    }

    public String getInternalDNSName() {
        return internalDNSName;
    }

    public void setInternalDNSName(String internalDNSName) {
        this.internalDNSName = internalDNSName;
    }

    public String getSshdStatus() {
        return sshdStatus;
    }

    public void setSshdStatus(String sshdStatus) {
        this.sshdStatus = sshdStatus;
    }

    public String getContainerFullID() {
        return containerFullID;
    }

    public void setContainerFullID(String containerFullID) {
        this.containerFullID = containerFullID;
    }

    public List<PortToOpen> getPortsToOpen() {
        return portsToOpen;
    }

    public void setPortsToOpen(List<PortToOpen> portsToOpen) {
        this.portsToOpen = portsToOpen;
    }

}
