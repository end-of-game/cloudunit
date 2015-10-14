package fr.treeptik.cloudunit.deployments;

public class Tomcat8DeploymentControllerTestIT
    extends AbstractDeploymentControllerTestIT
{
    public Tomcat8DeploymentControllerTestIT()
    {
        super.release = "tomcat-8";
    }
}
