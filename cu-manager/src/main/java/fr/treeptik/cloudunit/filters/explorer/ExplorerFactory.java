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
        if (name.contains("tomcat")) { name = "tomcat"; }
        if (name.contains("mysql")) {  name = "mysql"; }
        if (name.contains("mongo")) {  name = "mongo"; }
        if (name.contains("postgres")) {  name = "postgres"; }
        if (name.contains("jboss")) {  name = "jboss"; }
        return filters.get(name);
    }
}
