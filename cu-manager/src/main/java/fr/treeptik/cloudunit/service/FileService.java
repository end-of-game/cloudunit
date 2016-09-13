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

package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.dto.LogLine;
import fr.treeptik.cloudunit.dto.SourceUnit;
import fr.treeptik.cloudunit.exception.ServiceException;

import java.io.File;
import java.util.List;

/**
 * Created by nicolas on 20/05/15.
 */
public interface FileService {

    List<FileUnit> listByContainerIdAndPath(String containerId, String path)
        throws ServiceException;

    List<SourceUnit> listLogsFilesByContainer(String containerId)
        throws ServiceException;

    void sendFileToContainer(String applicationName, String containerId,
                             File file, String originalName, String destFile)
        throws ServiceException;

    File getFileFromContainer(String applicationName, String containerId,
                                        File file, String originalName, String destFile)
        throws ServiceException;

    void deleteFilesFromContainer(String applicationName, String containerId, String path)
        throws ServiceException;

    void createDirectory(String applicationName, String containerId, String path)
            throws ServiceException;

    public List<LogLine> catFileForNLines(String containerId, String file, Integer nbRows)
            throws ServiceException;

    public String getDefaultLogFile(String containerId)
            throws ServiceException;
}
