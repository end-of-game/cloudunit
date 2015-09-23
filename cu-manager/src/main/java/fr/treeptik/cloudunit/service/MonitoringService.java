package fr.treeptik.cloudunit.service;


/**
 * Created by nicolas on 25/08/2014.
 */
public interface MonitoringService {

    public String getFullContainerId(String containerName);

    public String getJsonFromCAdvisor(String containerId);

    public String getJsonMachineFromCAdvisor();
}