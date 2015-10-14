/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the GPL is right for you,
 * you can always test our software under the GPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.net.URL;

/**
 * Class with utilities method for testing url and application deployment
 */
public class TestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    /**
     * Return the content of an URL
     *
     * @param url
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String getUrlContentPage(String url)
        throws ParseException, IOException {
        HttpGet request = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * Download from github binaries and deploy file
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static MockMultipartFile downloadAndPrepareFileToDeploy(String remoteFile, String path)
        throws IOException {
        URL url;
        OutputStream outputStream = null;
        File file = new File(remoteFile);
        try {
            url = new URL(path);
            InputStream input = url.openStream();

            outputStream = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append(remoteFile);
            msgError.append(",");
            msgError.append(path);
            logger.debug(msgError.toString(), e);
        } finally {
            outputStream.close();
        }
        return new MockMultipartFile("file", file.getName(), "multipart/form-data", new FileInputStream(file));

    }
}
