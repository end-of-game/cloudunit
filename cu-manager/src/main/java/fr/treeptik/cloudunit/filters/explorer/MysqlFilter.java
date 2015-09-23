package fr.treeptik.cloudunit.filters.explorer;

/**
 * Created by nicolas on 08/06/15.
 */

import fr.treeptik.cloudunit.json.ui.FileUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicolas on 08/06/15.
 */
public class MysqlFilter implements ExplorerFilter {

    private final Logger logger = LoggerFactory.getLogger(MysqlFilter.class);

    @Override
    public boolean isValid(FileUnit fileUnit) {

        // On laisse passer tous les fichiers.
        // la sélection a été fait en aval
        if (!fileUnit.isDir())
            return true;

        if (logger.isDebugEnabled()) {
            logger.debug("--> fileUnit : " + fileUnit.getBreadcrump());
        }

        String breadcrumb = fileUnit.getBreadcrump();
        if (breadcrumb.equalsIgnoreCase("/cloudunit")
                || breadcrumb.startsWith("/cloudunit/backup")
                || breadcrumb.startsWith("/cloudunit/tmp")
                || breadcrumb.equalsIgnoreCase("/etc")
                || breadcrumb.equalsIgnoreCase("/var")
                || breadcrumb.startsWith("/etc/mysql")
                || breadcrumb.startsWith("/var/log")) {
            return true;
        }

        return false;
    }

    @Override
    public void isRemovable(FileUnit fileUnit) {

        if (logger.isDebugEnabled()) {
            logger.debug("--> fileUnit : " + fileUnit.getBreadcrump());
        }

        String breadcrumb = fileUnit.getBreadcrump();
        if (breadcrumb.equalsIgnoreCase("/cloudunit")
                || breadcrumb.equalsIgnoreCase("/cloudunit/backup")
                || breadcrumb.equalsIgnoreCase("/cloudunit/tmp")
                || breadcrumb.equalsIgnoreCase("/etc")
                || breadcrumb.equalsIgnoreCase("/etc/mysql")) {
            fileUnit.removable(false);
        } else {
            fileUnit.removable(true);
        }
    }

    @Override
    public void isSafe(FileUnit fileUnit) {
        if (logger.isDebugEnabled()) {
            logger.debug("--> fileUnit : " + fileUnit.getBreadcrump());
        }

        String breadcrumb = fileUnit.getBreadcrump();
        if (breadcrumb.equalsIgnoreCase("/cloudunit")
                || breadcrumb.equalsIgnoreCase("/cloudunit/backup")
                || breadcrumb.equalsIgnoreCase("/cloudunit/appconf")
                || breadcrumb.equalsIgnoreCase("/cloudunit/binaries/lib")) {
            fileUnit.safe(true);
        } else {
            fileUnit.safe(false);
        }
    }
}
