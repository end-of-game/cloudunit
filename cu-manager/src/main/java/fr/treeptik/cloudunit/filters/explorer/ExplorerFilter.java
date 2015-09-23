package fr.treeptik.cloudunit.filters.explorer;

import fr.treeptik.cloudunit.json.ui.FileUnit;

/**
 * Created by nicolas on 08/06/15.
 */
public interface ExplorerFilter {
    public boolean isValid(FileUnit fileUnit);

    public void isRemovable(FileUnit fileUnit);

    public void isSafe(FileUnit fileUnit);
}
