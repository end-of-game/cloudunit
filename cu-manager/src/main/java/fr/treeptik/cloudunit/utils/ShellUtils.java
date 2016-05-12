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

import com.jcraft.jsch.*;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * TODO : COMMENT
 */
@Component
public class ShellUtils {

    private Logger logger = LoggerFactory.getLogger(ShellUtils.class);

    @Value("${docker.manager.username}")
    private String dockerUserManager;

    /**
     * If you want to execute shell as cloudunit client you have to put in the
     * Map configShell a userLogin key and password key if they are not present
     * you execute command as root
     *
     * @param command
     * @param configShell
     * @return
     * @throws DockerJSONException
     */
    public int executeShell(String command, Map<String, String> configShell)
        throws RuntimeException {
        return executeShell(command, configShell, 0);
    }

    public int executeShell(String command, Map<String, String> configShell, int nbCallRecursive)
        throws RuntimeException {

        int exitCode = -1;

        if (nbCallRecursive > 0) {
            logger.warn("Recursiv call : " + nbCallRecursive
                + " for command : " + command);
        }

        // On évite un trop grand nombre d'appel récursifCoquille sur jboss
        if (nbCallRecursive >= 3) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("command=").append(command);
            throw new RuntimeException("No way to execute the command :"
                + msgError.toString());
        }

        Session session = null;
        Channel channel = null;
        InputStream in = null;

        Map<String, String> map = new HashMap<>();
        String dockerManagerIP = configShell.get("dockerManagerAddress")
            .substring(0,
                configShell.get("dockerManagerAddress").length() - 5);
        try {

            if (configShell.containsKey("userLogin")) {
                session = this.getSession(configShell.get("userLogin"),
                    configShell.get("password"), dockerManagerIP,
                    configShell.get("port"));
                channel = session.openChannel("exec");
            } else {
                session = this.getSession("root",
                    configShell.get("password"), dockerManagerIP,
                    configShell.get("port"));
                channel = session.openChannel("exec");
            }

            ((ChannelExec) channel).setCommand(command);
            channel.connect();

            in = channel.getInputStream();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        System.out.print(new String(tmp, 0, i));
                        break;
                    }
                    map.put("Line " + i, new String(tmp, 0, i));
                    String str = new String(tmp, 0, i);
                    // displays the output of the command executed.
                    System.out.print(str);
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) {

                        int i = in.read(tmp, 0, 1024);
                        System.out.print(new String(tmp, 0, i));
                    }
                    exitCode = channel.getExitStatus();
                    logger.debug("exit-status: " + exitCode);
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // ignore
                }
            }

            if (exitCode != 0) {
                throw new JSchException("ERROR : Error during execution of ["
                    + command + "] - exitCode = " + exitCode, null);
            } else {
                logger.info("command [" + command + "] execute correctly");
            }

        } catch (JSchException jex) {
            try {
                Thread.sleep(5000);
            } catch (Exception ignore) {
            }
            logger.warn("Recursiv call with a 5 seconds pause : " + nbCallRecursive);
            executeShell(command, configShell, ++nbCallRecursive);
        } catch (IOException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("command [").append(command)
                .append("] maybe in error");
            throw new RuntimeException(msgError.toString());
        } finally {
            if (channel != null)
                channel.disconnect();
            if (session != null)
                session.disconnect();
            try {
                in.close();
            } catch (Exception ignore) {
            }
        }
        return exitCode;

    }



    /**
     * TODO : COMMENT
     *
     * @param file
     * @param rootPassword
     * @param sshPort
     * @param dockerManagerAddress
     * @param completeFilePath
     * @throws CheckException
     */
    public void downloadFile(File file, String rootPassword, String sshPort,
                             String dockerManagerAddress, String completeFilePath)
        throws CheckException {
        Channel channel = null;
        try {
            String dockerManagerIP = dockerManagerAddress.substring(0,
                dockerManagerAddress.length() - 5);
            channel = this.getSession("root", rootPassword, dockerManagerIP,
                sshPort).openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            // file source, file destination
            sftpChannel.get(completeFilePath, file.getAbsolutePath());

            logger.debug("File received correctly");

        } catch (SftpException | JSchException e) {
            logger.error(e.getMessage());
            throw new CheckException("Error during file downloading");
        } finally {
            try {
                if (channel != null) {
                    channel.disconnect();
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Get a SSH session
     *
     * @param userName
     * @param password
     * @param dockerManagerAddress
     * @param port
     * @return
     * @throws JSchException
     */
    private Session getSession(String userName, String password,
                               String dockerManagerAddress, String port)
        throws JSchException {
        logger.info("parameters - IP : " + dockerManagerAddress + ", port : "
            + port + ", username : " + userName + ", password : "
            + password);

        JSch jSch = new JSch();
        Session session = jSch.getSession(userName, dockerManagerAddress,
            Integer.parseInt(port));
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
        return session;
    }

    /**
     * Send a file through sFTP
     *
     * @param file
     * @param userName
     * @param password
     * @param sshPort
     * @param address
     * @param destPathFile
     * @throws CheckException
     */
    public void sendFile(File file, String userName, String password,
                         String sshPort, String address, String destPathFile)
        throws CheckException {

        logger.debug("Send file " + file.getAbsolutePath() + " To Host "
            + userName + "@+" + address + ":" + destPathFile);

        FileInputStream fileInputStream = null;
        Channel channel = null;
        try {
            String dockerManagerIP =
                    address.substring(0, address.length() - 5);

            channel = this.getSession(userName, password, dockerManagerIP, sshPort)
                .openChannel("sftp");
            channel.connect();
            fileInputStream = new FileInputStream(file);
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel
                .put(fileInputStream, destPathFile+file.getName(), ChannelSftp.OVERWRITE);

            logger.debug("File send correctly");

        } catch (IOException | SftpException | JSchException e) {
            e.printStackTrace();
            StringBuilder msgError = new StringBuilder(512);
            msgError.append(", ").append("file=").append(file);
            msgError.append(", ").append("destPathFile=").append(destPathFile);
            msgError.append(", ").append(e);
            throw new CheckException("Error during file copying : " + msgError);

        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception ignore) {
            }
            try {
                if (channel != null) {
                    channel.disconnect();
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Send a file via sFtp with ROOT user
     *
     * @param file
     * @param rootPassword
     * @param sshPort
     * @param address
     * @param destParentDirectory
     * @throws CheckException
     */
    public void sendFile(File file, String rootPassword, String sshPort,
                         String address, String destParentDirectory)
            throws CheckException {
        sendFile(file, "root", rootPassword, sshPort, address, destParentDirectory);
    }

}
