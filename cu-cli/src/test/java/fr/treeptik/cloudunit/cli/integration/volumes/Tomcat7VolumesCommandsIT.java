package fr.treeptik.cloudunit.cli.integration.volumes;

/**
 * Created by guillaume on 16/10/15.
 */
public class Tomcat7VolumesCommandsIT extends AbstractVolumesCommandsIT {

    public Tomcat7VolumesCommandsIT() {
        super.serverType = "tomcat-8";
    }
}
