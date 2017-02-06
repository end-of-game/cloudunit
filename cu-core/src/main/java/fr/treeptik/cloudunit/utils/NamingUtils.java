package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.model.Application;
import org.apache.commons.io.FilenameUtils;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utils for all naming into Application
 *
 * Created by nicolas on 26/09/2016.
 */
public class NamingUtils {

    /**
     * Test the suffix for a String without case
     * @param extension
     * @return
     */
    private static Predicate<String> fileEndsWith(String extension) {
        return f -> f.toLowerCase().endsWith(extension.toLowerCase());
    }

    /**
     * Evaluate the context to display in UI in function the file deployed.
     * ROOT.war and all .jar don't have specific context
     */
    private static Predicate<String> evaluateCtx =
            s -> fileEndsWith("ROOT.war")
                    .or(fileEndsWith(".jar")
                            .or(fileEndsWith(".java"))
                            .or(fileEndsWith(".rb"))
                            .or(fileEndsWith(".groovy"))
                            .or(fileEndsWith(".js"))).test(s);

    /**
     * Define the rule for Application Context.
     * If war is ROOT.war by convention, Context is /
     * Else the context is the war name without extension
     */
    public static Function<String, String> getContext =
            s ->  evaluateCtx.test(s) ? "/" : ("/"+ FilenameUtils.getBaseName(s));

    /**
     * Create the alias name for open port feature
    */
    public final static String getAliasForOpenPortFeature(String applicationName, String userLogin, Integer port, String domainName){
        return applicationName + "-" + userLogin + "-" + "forward-" + port + domainName;
    }

    /**
     * Create the container name
     */
    public final static String getContainerName(String applicationName, String nature, String userLogin){
        String value = "error";
        if (nature != null && !nature.isEmpty()) {
            if (nature.indexOf("-") != -1) {
                nature = nature.substring(0, nature.indexOf("-"));
            }
            value = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(applicationName) + "-" + nature + "-" + userLogin;
        } else {
            value = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(applicationName) + "-" + userLogin;
        }
        return value.toLowerCase();
    }

    /**
     * To decide if we use unix or http/https  mode to access docker socket
     */
    public static String getProtocolSocket(boolean isUnix, String mode) {
        if (isUnix) {
            return "unix";
        } else {
            return mode;
        }
    }

    /**
     * Normalize the domain name
     */
    public static String getCloudUnitDomain(String input) {
        if (input == null || input.isEmpty()) {
            return ".cloudunit.dev";
        } else {
            if (!input.startsWith(".")) {
                input = "." + input;
            }
            return input;
        }
    }
}
