package fr.treeptik.cloudunit.deployments;

public class Tomcat8DeploymentControllerTestIT
    extends AbstractTomcatDeploymentControllerTestIT
{
    public Tomcat8DeploymentControllerTestIT()
    {
        super.release = "tomcat-8";
    }
}
