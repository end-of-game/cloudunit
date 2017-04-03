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

import static java.lang.System.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.model.Volume;

public class MessageConverter {

    private static ShellRowFormater printer = new ShellRowFormater(out);

    private static Logger logger = Logger.getLogger("MessageConverter");

    public static void buildLightModuleMessage(Application application) {

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
}
