/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.exception.CheckException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by nicolas on 18/08/2014.
 */
@SuppressWarnings("ALL")
public class CheckUtils {

    private static final List<String> listJvmMemoriesAllowed = Arrays.asList("512", "1024", "2048", "3072");

    private static final List<String> listJvmReleaseAllowed = Arrays.asList("jdk1.7.0_55", "jdk1.8.0_25");

    private static MessageSource messageSource = (MessageSource) StaticSpringApplicationContext.getBean("messageSource");

    /**
     * Valid classic input
     *
     * @param field
     * @param message
     * @throws CheckException
     */
    public static void validateInput(String field, String message)
        throws CheckException {
        if (field == null
            || field.trim().length() == 0
            || "undefined".equals(field)
            || field.length() > 15) {
            String messageTranslated = messageSource.getMessage(field, null, Locale.ENGLISH);
            throw new CheckException(messageTranslated);
        }
    }

    /**
     * Valid Classic + Syntax input
     *
     * @param field
     * @param message
     * @throws CheckException
     */
    public static void validateSyntaxInput(String field, String message)
        throws CheckException {
        if (field == null
            || field.trim().length() == 0
            || "undefined".equals(field)
            || field.length() > 15
            || !StringUtils.isAlphanumeric(field)) {
            String messageTranslated = messageSource.getMessage(field, null, Locale.ENGLISH);
            throw new CheckException(messageTranslated);
        }
    }

    /**
     * Verify the input for the jvm options
     *
     * @param jvmOpts
     * @param jvmMemory
     * @param jvmRelease
     * @throws CheckException
     */
    public static void checkJavaOpts(String jvmOpts, String jvmMemory, String jvmRelease)
        throws CheckException {

        if (jvmOpts.toLowerCase().contains("xms")
            || jvmOpts.toLowerCase().contains("xmx")) {
            throw new CheckException("You are not allowed to change memory with java opts");
        }

        if (!listJvmMemoriesAllowed.contains(jvmMemory)) {
            throw new CheckException("You are not allowed to set this jvm memory size : [" + jvmMemory + "]");
        }

        if (!listJvmReleaseAllowed.contains(jvmRelease)) {
            throw new CheckException("You are not allowed to set this jvm release : [" + jvmRelease + "]");
        }
    }

}

