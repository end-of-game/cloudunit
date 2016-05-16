package fr.treeptik.cloudunit.utils;

/**
 * Created by nicolas on 13/05/2016.
 */
public class JvmOptionsUtils {

    public final static String CLOUDUNIT_SHARED_DIR = "-Dcloudunit.shared=";

    public static String extractDirectory(String jvmOptions) {
        if (jvmOptions == null || jvmOptions.trim().length() == 0) { return null; }
        if (jvmOptions.indexOf(CLOUDUNIT_SHARED_DIR) == -1) { return null; }
        // we get cloudunit.shared=...
        String subStringFromStart = jvmOptions.substring(jvmOptions.indexOf(CLOUDUNIT_SHARED_DIR)+2);
        if (subStringFromStart.indexOf("-D") != -1) {
            subStringFromStart = subStringFromStart.substring(0, subStringFromStart.indexOf("-D"));
        }
        String finalValue = subStringFromStart.substring(subStringFromStart.indexOf("=")+1);
        if (finalValue != null) { finalValue = finalValue.trim(); }
        if (finalValue != null && finalValue.length() == 0) { finalValue = null; }
        return finalValue;
    }
}
