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

package fr.treeptik.cloudunit.json.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nicolas on 25/08/2014.
 */
public class LogUnit {

    private static SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private String source;

    private String level;

    private String date;

    private String message;

    public LogUnit(final String source, final String date, final String message) {
        this.message = message;
        if (source != null && source.contains("/")) {
            this.source = source.substring(source.lastIndexOf("/") + 1);
        } else {
            this.source = source;
        }
        // TODO : BEGIN : Virer cette horreur pour que le plugin GROK de LogStash fasse le taf
        try {
            if (date != null && date.trim().length() > 0) {
                Date tempDate = simpleDateFormatFrom.parse(date.replaceAll("Z$", "+0000"));
                this.date = simpleDateFormatTo.format(tempDate);
            }
        } catch (Exception ignore) {
        }

        // TODO : BEGIN : Virer cette horreur pour que le plugin GROK de LogStash fasse le taf
        /*
        if (message != null) {
            if (message.startsWith("INFO:")) {
                this.level = "INFO:";
                this.message = message.substring(5);
            } else if (message.startsWith("WARN:")) {
                this.level = "WARN:";
                this.message = message.substring(5);
            } else if (message.startsWith("DEBUG:")) {
                this.level = "DEBUG:";
                this.message = message.substring(6);
            } else if (message.startsWith("ERROR:")) {
                this.level = "ERROR:";
                this.message = message.substring(6);
            } else {
                this.level = "INFO";
            }
        }
        */
        // TODO : END : Virer cette horreur pour que le plugin GROK de LogStash fasse le taf

    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }

    public String getSource() {
        return source;
    }

}
