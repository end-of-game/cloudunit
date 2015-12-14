package fr.treeptik.cloudunit.deployments;

public class Tomcat6DeploymentControllerTestIT
    extends AbstractTomcatDeploymentControllerTestIT
{
    public Tomcat6DeploymentControllerTestIT()
    {
        super.release = "tomcat-6";
    }
}
