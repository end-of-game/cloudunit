package fr.treeptik.cloudunit.cli.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.model.Volume;

@Component
public class VolumeService {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthenticationUtils authenticationUtils;

	@Autowired
	private RestUtils restUtils;

	/**
	 * @param name
	 * @return
	 */
	public void createVolume(String name) {
	    authenticationUtils.checkConnected();
	    
	    getVolumes().stream()
	        .filter(v -> v.getName().equals(name))
	        .findAny()
	        .ifPresent(v -> { throw new CloudUnitCliException("Volume name already exists"); });
	    
		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("name", name);
			restUtils.sendPostCommand(authenticationUtils.finalHost + "/volume",
					authenticationUtils.getMap(), parameters);
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't create volume", e);
		}
	}
	
	private List<Volume> getVolumes() {
        String response;
        try {
            response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + "/volume",
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list volumes", e);
        }
        List<Volume> volumes = JsonConverter.getVolumes(response);
        return volumes;
	}

	/**
	 * @return list of volumes as text
	 */
	public List<Volume> displayVolumes() {
	    authenticationUtils.checkConnected();
	    
		return getVolumes();
	}

	/**
	 * @param name
	 * @return
	 */
	public void removeVolume(String name) {
	    authenticationUtils.checkConnected();
	    
		int id = getVolumes().stream()
		        .filter(v -> v.getName().equals(name))
		        .findAny()
		        .orElseThrow(() -> new CloudUnitCliException(MessageFormat.format("No such volume \"{0}\"", name)))
		        .getId();
		try {			
			restUtils.sendDeleteCommand(authenticationUtils.finalHost + "/volume/" + id,
					authenticationUtils.getMap());
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't remove volume", e);
		}
	}
}
