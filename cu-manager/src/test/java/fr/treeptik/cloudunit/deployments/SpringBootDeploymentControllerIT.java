package fr.treeptik.cloudunit.deployments;

public class SpringBootDeploymentControllerIT
        extends AbstractFatjarDeploymentControllerIT
{
    public SpringBootDeploymentControllerIT()
    {
        super.release = "fatjar";
    }
}
