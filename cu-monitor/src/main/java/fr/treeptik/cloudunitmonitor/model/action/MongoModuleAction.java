package fr.treeptik.cloudunitmonitor.model.action;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;

import org.springframework.core.env.Environment;

import fr.treeptik.cloudunitmonitor.model.Module;
import fr.treeptik.cloudunitmonitor.utils.HipacheRedisUtils;

public class MongoModuleAction
    extends ModuleAction
{

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MANAGER_PORT = "3333";

    String redisIp = ApplicationEntryPoint.IP_REDIS;

    public MongoModuleAction( Module parent )
    {
        super( parent );
    }

    /**
     * Should be call when all fields have been initialized
     */

    @Override
    public Module enableModuleManager( HipacheRedisUtils hipacheRedisUtils, Module module, Environment env,
                                       Long instanceNumber )
    {
        hipacheRedisUtils.createModuleManagerKey( module.getApplication(), redisIp, module.getContainerIP(),
                                                  DEFAULT_MANAGER_PORT, module.getImage().getManagerName(),
                                                  instanceNumber );
        return module;
    }

    @Override
    public void updateModuleManager( HipacheRedisUtils hipacheRedisUtils, Environment env )
    {
        hipacheRedisUtils.updateAdminAddress( parent.getApplication(),
                                              redisIp,
                                              parent.getContainerIP(),
                                              DEFAULT_MANAGER_PORT,
                                              parent.getImage().getManagerName(),
                                              Long.parseLong( parent.getName().substring( parent.getName().lastIndexOf( "-" ) + 1 ) ) );

    }
}
