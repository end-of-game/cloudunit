package fr.treeptik.cloudunit.cli.integration.volumes;

/**
 * Created by guillaume on 16/10/15.
 */
public class Tomcat6VolumesCommandsIT extends AbstractVolumesCommandsIT {

    public Tomcat6VolumesCommandsIT() {
        super.serverType = "tomcat-8";
    }
}
