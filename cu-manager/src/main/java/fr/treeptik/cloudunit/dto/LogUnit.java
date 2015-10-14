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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nicolas on 25/08/2014.
 */
public class LogUnit {

    private static SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private String source;

    private String date;

    private String message;

    public LogUnit(final String source, final String date, final String message) {

        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        this.message = message;
        if (source != null && source.contains("/")) {
            this.source = source.substring(source.lastIndexOf("/") + 1);
        } else {
            this.source = source;
        }
        try {
            if (date != null && date.trim().length() > 0) {
                Date tempDate = simpleDateFormatFrom.parse(date.replaceAll("Z$", "+0000"));
                this.date = simpleDateFormatTo.format(tempDate);
            }
        } catch (Exception ignore) {
        }
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "LogUnit{" +
            "source='" + source + '\'' +
            ", date='" + date + '\'' +
            ", message='" + message + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogUnit)) return false;

        LogUnit logUnit = (LogUnit) o;

        if (this.source != null ? !this.source.equals(logUnit.source) : logUnit.source != null) return false;
        if (this.date != null ? !this.date.equals(logUnit.date) : logUnit.date != null) return false;
        return !(message != null ? !message.equals(logUnit.message) : logUnit.message != null);
    }

    @Override
    public int hashCode() {
        int result = this.source != null ? this.source.hashCode() : 0;
        result = 31 * result + (this.date != null ? this.date.hashCode() : 0);
        result = 31 * result + (this.message != null ? this.message.hashCode() : 0);
        return result;
    }
}
