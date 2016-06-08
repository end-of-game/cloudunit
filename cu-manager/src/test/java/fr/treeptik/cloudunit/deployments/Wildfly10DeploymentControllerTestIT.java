package fr.treeptik.cloudunit.deployments;

/**
 * Created by guillaume on 07/06/16.
 */
public class Wildfly10DeploymentControllerTestIT extends AbstractJBossDeploymentControllerTestIT {

    public Wildfly10DeploymentControllerTestIT() {
        super.release = "wildfly-10";
    }

}
