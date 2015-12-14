package fr.treeptik.cloudunit.deployments;

public class SpringBootDeploymentControllerTestIT
    extends AbstractFatJarDeploymentControllerTestIT
{
    public SpringBootDeploymentControllerTestIT()
    {
        super.release = "fatjar";
        super.binary = "spring-boot";
    }
}
