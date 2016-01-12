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

package fr.treeptik.cloudunit.utils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

/**
 * Created by mathieu on 08/01/16.
 */
@RunWith(value = Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
public class DomainUtilsTest {

    private String domain;
    private boolean expected;

    public DomainUtilsTest(String domain, boolean expected) {
        this.domain = domain;
        this.expected = expected;
    }

    @Parameters(name = "{index}: isValid({0})={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                        {"www.treeptik.fr", true},
                        {"treeptik.fr", true},
                        {"treeptik123.fr", true},
                        {"treeptik-info.fr", true},
                        {"sub.treeptik.fr", true},
                        {"sub.treeptik-info.fr", true},
                        {"treeptik.fr.eu", true},
                        {"sub.treeptik.fr", true},
                        {"sub.sub.treeptik.fr", true},
                        {"t.fr", true},
                        {"treeptik.t.t.co", true},
                        {"treeptik.t.t.c", false},      // Tld must at between 2 and 6 long
                        {"treeptik,fr", false},         // Comma not allowed
                        {"treeptik", false},            // No tld
                        {"treeptik.123", false},        // Digit not allowed in tld
                        {".fr", false},                 // Must start with [A-Za-z0-9]
                        {"treeptik.a", false},          // Last tld need at least two characters
                        {"treeptik.fr/users", false},   // No tld
                        {"-treeptik.fr", false},        // Cannot begin with a hyphen -
                        {"treeptik-.fr", false},        // Cannot end with a hyphen -
                        {"sub.-treeptik.fr", false},    // Cannot begin with a hyphen -
                        {"sub.treeptik-.fr", false}     // Cannot end with a hyphen -
                }
        );
    }

    @Test
    public void test_validDomains() {
        Assert.assertEquals(expected, DomainUtils.isValidDomainName(domain));
    }
}
