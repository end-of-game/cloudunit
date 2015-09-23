package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.exception.CheckException;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nicolas on 18/08/2014.
 */
public class CheckUtils {

	private static final List<String> listJvmMemoriesAllowed = Arrays.asList("512", "1024", "2048", "3072");

	/**
	 * Valid classic input
	 * @param field
	 * @param message
	 * @throws CheckException
	 */
	public static void validateInput(String field, String message)
			throws CheckException {
		if (field == null
				|| field.trim().length() == 0
				|| "undefined".equals(field)
				|| field.length() > 25) {
			throw new CheckException(message + " : " + field);
		}
	}

    /**
     * Valid Classic + Syntax input
     * @param field
     * @param message
     * @throws CheckException
     */
    public static void validateSyntaxInput(String field, String message)
            throws CheckException {
        if (field == null
                || field.trim().length() == 0
                || "undefined".equals(field)
                || field.length() > 25
                || !StringUtils.isAlphanumeric(field)) {
            throw new CheckException(message + " : " + field);
        }
    }

	/**
	 * Verify the input for the jvm options
	 *
	 * @param opts
	 * @param jvmMemory
	 * @param jvmRelease
	 * @throws CheckException
	 */
	public static void checkJavaOpts(String opts, String jvmMemory, String jvmRelease) throws CheckException {
		if (opts.toLowerCase().contains("xms")
				|| opts.toLowerCase().contains("xmx")) {
			throw new CheckException(
					"You are not allowed to change memory with java opts");
		}

		if (!listJvmMemoriesAllowed.contains(jvmMemory)) {
			throw new CheckException("You are not allowed to set this jvm memory size : " + jvmMemory);
		}

		// todo : test the jvm release
	}

}

