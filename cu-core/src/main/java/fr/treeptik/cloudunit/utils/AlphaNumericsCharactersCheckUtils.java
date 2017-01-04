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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

/**
 * TODO : Replace with Apache StringUTils
 */
public class AlphaNumericsCharactersCheckUtils {

    private static Logger logger = LoggerFactory
        .getLogger(AlphaNumericsCharactersCheckUtils.class);

    public static String convertToAlphaNumerics(String value) {
        logger.debug("Before : " + value);
        value = Normalizer.normalize(value, Form.NFD);
        value = value.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        value = value.replaceAll("[^-_a-zA-Z0-9\\s]", "").replace(" ", "");
        logger.debug("After : " + value);
        return value;
    }

    public static String deAccent(String value) {
        logger.debug("Before : " + value);
        String nfdNormalizedString = Normalizer.normalize(value, Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        value = pattern.matcher(nfdNormalizedString).replaceAll("");
        logger.debug("After : " + value);
        return value;
    }

}
