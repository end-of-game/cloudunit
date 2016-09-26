package fr.treeptik.cloudunit.enums;

import java.util.Map;

/**
 * Created by nicolas on 19/04/2016.
 */
public enum RemoteExecAction {

    CLEAN_LOGS("Add user for admin console", "/opt/cloudunit/scripts/clean-logs.sh"),
    ADD_USER("Add user for admin console", "/opt/cloudunit/scripts/add-user.sh CU_USER CU_PASSWORD"),
    CHANGE_CU_RIGHTS("Change rights for user CloudUnit", "chown -R cloudunit:cloudunit /opt/cloudunit"),
    CHECK_RUNNING("Check running", "/opt/cloudunit/scripts/check-running.sh CU_USER CU_PASSWORD"),
    CHMOD_PLUSX("Check running", "chmod +x"),
    DEPLOY("Deploy application", "/opt/cloudunit/scripts/deploy.sh CU_USER CU_PASSWORD CU_FILE CU_CONTEXT_PATH"),
    CLEAN_DEPLOY("Clean temporary deployment files", "rm CU_TARGET"),
    ADD_ENV("Add env variable", "/opt/cloudunit/scripts/add-env.sh CU_KEY CU_VALUE"),
    MODULE_POST_CREATION("Module pre creation", "/opt/cloudunit/hooks/module-post-creation.sh"),
    MODULE_POST_START_ONCE("Module post start", "/opt/cloudunit/hooks/module-post-start-once.sh"),
    MODULE_POST_START("Module post start just one time", "/opt/cloudunit/hooks/module-post-start.sh"),
    CLONE_PRE_ACTION("Before restoring an application", "/opt/cloudunit/hooks/clone-pre-action.sh"),
    CLONE_POST_ACTION("After restoring an application", "/opt/cloudunit/hooks/clone-post-action.sh"),
    GATHER_CU_ENV("Gather CU env variables", "/opt/cloudunit/scripts/env.sh"),
    ELK_CONFIG_PROXYBEAT_YAML("Configure yaml template", "/opt/cloudunit/scripts/elk_config.sh IP_ELK"),
    ELK_RUN_PROXYBEAT("Run ProxyBeat", "/opt/cloudunit/scripts/elk_run_proxybeat.sh"),
    ELK_UPDATE_KIBANA("Update Kibana", "/opt/cloudunit/scripts/elk_update_kibana.sh CONTAINER");

    private final String label;
    private String command;

    RemoteExecAction(String label, String command) {
        this.label = label;
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }

    public String getCommand(Map<String, String> kvStore) {
        kvStore.entrySet().stream().forEach(e -> command = command.replaceAll(e.getKey(), e.getValue()));
        return command;
    }

    public String[] getCommandBash() {
        String[] commandBash = new String[2];
        commandBash[0] = "bash";
        commandBash[1] = command;
        return commandBash;
    }

}
