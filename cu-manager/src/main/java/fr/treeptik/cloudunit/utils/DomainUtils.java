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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mathieu on 08/01/16.
 */
public class DomainUtils {

    /**
     * Domain Name Regular Expression Pattern.
     * <br>
     * Above pattern makes sure domain name matches the following criteria :
     * <ul>
     *      <li>The domain name should be a-z | A-Z | 0-9 and hyphen(-)</li>
     *      <li>Last tld must be at least two characters, and a maximum of 6 characters</li>
     *      <li>The domain name should not start or end with hyphen (-) (e.g. -google.com or google-.com)</li>
     *      <li>The domain name can be a subdomain (e.g. treeptik.corp.eu)</li>
     * </ul>
     */
    private static final String DOMAIN_NAME_PATTERN = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";

    /**
     * Validate if domain name is correct.
     *
     * @param domainName the domain name to check
     * @return {@code True} only if the domain name matches with the domain name pattern
     */
    public static boolean isValidDomainName(String domainName) {
        Pattern p = Pattern.compile(DOMAIN_NAME_PATTERN);
        Matcher m = p.matcher(domainName);
        return m.matches();
    }

}
