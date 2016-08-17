/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
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

package fr.treeptik.cloudunit.cli.utils;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public final class ShellRowFormater {

    private static final String DEFAULT_AS_NULL = "(NULL)";

    private static final char BORDER_KNOT = '+';
    private static final char HORIZONTAL_BORDER = '-';
    private static final char VERTICAL_BORDER = '|';
    private final String asNull;
    private Logger logger = Logger.getLogger("PrettyPrinter");

    public ShellRowFormater(PrintStream out) {
        this(out, DEFAULT_AS_NULL);
    }

    public ShellRowFormater(PrintStream out, String asNull) {
        if (out == null) {
            throw new IllegalArgumentException("No print stream provided");
        }
        if (asNull == null) {
            throw new IllegalArgumentException(
                    "No NULL-value placeholder provided");
        }

        this.asNull = asNull;
    }

    private static String padRight(String s, int n) {
        return format("%1$-" + n + "s", s);
    }

    private static String safeGet(String[] array, int index, String defaultValue) {
        return index < array.length ? array[index] : defaultValue;
    }

    public void print(String[][] table) {
        if (table == null) {
            logger.log(Level.SEVERE, "An error has occured");
        }
        if (table.length == 0) {
            return;
        }
        final int[] widths = new int[getMaxColumns(table)];
        adjustColumnWidths(table, widths);
        printPreparedTable(table, widths, getHorizontalBorder(widths));

    }

    private void printPreparedTable(String[][] table, int widths[],
                                    String horizontalBorder) {
        final int lineLength = horizontalBorder.length();
        logger.log(Level.INFO, horizontalBorder);
        for (final String[] row : table) {
            if (row != null) {
                logger.log(Level.INFO, getRow(row, widths, lineLength));
                logger.log(Level.INFO, horizontalBorder);
            }
        }
    }

    private String getRow(String[] row, int[] widths, int lineLength) {
        final StringBuilder builder = new StringBuilder(lineLength)
                .append(VERTICAL_BORDER);
        final int maxWidths = widths.length;
        for (int i = 0; i < maxWidths; i++) {
            builder.append(
                    padRight(getCellValue(safeGet(row, i, null)), widths[i]))
                    .append(VERTICAL_BORDER);
        }
        return builder.toString();
    }

    private String getHorizontalBorder(int[] widths) {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(BORDER_KNOT);
        for (final int w : widths) {
            for (int i = 0; i < w; i++) {
                builder.append(HORIZONTAL_BORDER);
            }
            builder.append(BORDER_KNOT);
        }
        return builder.toString();
    }

    private int getMaxColumns(String[][] rows) {
        int max = 0;
        for (final String[] row : rows) {
            if (row != null && row.length > max) {
                max = row.length;
            }
        }
        return max;
    }

    private void adjustColumnWidths(String[][] rows, int[] widths) {
        for (final String[] row : rows) {
            if (row != null) {
                for (int c = 0; c < widths.length; c++) {
                    final String cv = getCellValue(safeGet(row, c, asNull));
                    final int l = cv.length();
                    if (widths[c] < l) {
                        widths[c] = l;
                    }
                }
            }
        }
    }

    private String getCellValue(Object value) {
        return value == null ? asNull : value.toString();
    }
}
