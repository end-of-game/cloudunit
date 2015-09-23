package fr.treeptik.cloudunit.filters.explorer;

import fr.treeptik.cloudunit.json.ui.FileUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used into FileExplorer feature
 * to display or hide paths for users.
 */
public class TomcatFilter implements ExplorerFilter {

    private final Logger logger = LoggerFactory.getLogger(TomcatFilter.class);

    @Override
    public boolean isValid(FileUnit fileUnit) {

        if (logger.isDebugEnabled()) {
            logger.debug("breadcrump : " + fileUnit.getBreadcrump());
        }

        if (!fileUnit.isDir()) { return true; }

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
            logger.debug("breadcrump : " + fileUnit.getBreadcrump());
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
            logger.debug("breadcrump : " + fileUnit.getBreadcrump());
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
