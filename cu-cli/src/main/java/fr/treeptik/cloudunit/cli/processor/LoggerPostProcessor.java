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

package fr.treeptik.cloudunit.cli.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

@Component
public class LoggerPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(final Object bean,
                                                  String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(),
                new ReflectionUtils.FieldCallback() {

                    public void doWith(Field field)
                            throws IllegalArgumentException,
                            IllegalAccessException {

                        if (field.getAnnotation(InjectLogger.class) != null
                                && field.getType().equals(Logger.class)) {
                            ReflectionUtils.makeAccessible(field);
                            Logger logger = Logger.getLogger(bean.getClass()
                                    .getName());

                            field.set(bean, logger);

                            StreamHandler fileHandler;
                            try {

                                FileOutputStream fileOutputStream = new FileOutputStream(
                                        new File("errors.log"), true);
                                fileHandler = new StreamHandler(
                                        fileOutputStream, new SimpleFormatter());
                                fileHandler.setLevel(Level.SEVERE);
                                logger.addHandler(fileHandler);

                            } catch (SecurityException | IOException e) {
                                throw new IllegalArgumentException(e);
                            }

                        }
                    }
                });

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }
}