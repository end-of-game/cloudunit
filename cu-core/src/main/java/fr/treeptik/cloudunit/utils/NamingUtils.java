package fr.treeptik.cloudunit.utils;

import org.apache.commons.io.FilenameUtils;

import java.util.function.Function;

/**
 * Utils for all naming into Application
 *
 * Created by nicolas on 26/09/2016.
 */
public class NamingUtils {

    /**
     * Define the rule for Application Context.
     * If war is ROOT.war by convention, Context is /
     * Else the context is the war name without extension
     */
    public static Function<String, String> getContext =
            s -> s.toUpperCase().trim().equalsIgnoreCase("ROOT.war") ? "/" : ("/"+ FilenameUtils.getBaseName(s));


}
