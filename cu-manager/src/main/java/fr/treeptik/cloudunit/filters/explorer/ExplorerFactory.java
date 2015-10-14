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

package fr.treeptik.cloudunit.filters.explorer;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for FileExplorer Filters
 */
public class ExplorerFactory {

    private static final ExplorerFactory ourInstance = new ExplorerFactory();

    private static final Map<String, ExplorerFilter> filters = new HashMap<>();

    static {
        ExplorerFactory.filters.put("tomcat", new TomcatFilter());
        ExplorerFactory.filters.put("mysql", new MysqlFilter());
        ExplorerFactory.filters.put("postgres", new PostgresFilter());
        ExplorerFactory.filters.put("mongo", new MongoFilter());
        ExplorerFactory.filters.put("jboss", new JBossFilter());
    }

    private ExplorerFactory() {
    }

    public static ExplorerFactory getInstance() {
        return ExplorerFactory.ourInstance;
    }

    /**
     * Retourne l'implementation sur base du nom.
     *
     * @param name
     * @return
     */
    public ExplorerFilter getCustomFilter(String name) {
        if (name.contains("tomcat")) {
            name = "tomcat";
        }
        if (name.contains("mysql")) {
            name = "mysql";
        }
        if (name.contains("mongo")) {
            name = "mongo";
        }
        if (name.contains("postgres")) {
            name = "postgres";
        }
        if (name.contains("jboss")) {
            name = "jboss";
        }
        return filters.get(name);
    }
}
