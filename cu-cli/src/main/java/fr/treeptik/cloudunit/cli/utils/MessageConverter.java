/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.cli.utils;

import static java.lang.System.out;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.model.*;

public class MessageConverter {

    private static ShellRowFormater printer = new ShellRowFormater(out);

    private static Logger logger = Logger.getLogger("MessageConverter");

    private static Map<String, String> modulePorts = new HashMap<>();

    public static void buildApplicationMessage(Application application, String dockerManagerIP) {
        logger.log(Level.WARNING, "\n GENERAL \n");

        printer.print(new String[][] {
                new String[] { "APPLICATION NAME", "AUTHOR", "STARTING DATE", "SERVER TYPE", "STATUS", "JAVA VERSION" },
                new String[] { application.getName(),
                        application.getUser().getLastName() + " " + application.getUser().getFirstName(),
                        DateUtils.formatDate(application.getDate()),
                        application.getServer().getImage().getName().toUpperCase(), application.getStatus().toString(),
                        application.getJvmRelease() },

        });

        logger.log(Level.WARNING, "\n SERVER INFORMATION \n");

        buildServerMessage(application, dockerManagerIP);
        if (!application.getModules().isEmpty()) {
            logger.log(Level.WARNING, "\n MODULES INFORMATION \n");
            buildModuleMessage(application, dockerManagerIP);

        }

    }

    public static void buildGlobalModuleMessage(Application application, String dockerManagerIP) {
        buildServerMessage(application, dockerManagerIP);
        if (!application.getModules().isEmpty()) {
            logger.log(Level.WARNING, "\n AVAILABLE MODULES \n");
            buildLightModuleMessage(application, dockerManagerIP);

        }

    }

    public static void buildServerMessage(Application application, String dockerManagerIP) {

        Server server = application.getServer();

        String[][] tab = new String[2][7];
        tab[0][0] = "TYPE";
        tab[0][1] = "ADDRESS";
        tab[0][2] = "SSH PORT";
        tab[0][3] = "STATUS";
        tab[0][4] = "JVM OPTS";
        tab[0][5] = "MEMORY";
        tab[0][6] = "MANAGER LOCATION";

        tab[1][0] = server.getImage().getName().toUpperCase();
        tab[1][1] = application.getSuffixCloudUnitIO().substring(1);
        tab[1][2] = server.getSshPort();
        tab[1][3] = server.getStatus().toString();
        tab[1][4] = server.getJvmOptions() != "" ? server.getJvmOptions() : "NONE";
        tab[1][5] = server.getJvmMemory() + "";
        tab[1][6] = server.getManagerLocation() + " ";

        printer.print(tab);

    }

    public static void buildImageResponse(Image image) {
        String status = "";
        if (image.getStatus().equals(0)) {
            status = "DISABLED";
        } else {
            status = "ENABLED";
        }

        printer.print(
                new String[][] { new String[] { "IMAGE NAME", "NEW STATUS" }, new String[] { image.getName(), status },

                });
    }

    public static void buildImageListResponse(List<Image> images) {
        String[][] tab = new String[images.size() + 1][2];
        tab[0][0] = " IMAGE NAME";
        tab[0][1] = "STATUS";
        int i = 0;
        for (Image image : images) {
            String status = image.getStatus().equals(0) ? status = "DISABLED" : "ENABLED";
            tab[i + 1][0] = image.getName();
            tab[i + 1][1] = status;
            i++;
        }
        printer.print(tab);
    }

    public static void buildModuleMessage(Application application, String dockerManagerIP) {

        modulePorts.put("mysql-5-5", "3306");
        modulePorts.put("postgresql-9-3", "5432");

        List<Module> modules = application.getModules();
        if (modules.size() < 1) {
            logger.log(Level.WARNING, "No modules found!");
        } else {

            for (Module module : modules) {

                int moduleIndex = 0;

                String[][] tab = new String[4][2];

                tab[0][0] = "MODULE NAME";
                tab[1][0] = "TYPE";
                tab[2][0] = "DOMAIN NAME";
                tab[3][0] = "FORWARDED PORT";

                if (!module.getImage().getImageType().equalsIgnoreCase(Image.MODULE)) {
                    continue;
                }

                int indexName = module.getName().indexOf(application.getName());
                // #POINTDROGUE
                tab[0][moduleIndex + 1] = module.getName().substring(indexName + application.getName().length() + 1);
                tab[1][moduleIndex + 1] = module.getImage().getName();
                tab[2][moduleIndex + 1] = module.getInternalDNSName();
                moduleIndex++;

                printer.print(tab);

                logger.log(Level.WARNING, " ");

            }

        }
    }

    public static void buildLightModuleMessage(Application application, String dockerManagerIP) {

        List<Module> modules = application.getModules();
        if (modules.size() < 1) {
            logger.log(Level.WARNING, "No modules found!");
        } else {

            for (Module module : modules) {

                int moduleIndex = 0;

                String[][] tab = new String[2][2];

                tab[0][0] = "MODULE NAME";
                tab[1][0] = "TYPE";

                if (!module.getImage().getImageType().equalsIgnoreCase(Image.MODULE)) {
                    continue;
                }

                int indexName = module.getName().indexOf(application.getName());
                // #POINTDROGUE
                tab[0][moduleIndex + 1] = module.getName().substring(indexName + application.getName().length() + 1);
                tab[1][moduleIndex + 1] = module.getImage().getName();

                moduleIndex++;

                printer.print(tab);

                logger.log(Level.WARNING, " ");

            }

        }
    }

    public static void buildListApplications(List<Application> apps) {
        if (apps.isEmpty()) {
            logger.log(Level.WARNING, "No apps found!");
        } else {

            String[][] tab = new String[apps.size() + 1][5];
            tab[0][0] = "APPLICATION NAME";
            tab[0][1] = "AUTHOR";
            tab[0][2] = "STARTING DATE";
            tab[0][3] = "SERVER TYPE";
            tab[0][4] = "STATUS";

            Application application = null;
            for (int i = 0; i < apps.size(); i++) {
                application = apps.get(i);

                tab[i + 1][0] = application.getName();
                tab[i + 1][1] = application.getUser().getLastName() + " " + application.getUser().getFirstName();
                tab[i + 1][2] = DateUtils.formatDate(application.getDate());
                tab[i + 1][3] = application.getServer().getImage().getName();
                tab[i + 1][4] = application.getStatus().name();
            }
            printer.print(tab);

        }
    }

    public static void buildListUsers(List<User> users) {

        if (users.isEmpty()) {
            logger.log(Level.WARNING, "No apps found!");

        } else {

            String[][] tab = new String[users.size() + 1][6];
            tab[0][0] = "LOGIN";
            tab[0][1] = "FIRSTNAME";
            tab[0][2] = "LASTNAME";
            tab[0][3] = "EMAIL";
            tab[0][4] = "LAST CONNECTION";
            tab[0][5] = "STATUS";

            User user = null;
            for (int i = 0; i < users.size(); i++) {
                user = users.get(i);
                tab[i + 1][0] = user.getLogin();
                tab[i + 1][1] = user.getFirstName();
                tab[i + 1][2] = user.getLastName();
                tab[i + 1][3] = user.getEmail();
                tab[i + 1][4] = user.getLastConnection() != null ? DateUtils.formatDate(user.getLastConnection())
                        : "NEVER";
                tab[i + 1][5] = user.getRole().getDescription().substring(5);
            }
            printer.print(tab);
        }
    }

    public static void buildListSnapshots(List<Snapshot> snapshots) {

        if (snapshots.isEmpty()) {
            logger.log(Level.WARNING, "No snapshots found!");

        } else {

            String[][] tab = new String[snapshots.size() + 1][3];
            tab[0][0] = "TAG";
            tab[0][1] = "DATE";
            tab[0][2] = "APPLICATION SOURCE";

            Snapshot snapshot = null;
            for (int i = 0; i < snapshots.size(); i++) {
                snapshot = snapshots.get(i);
                tab[i + 1][0] = snapshot.getTag();
                tab[i + 1][1] = DateUtils.formatDate(snapshot.getDate());
                tab[i + 1][2] = snapshot.getApplicationName();

            }
            printer.print(tab);
        }
    }

    public static void buildUserMessages(List<Message> messages) {

        String[][] tab = new String[messages.size() + 1][4];
        tab[0][0] = "USER";
        tab[0][1] = "TYPE";
        tab[0][2] = "DATE";
        tab[0][3] = "EVENT";

        Message message = null;
        for (int i = 0; i < messages.size(); i++) {
            message = messages.get(i);
            tab[i + 1][0] = message.getAuthor().getFirstName() + " " + message.getAuthor().getLastName();
            tab[i + 1][1] = message.getType();
            tab[i + 1][2] = DateUtils.formatDate(message.getDate());
            tab[i + 1][3] = message.getEvent();

        }
        printer.print(tab);

    }

    public static String buildListTags(List<String> tags) {
        StringBuilder builder = new StringBuilder();
        if (tags.isEmpty()) {
            return "No tag found!";
        }
        for (String tag : tags) {
            builder.append(tags.indexOf(tag) + " - ").append(tag + "\n");
        }
        return builder.toString();
    }

    public static void buildListContainerUnits(List<ContainerUnit> containerUnits, String string,
            Application application) {
        logger.log(Level.INFO, "Available containers for application : " + application.getName());
        String[][] tab = new String[containerUnits.size() + 1][2];
        tab[0][0] = "CONTAINER NAME";
        tab[0][1] = "TYPE";

        ContainerUnit containerUnit = null;
        for (int i = 0; i < containerUnits.size(); i++) {
            containerUnit = containerUnits.get(i);
            tab[i + 1][0] = containerUnit.getName().substring((application.getUser().getFirstName()
                    + application.getUser().getLastName() + "-" + application.getName() + "-").length());
            tab[i + 1][1] = containerUnit.getType();
        }
        printer.print(tab);

    }

    public static void buildListAliases(List<String> aliases) {
        String[][] tab = new String[aliases.size() + 1][1];
        tab[0][0] = "CURRENT ALIASES";

        if (aliases.size() == 0) {
            logger.log(Level.INFO, "This application has not custom aliases");
        } else {
            for (int i = 0; i < aliases.size(); i++) {
                tab[i + 1][0] = aliases.get(i);
            }
            printer.print(tab);
        }
    }

    public static void buildListEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        String[][] tab = new String[environmentVariables.size() + 1][2];
        tab[0][0] = "CURRENT ENVIRONMENT VARIABLES";
        tab[0][1] = "VALUES";

        if (environmentVariables.size() == 0) {
            logger.log(Level.INFO, "This application has not custom environment variable");
        } else {
            for (int i = 0; i < environmentVariables.size(); i++) {
                tab[i + 1][0] = environmentVariables.get(i).getKeyEnv();
                tab[i + 1][1] = environmentVariables.get(i).getValueEnv();
            }
            printer.print(tab);
        }
    }

    public static String buildListVolumes(List<Volume> volumes) {
        StringBuilder builder = new StringBuilder(512);
        String[][] tab = new String[volumes.size() + 1][1];
        tab[0][0] = "VOLUMES NAMES";
        if (volumes.size() == 0) {
            logger.log(Level.INFO, "It has not custom volume");
        } else {
            for (int i = 0; i < volumes.size(); i++) {
                tab[i + 1][0] = volumes.get(i).getName();
                builder.append(volumes.get(i).getName()).append(":");
            }
            printer.print(tab);
        }
        logger.log(Level.INFO, builder.toString());
        return builder.toString();
    }

    public static String buildListFileUnit(List<FileUnit> fileUnits) {
        StringBuilder builder = new StringBuilder(512);
        for (FileUnit fileUnit : fileUnits) {
            if (fileUnit.getName().equalsIgnoreCase(".")) {
                continue;
            }
            builder.append("\t" + fileUnit.getName() + "\t");
        }
        logger.log(Level.INFO, builder.toString());
        return builder.toString();
    }

    public static void buildListCommands(List<Command> commands) {
        String[][] tab = new String[commands.size() + 1][3];
        tab[0][0] = "CURRENT COMMAND";
        tab[0][1] = "ARGUMENT NUMBER REQUIRED";
        tab[0][2] = "ARGUMENTS";

        if (commands.size() == 0) {
            logger.log(Level.INFO, "This application has not custom command");
        } else {
            for (int i = 0; i < commands.size(); i++) {
                tab[i + 1][0] = commands.get(i).getName();
                tab[i + 1][1] = commands.get(i).getArgumentNumber().toString();
                String arguments = "";
                for (String argument : commands.get(i).getArguments())
                    arguments = arguments + argument + " ";
                arguments = arguments.substring(0, arguments.length() - 1);
                tab[i + 1][2] = arguments;
            }
            printer.print(tab);
        }
    }

    public static void buildListContainers(List<String> containers) {
        String[][] tab = new String[containers.size() + 1][1];
        tab[0][0] = "CONTAINER NAME";

        if (containers.size() == 0) {
            logger.log(Level.INFO, "This application has not container");
        } else {
            for (int i = 0; i < containers.size(); i++) {
                tab[i + 1][0] = containers.get(i);
            }
            printer.print(tab);
        }
    }

}
