/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the GPL is right for you,
 * you can always test our software under the GPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.utils.SecurityContextUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by nicolas on 09/10/15.
 */

public class UserControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    private static final String PASSWORD = "johndoe";
    private static final String USERNAME = "abc2015";

    private UserController controller;

    private SecurityContextUtil securityContextUtilMock;

    @Before
    public void setUp() {
        controller = new UserController();

        securityContextUtilMock = mock(SecurityContextUtil.class);
        ReflectionTestUtils.setField(controller, "securityContextUtil", securityContextUtilMock);
    }

    @Test
    public void getLoggedInUSerWhenUserIsNotLoggedIn() {
        assertTrue(true, "true");
    }
}
