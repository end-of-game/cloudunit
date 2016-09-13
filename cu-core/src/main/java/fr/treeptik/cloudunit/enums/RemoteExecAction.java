package fr.treeptik.cloudunit.enums;

/**
 * Created by nicolas on 19/04/2016.
 */
public enum RemoteExecAction {

    MODULE_PRE_CREATION("Module pre creation", "/cloudunit/appconf/hooks/module-pre-creation.sh"),
    MODULE_POST_CREATION("Module pre creation", "/cloudunit/appconf/hooks/module-post-creation.sh"),

    APPLICATION_PRE_FIRST_DEPLOY("Application pre first deploy", "/cloudunit/appconf/hooks/application-pre-first-deploy.sh"),
    APPLICATION_POST_FIRST_DEPLOY("Application post first deploy", "/cloudunit/appconf/hooks/application-post-first-deploy.sh"),

    APPLICATION_PRE_START("Application pre start", "/cloudunit/appconf/hooks/application-pre-start.sh"),
    APPLICATION_POST_START("Application post start", "/cloudunit/appconf/hooks/application-post-start.sh"),

    APPLICATION_PRE_STOP("Application pre stop", "/cloudunit/appconf/hooks/application-pre-stop.sh"),
    APPLICATION_POST_STOP("Application post stop", "/cloudunit/appconf/hooks/application-post-stop.sh"),

    SNAPSHOT_PRE_ACTION("Before Snapshot", "/cloudunit/appconf/hooks/snapshot-pre-action.sh"),
    SNAPSHOT_POST_ACTION("After Snapshot", "/cloudunit/appconf/hooks/snapshot-post-action.sh"),

    CLONE_PRE_ACTION("Before restoring an application", "/cloudunit/appconf/hooks/clone-pre-action.sh"),
    CLONE_POST_ACTION("After restoring an application", "/cloudunit/appconf/hooks/clone-post-action.sh"),

    GATHER_CU_ENV("Gather CU env variables", "/cloudunit/scripts/env.sh");

    private final String label;
    private final String command;

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

    public String[] getCommandBash() {
        String[] commandBash = new String[2];
        commandBash[0] = "bash";
        commandBash[1] = command;
        return commandBash;
    }
}
