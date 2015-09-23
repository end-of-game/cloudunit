package fr.treeptik.cloudunit.filters.explorer;

import fr.treeptik.cloudunit.json.ui.FileUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicolas on 08/06/15.
 */
public class JBossFilter implements ExplorerFilter {

    private final Logger logger = LoggerFactory.getLogger(JBossFilter.class);

    @Override
    public boolean isValid(FileUnit fileUnit) {

        if (logger.isDebugEnabled()) {
            logger.debug("--> fileUnit : " + fileUnit.getBreadcrump());
        }

        // On laisse passer tous les fichiers.
        // la sélection a été fait en aval
        if (!fileUnit.isDir())
            return true;

        String breadcrumb = fileUnit.getBreadcrump();
        if (breadcrumb.equalsIgnoreCase("/cloudunit")
                || breadcrumb.startsWith("/cloudunit/backup")
                || breadcrumb.startsWith("/cloudunit/appconf")
                || breadcrumb.startsWith("/cloudunit/binaries")
                || breadcrumb.startsWith("/cloudunit/tmp")) {
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
                || breadcrumb.equalsIgnoreCase("/cloudunit/appconf")
                || breadcrumb.equalsIgnoreCase("/cloudunit/binaries")
                || breadcrumb.equalsIgnoreCase("/cloudunit/binaries/lib")
                || breadcrumb.startsWith("/cloudunit/binaries/bin")) {
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
                || breadcrumb.startsWith("/cloudunit/appconf")) {
            fileUnit.safe(true);
        } else {
            fileUnit.safe(false);
        }
    }
}
