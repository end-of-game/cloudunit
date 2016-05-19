package fr.treeptik.cloudunitmonitor.model.action;

import java.io.Serializable;

import org.springframework.core.env.Environment;

import fr.treeptik.cloudunitmonitor.model.Module;
import fr.treeptik.cloudunitmonitor.utils.HipacheRedisUtils;

public abstract class ModuleAction
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    protected Module parent;

    public ModuleAction( Module parent )
    {
        this.parent = parent;
    }

    /**
     * add url to access to module manager (e.g phpMyAdmin)
     * 
     * @param hipacheRedisUtils
     * @param parent
     * @param env
     * @param instanceNumber
     * @return
     */
    public abstract Module enableModuleManager( HipacheRedisUtils hipacheRedisUtils, Module parent, Environment env,
                                                Long instanceNumber );

    public abstract void updateModuleManager( HipacheRedisUtils hipacheRedisUtils, Environment env );

}
