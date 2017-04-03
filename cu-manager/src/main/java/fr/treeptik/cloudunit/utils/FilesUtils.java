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

import java.io.*;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.model.Server;

public class FilesUtils {

	private static Logger logger = LoggerFactory.getLogger(FilesUtils.class);

	public static String[] suffixesDeployment = { ".war", ".ear", ".jar", ".rb", ".js", ".java", ".groovy" };

	public static String[] notAllowed = { ".docker", "init-service-ok" };

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
	public static void createUploadDir(Server server, String uploadDir) throws DockerJSONException {
		File uploadFolder = new File(uploadDir + "/uploadDir_" + server.getContainerID());
		if (!uploadFolder.exists()) {
			uploadFolder.mkdir();
		}
	}

	public static int count(InputStream is) throws IOException {
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

	public static void deleteDirectory(File file) throws IOException {

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

	/**
	 * Untar
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 * @return The {@link List} of {@link File}s with the untared content.
	 * @throws ArchiveException
	 */
	public static void unTar(final InputStream is, final OutputStream outputFileStream)
			throws FileNotFoundException, IOException, ArchiveException {
		try {
			final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
					.createArchiveInputStream("tar", is);
			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
				logger.debug("Entry = " + entry.getName());
				IOUtils.copy(debInputStream, outputFileStream);
			}
		} finally {
			is.close();
		}
	}

	public static void createTarGZ(String dirPath, String tarGzPath)
			throws FileNotFoundException, IOException
	{
		FileOutputStream fOut = new FileOutputStream(new File(tarGzPath));
		BufferedOutputStream bOut = new BufferedOutputStream(fOut);
		GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
		TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);
		addFileToTarGz(tOut, dirPath, "");

		tOut.finish();
		tOut.close();
		gzOut.close();
		bOut.close();
		fOut.close();
	}

	public static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base)
			throws IOException
	{
		File f = new File(path);
		String entryName = base + f.getName();
		TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
		tOut.putArchiveEntry(tarEntry);

		if (f.isFile()) {
			IOUtils.copyLarge(new FileInputStream(f), tOut);
			tOut.closeArchiveEntry();
		} else {
			tOut.closeArchiveEntry();
			File[] children = f.listFiles();
			if (children != null) {
				for (File child : children) {
					addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
				}
			}
		}
	}
}
