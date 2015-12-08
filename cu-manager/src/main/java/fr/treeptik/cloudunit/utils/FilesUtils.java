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

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.model.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FilesUtils {

    public static String[] suffixesDeployment = {".war", ".ear", ".jar"};

    public static String[] notAllowed = {".docker", "init-service-ok"};

    public static Boolean isNotAuthorizedExtension(String filename) {
        if (filename != null) {
            filename = filename.trim();
        }
        for (String token : notAllowed) {
            if (filename != null && filename.startsWith(token) || filename.endsWith(token)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isAuthorizedFileForDeployment(final String filename) {
        for (String suffix : suffixesDeployment) {
            if (filename.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create an upload directory
     *
     * @param server
     * @param uploadDir
     * @throws DockerJSONException
     */
    public static void createUploadDir(Server server, String uploadDir)
        throws DockerJSONException {
        File uploadFolder = new File(uploadDir + "/uploadDir_"
            + server.getContainerID());
        if (!uploadFolder.exists()) {
            uploadFolder.mkdir();
        }
    }

    public static int count(InputStream is)
        throws IOException {
        byte[] c = new byte[1024];
        int count = 0;
        int readChars = 0;
        boolean empty = true;
        while ((readChars = is.read(c)) != -1) {
            empty = false;
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == '\n') {
                    ++count;

                }
            }
        }
        return (count == 0 && !empty) ? 1 : count;
    }

    public static void deleteDirectory(File file)
        throws IOException {

        if (file.isDirectory()) {

            // directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();

            } else {

                // list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    // construct the file structure
                    File fileDelete = new File(file, temp);

                    // recursive delete
                    deleteDirectory(fileDelete);
                }

                // check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }

        } else {
            // if file, then delete it
            file.delete();
        }
    }

    public static String setSuffix(String fileName) {
        if (!fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }
}
