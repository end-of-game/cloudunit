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
        return f -> f.toLowerCase().endsWith(extension);
    }

    /**
     * Evaluate the context to display in UI in function the file deployed.
     * ROOT.war and all .jar don't have specific context
     */
    private static Predicate<String> evaluateCtx = s -> fileEndsWith("ROOT.war").or(fileEndsWith(".jar")).test(s);

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
     * To decide if we use unix or http  mode to access docker socket
     */
    public static Function<Boolean, String> protocolSocket
            = isUnix -> isUnix.booleanValue() ? "unix" : "http";
}
