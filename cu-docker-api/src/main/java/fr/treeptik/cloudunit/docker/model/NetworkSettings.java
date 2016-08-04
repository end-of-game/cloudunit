package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by guillaume on 21/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSettings implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Bridge")
	private String bridge;

	@JsonProperty("Gateway")
	private String gateway;

	@JsonProperty("IPAddress")
	private String IPAddress;

	@JsonProperty("IPPrefixLen")
	private Long IPPrefixLen;

	@JsonProperty("GlobalIPv6Address")
	private String globalIPv6Address;

	@JsonProperty("GlobalIPv6PrefixLen")
	private Long globalIPv6PrefixLen;

	@JsonProperty("IPv6Gateway")
	private String IPv6Gateway;

	@JsonProperty("LinkLocalIPv6Address")
	private String linkLocalIPv6Address;

	@JsonProperty("LinkLocalIPv6PrefixLen")
	private Long linkLocalIPv6PrefixLen;

	@JsonProperty("MacAddress")
	private String macAddress;

	@JsonProperty("PortMapping")
	private Map<String, Object> portMapping;

	@JsonProperty("Ports")
	Map<String, Object> ports;

	@JsonProperty("SandboxKey")
	private String sandboxKey;

	@JsonProperty("HairpinMode")
	private Boolean hairpinMode;

	@JsonProperty("SandboxID")
	private String sandboxID;

	@JsonProperty("SecondaryIPAddresses")
	private String secondaryIPAddresses;

	@JsonProperty("SecondaryIPv6Addresses")
	private String secondaryIPv6Addresses;

	@JsonProperty("EndpointID")
	private String endpointID;

	@JsonProperty("Networks")
	private Object networks;

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	public Long getIPPrefixLen() {
		return IPPrefixLen;
	}

	public void setIPPrefixLen(Long IPPrefixLen) {
		this.IPPrefixLen = IPPrefixLen;
	}

	public String getGlobalIPv6Address() {
		return globalIPv6Address;
	}

	public void setGlobalIPv6Address(String globalIPv6Address) {
		this.globalIPv6Address = globalIPv6Address;
	}

	public Long getGlobalIPv6PrefixLen() {
		return globalIPv6PrefixLen;
	}

	public void setGlobalIPv6PrefixLen(Long globalIPv6PrefixLen) {
		this.globalIPv6PrefixLen = globalIPv6PrefixLen;
	}

	public String getIPv6Gateway() {
		return IPv6Gateway;
	}

	public void setIPv6Gateway(String IPv6Gateway) {
		this.IPv6Gateway = IPv6Gateway;
	}

	public String getLinkLocalIPv6Address() {
		return linkLocalIPv6Address;
	}

	public void setLinkLocalIPv6Address(String linkLocalIPv6Address) {
		this.linkLocalIPv6Address = linkLocalIPv6Address;
	}

	public Long getLinkLocalIPv6PrefixLen() {
		return linkLocalIPv6PrefixLen;
	}

	public void setLinkLocalIPv6PrefixLen(Long linkLocalIPv6PrefixLen) {
		this.linkLocalIPv6PrefixLen = linkLocalIPv6PrefixLen;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Map<String, Object> getPortMapping() {
		return portMapping;
	}

	public void setPortMapping(Map<String, Object> portMapping) {
		this.portMapping = portMapping;
	}

	public Map<String, Object> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, Object> ports) {
		this.ports = ports;
	}
}
