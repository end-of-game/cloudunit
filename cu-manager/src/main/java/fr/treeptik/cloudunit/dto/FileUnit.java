/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.dto;

/**
 * Created by nicolas on 20/05/15.
 */
public class FileUnit {

    private String name;

    private String user;

    private String day;

    private String month;

    private String hour;

    // safe property means the resource will be saved during cloning
    private boolean safe;

    private boolean dir;

    private boolean exec;

    private boolean isRemovable;

    private String breadcrumb;

    public FileUnit(String name, String user, String day, String month, String hour, boolean safe, boolean dir,
                    boolean exec, String breadcrumb) {
        this.name = name;
        this.user = user;
        this.day = day;
        this.month = month;
        this.hour = hour;
        this.safe = safe;
        this.exec = exec;
        this.dir = dir;
        this.breadcrumb = breadcrumb;
    }

    public boolean isDir() {
        return dir;
    }

    public boolean isExec() {
        return exec;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getHour() {
        return hour;
    }

    public boolean isSafe() {
        return safe;
    }

    public void safe(boolean safe) {
        this.safe = safe;
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public void removable(boolean isRemovable) {
        this.isRemovable = isRemovable;
    }

    public String getBreadcrump() {
        return breadcrumb;
    }

    @Override
    public String toString() {
        return "FileUnit{" +
            "name='" + name + '\'' +
            ", user='" + user + '\'' +
            ", day='" + day + '\'' +
            ", month='" + month + '\'' +
            ", hour='" + hour + '\'' +
            ", safe=" + safe +
            ", dir=" + dir +
            ", exec=" + exec +
            ", isRemovable=" + isRemovable +
            ", breadcrumb='" + breadcrumb + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileUnit)) return false;

        FileUnit fileUnit = (FileUnit) o;

        if (this.safe != fileUnit.safe) return false;
        if (this.dir != fileUnit.dir) return false;
        if (this.exec != fileUnit.exec) return false;
        if (this.isRemovable != fileUnit.isRemovable) return false;
        if (this.name != null ? !this.name.equals(fileUnit.name) : fileUnit.name != null) return false;
        if (this.user != null ? !this.user.equals(fileUnit.user) : fileUnit.user != null) return false;
        if (this.day != null ? !this.day.equals(fileUnit.day) : fileUnit.day != null) return false;
        if (this.month != null ? !this.month.equals(fileUnit.month) : fileUnit.month != null) return false;
        if (this.hour != null ? !this.hour.equals(fileUnit.hour) : fileUnit.hour != null) return false;
        return !(breadcrumb != null ? !breadcrumb.equals(fileUnit.breadcrumb) : fileUnit.breadcrumb != null);

    }

    @Override
    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.user != null ? this.user.hashCode() : 0);
        result = 31 * result + (this.day != null ? this.day.hashCode() : 0);
        result = 31 * result + (this.month != null ? this.month.hashCode() : 0);
        result = 31 * result + (this.hour != null ? this.hour.hashCode() : 0);
        result = 31 * result + (this.safe ? 1 : 0);
        result = 31 * result + (this.dir ? 1 : 0);
        result = 31 * result + (this.exec ? 1 : 0);
        result = 31 * result + (this.isRemovable ? 1 : 0);
        result = 31 * result + (this.breadcrumb != null ? this.breadcrumb.hashCode() : 0);
        return result;
    }
}
