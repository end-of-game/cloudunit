package fr.treeptik.cloudunit.deployments;

/**
 * Created by guillaume on 07/06/16.
 */
public class Wildfly9DeploymentControllerTestIT extends AbstractJBossDeploymentControllerTestIT {

    public Wildfly9DeploymentControllerTestIT() {
        super.release = "wildfly-9";
    }

}
