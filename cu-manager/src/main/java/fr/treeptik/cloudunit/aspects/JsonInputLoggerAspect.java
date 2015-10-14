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


package fr.treeptik.cloudunit.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicolas on 02/10/15.
 *
 * DISABLED AT THE MOMENT.
 */
//@Component
//@Aspect
public class JsonInputLoggerAspect {

    // Before methods
    @Before("execution(* fr.treeptik.cloudunit.controller.*.*(fr.treeptik.cloudunit.dto.JsonInput))")
    public void traceAll(JoinPoint joinPoint) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getClass());
        logger.debug(joinPoint.getArgs()[0].toString());
    }

}
