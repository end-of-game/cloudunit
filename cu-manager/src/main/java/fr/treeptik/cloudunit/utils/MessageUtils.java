/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.model.*;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageUtils {

    public static Message writeBeforeApplicationMessage(User user,
                                                        String applicationName, String type) {
        Message message = new Message();
        String body = "";
        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to create a new Application : "
                    + applicationName;
                break;
            case "UPDATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to update the Application : "
                    + applicationName;
                break;

            case "REMOVE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to remove the Application : "
                    + applicationName;
                break;
            case "START":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to start the Application : " + applicationName;
                break;
            case "STOP":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to stop the Application : " + applicationName;

                break;
            case "RESTART":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to restart the Application : "
                    + applicationName;
                break;
        }

        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(applicationName);
        message.setAuthor(user);

        return message;
    }

    public static Message writeBeforeModuleMessage(User user,
                                                   String moduleName, String applicationName, String type) {
        Message message = new Message();
        String body = "";
        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to add a new module  " + moduleName
                    + " to the application " + applicationName;
                break;

            case "REMOVE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to remove the module " + moduleName
                    + " from the application " + applicationName;
                break;

        }

        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(applicationName);
        message.setAuthor(user);

        return message;
    }

    public static Message writeAfterReturningApplicationMessage(User user,
                                                                Application application, String type) {
        Message message = new Message();
        String body = "";
        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has created a new Application : "
                    + application.getDisplayName();
                break;
            case "UPDATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has updated the Application : " + application.getDisplayName();
                break;

            case "REMOVE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has removed the Application : " + application.getDisplayName();
                break;
            case "START":
                body = "The application " + application.getDisplayName()
                    + " was correctly started by " + user.getFirstName() + " "
                    + user.getLastName();
                break;
            case "STOP":
                body = "The application " + application.getDisplayName()
                    + " was correctly stopped by " + user.getFirstName() + " "
                    + user.getLastName();

                break;
            case "RESTART":
                body = "The application " + application.getDisplayName()
                    + " was correctly restarted by " + user.getFirstName()
                    + " " + user.getLastName();
                break;
        }
        if (message != null) {
            message.setEvent(body);
            message.setType(Message.INFO);
            message.setApplicationName(application.getName());
            message.setAuthor(user);
        }
        return message;
    }

    public static Message writeServerMessage(User user, Server server,
                                             String type) {
        Message message = new Message();
        String body = "";
        switch (type) {

            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has added a new Server : " + server.getName()
                    + " for the application "
                    + server.getApplication().getName();
                break;
            case "UPDATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has updated the Server : " + server.getName()
                    + " for the application "
                    + server.getApplication().getName();
                break;

        }
        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(server.getApplication().getName());
        message.setAuthor(user);
        return message;
    }

    public static Message writeModuleMessage(User user, Module module,
                                             String type) {
        Message message = new Message();
        String body = "";
        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has added a new Module : " + module.getName()
                    + " for the application "
                    + module.getApplication().getName();
                break;
            case "REMOVE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has remove the Module : " + module.getName()
                    + " from the application "
                    + module.getApplication().getName();
                break;
        }

        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(module.getApplication().getName());
        message.setAuthor(user);

        return message;
    }

    public static Message writeDeploymentMessage(User user,
                                                 Deployment deployment, String type) {
        Message message = new Message();
        String body = "";
        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has deployed a new Application : "
                    + deployment.getApplication().getDisplayName() + " from "
                    + deployment.getType().toString();
                break;
        }

        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(deployment.getApplication().getName());
        message.setAuthor(user);

        return message;
    }

    public static Message writeSnapshotMessage(User user, Snapshot snapshot,
                                               String type) {
        Message message = new Message();
        String body = "";
        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has created a new snapshot " + snapshot.getDisplayTag()
                    + " from : " + snapshot.getApplicationDisplayName();
                break;
            case "REMOVE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has removed the snapshot " + snapshot.getDisplayTag();
                break;
            case "CLONEFROMASNAPSHOT":
                body = user.getFirstName() + " " + user.getLastName()
                    + " has created a new application from : "
                    + snapshot.getDisplayTag();
                break;
        }
        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(snapshot.getApplicationName());
        message.setAuthor(user);
        return message;
    }

    public static Message writeBeforeDeploymentMessage(User user,
                                                       Application application, String type) {
        Message message = new Message();
        String body = "";

        switch (type) {
            case "CREATE":
                body = user.getFirstName() + " " + user.getLastName()
                    + " attempts to deploy a new Application : "
                    + application.getDisplayName();
                break;
        }

        message.setEvent(body);
        message.setType(Message.INFO);
        message.setApplicationName(application.getName());
        message.setAuthor(user);

        return message;
    }

    public static Message writeAfterThrowingApplicationMessage(Exception e,
                                                               User user, String type, MessageSource messageSource,
                                                               Locale locale) {
        Message message = new Message();
        String body = "";
        message.setType(Message.ERROR);
        message.setAuthor(user);

        switch (type) {
            case "CREATE":
                body = messageSource.getMessage("app.create.error", null, locale);
                break;
            case "UPDATE":
                body = "Error update application - " + e.getLocalizedMessage();
                break;
            case "DELETE":
                body = "Error delete application - " + e.getLocalizedMessage();
                break;
            case "START":
                body = "Error start application - " + e.getLocalizedMessage();
                break;
            case "STOP":
                body = "Error stop application - " + e.getLocalizedMessage();
                break;
            case "RESTART":
                body = "Error restart application - " + e.getLocalizedMessage();
                break;

            default:
                body = "Error : unkown error";
                break;
        }
        message.setEvent(body);

        return message;
    }

    public static Message writeAfterThrowingModuleMessage(Exception e,
                                                          User user, String type) {
        Message message = new Message();
        String body = "";
        message.setType(Message.ERROR);
        message.setAuthor(user);

        switch (type) {
            case "CREATE":
                body = "Error create application - " + e.getLocalizedMessage();
                break;
            case "DELETE":
                body = "Error delete application - " + e.getLocalizedMessage();
                break;
            default:
                body = "Error : unkown error";
                break;
        }


        message.setEvent(body);

        return message;
    }

    public static Message writeAfterThrowingSnapshotMessage(Exception e,
                                                            User user, String type) {
        Message message = new Message();
        String body = "";
        message.setType(Message.ERROR);
        message.setAuthor(user);

        switch (type) {
            case "CREATE":
                body = "Error create application - " + e.getLocalizedMessage();
                break;
            case "REMOVE":
                body = "Error delete application - " + e.getLocalizedMessage();
                break;
            case "CLONEFORMASNAPSHOT":
                body = "Error delete application - " + e.getLocalizedMessage();
                break;
            default:
                body = "Error : unkown error";
                break;
        }
        message.setEvent(body);
        return message;
    }

}
