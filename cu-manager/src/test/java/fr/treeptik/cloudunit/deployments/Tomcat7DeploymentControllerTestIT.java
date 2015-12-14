package fr.treeptik.cloudunit.deployments;

public class Tomcat7DeploymentControllerTestIT
    extends AbstractTomcatDeploymentControllerTestIT
{
    public Tomcat7DeploymentControllerTestIT()
    {
        super.release = "tomcat-7";
    }
}
