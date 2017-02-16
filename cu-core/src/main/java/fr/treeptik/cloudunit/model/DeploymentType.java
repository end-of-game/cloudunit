package fr.treeptik.cloudunit.model;/*
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

import java.util.EnumSet;

import org.apache.commons.io.FilenameUtils;

public enum DeploymentType {
    WAR("war"),
    EAR("ear"),
    JAR("jar"),
    JAVA("java"),
    GROOVY("groovy"),
    RUBY("rb"),
    JAVASCRIPT("js");

    private String extension;

    DeploymentType(String extension) {
        this.extension = extension;
    }

    public static DeploymentType from(String name) {
        return EnumSet.allOf(DeploymentType.class).stream()
                .filter(t -> FilenameUtils.getExtension(name).equalsIgnoreCase(t.extension))
                .findFirst()
                .orElse(DeploymentType.JAR);
    }
    
    public String getContextPath(String filename) {
        String basename = FilenameUtils.getBaseName(filename);
        if (this == WAR && !"ROOT".equals(basename)) {
            return "/" + basename;
        } else {
            return "/";
        }
    }
}