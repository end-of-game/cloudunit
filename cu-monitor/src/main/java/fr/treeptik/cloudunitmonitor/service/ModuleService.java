package fr.treeptik.cloudunitmonitor.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunitmonitor.utils.HipacheRedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunitmonitor.dao.ModuleDAO;
import fr.treeptik.cloudunitmonitor.docker.model.DockerContainer;
import fr.treeptik.cloudunitmonitor.exception.DockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.ServiceException;
import fr.treeptik.cloudunitmonitor.utils.ContainerMapper;

@Service
public class ModuleService
{

    private Logger logger = LoggerFactory.getLogger( ModuleService.class );

    @Inject
    private ModuleDAO moduleDAO;

    @Inject
    private ContainerMapper containerMapper;

    @Inject
    private HipacheRedisUtils hipacheRedisUtils;

    @Inject
    private Environment env;

    public List<Module> findAll()
        throws ServiceException
    {
        try
        {
            return moduleDAO.findAll();
        }
        catch ( DataAccessException e )
        {
            throw new ServiceException( "error find all modules", e );
        }
    }

    @Transactional
    public Module startModule( Module module )
        throws ServiceException
    {

        Map<String, String> forwardedPorts = new HashMap<>();

        Application application = module.getApplication();

        try
        {
            DockerContainer dockerContainer = new DockerContainer();
            DockerContainer dataDockerContainer = new DockerContainer();
            dockerContainer.setName( module.getName() );
            dockerContainer.setPorts( forwardedPorts );
            dockerContainer.setImage( module.getImage().getName() );

            if ( module.getImage().getImageType().equals( "module" ) )
            {
                dockerContainer.setVolumesFrom( module.getVolumesFrom() );
            }
            if ( module.getImage().getName().contains( "git" ) )
            {
                dockerContainer.setPortBindings( "22/tcp", "0.0.0.0", module.getSshPort() );
                dockerContainer.setPortBindings( "80/tcp", "0.0.0.0", module.getListPorts().get( "80/tcp" ) );

            }

            DockerContainer.start( dockerContainer, application.getManagerIp() );

            dockerContainer = DockerContainer.findOne( dockerContainer, application.getManagerIp() );

            if ( module.getImage().getImageType().equals( "module" ) )
            {
                dataDockerContainer.setName( module.getName() + "-data" );
                try
                {
                    DockerContainer.start( dataDockerContainer, application.getManagerIp() );
                }
                catch ( Exception e )
                {
                    logger.info( "data container already started" );
                }
            }

            module = containerMapper.mapDockerContainerToModule( dockerContainer, module );

            // Unsubscribe module manager
            module.getModuleAction().updateModuleManager(hipacheRedisUtils);

            module.setStartDate( new Date() );
            module = moduleDAO.saveAndFlush( module );

        }
        catch ( PersistenceException e )
        {
            module.setStatus( Status.FAIL );
            module = moduleDAO.saveAndFlush( module );
            throw new ServiceException( e.getLocalizedMessage(), e );
        }
        catch ( DockerJSONException e )
        {
            module.setStatus( Status.FAIL );
            module = moduleDAO.saveAndFlush( module );
            throw new ServiceException( e.getLocalizedMessage(), e );
        }
        return module;
    }

}
