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

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.User;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.URIish;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class GitUtils
{

    private static CredentialsProvider configCredentialsForGit(
                    final String userNameGit, final String password )
    {
        CredentialsProvider credentialsProvider = new CredentialsProvider()
        {

            @Override
            public boolean supports( CredentialItem... arg0 )
            {
                return true;
            }

            @Override
            public boolean isInteractive()
            {
                return true;
            }

            @Override
            public boolean get( URIish arg0, CredentialItem... items )
                            throws UnsupportedCredentialItem
            {
                for ( CredentialItem item : items )
                {
                    if ( item instanceof CredentialItem.StringType )
                    {
                        ( (CredentialItem.StringType) item ).setValue( "YOUR_PASSPHRASE" );

                        continue;
                    }
                    if ( item instanceof CredentialItem.Username )
                    {
                        ( (CredentialItem.Username) item ).setValue( userNameGit );
                        continue;
                    }

                    if ( item instanceof CredentialItem.Password )
                    {
                        ( (CredentialItem.Password) item ).setValue( password
                                                                                     .toCharArray() );
                        continue;
                    }
                    if ( item instanceof CredentialItem.YesNoType )
                    {
                        ( (CredentialItem.YesNoType) item ).setValue( true );
                        continue;
                    }
                }
                return true;
            }
        };
        return credentialsProvider;

    }

    /**
     * List all GIT Tags of an application with an index
     * After this index is use to choose on which tag user want to restre his application
     * with resetOnChosenGitTag() method
     *
     * @param application
     * @param dockerManagerAddress
     * @param containerGitAddress
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public static List<String> listGitTagsOfApplication(
                    Application application, String dockerManagerAddress, String containerGitAddress )
                    throws GitAPIException, IOException
    {

        List<String> listTagsName = new ArrayList<>();

        User user = application.getUser();
        String sshPort = application.getServers().get( 0 ).getSshPort();
        String password = user.getPassword();
        String userNameGit = user.getLogin();
        String dockerManagerIP = dockerManagerAddress.substring( 0, dockerManagerAddress.length() - 5 );
        String remoteRepository = "ssh://" + userNameGit + "@"
                        + dockerManagerIP + ":" + sshPort + containerGitAddress;

        Path myTempDirPath = Files.createTempDirectory( Paths.get( "/tmp" ), null );
        File gitworkDir = myTempDirPath.toFile();

        InitCommand initCommand = Git.init();
        initCommand.setDirectory( gitworkDir );
        initCommand.call();
        FileRepository gitRepo = new FileRepository( gitworkDir );
        LsRemoteCommand lsRemoteCommand = new LsRemoteCommand( gitRepo );

        CredentialsProvider credentialsProvider = configCredentialsForGit(
                        userNameGit, password );

        lsRemoteCommand.setCredentialsProvider( credentialsProvider );
        lsRemoteCommand.setRemote( remoteRepository );
        lsRemoteCommand.setTags( true );
        Collection<Ref> collectionRefs = lsRemoteCommand.call();
        List<Ref> listRefs = new ArrayList<>( collectionRefs );

        for ( Ref ref : listRefs )
        {
            listTagsName.add( ref.getName() );
        }
        Collections.sort( listTagsName );
        FilesUtils.deleteDirectory( gitworkDir );

        return listTagsName;

    }

    /**
     * this method is associate with listGitTagsOfApplication() method
     * which list all tags with index, this is this index which must pass as parammeter of this method
     *
     * @param application
     * @param indexChosen
     * @param dockerManagerAddress
     * @param containerGitAddress
     * @return
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws GitAPIException
     * @throws IOException
     */
    public static List<String> resetOnChosenGitTag( Application application,
                                                    int indexChosen, String dockerManagerAddress,
                                                    String containerGitAddress )
                    throws InvalidRemoteException, TransportException, GitAPIException,
                    IOException
    {
        User user = application.getUser();
        String sshPort = application.getServers().get( 0 ).getSshPort();
        String password = user.getPassword();
        String userNameGit = user.getLogin();
        String dockerManagerIP = dockerManagerAddress.substring( 0, dockerManagerAddress.length() - 5 );
        String remoteRepository = "ssh://" + userNameGit + "@"
                        + dockerManagerIP + ":" + sshPort + containerGitAddress;
        File gitworkDir = Files.createTempDirectory( "clone" ).toFile();
        CloneCommand clone = Git.cloneRepository();
        clone.setDirectory( gitworkDir );

        CredentialsProvider credentialsProvider = configCredentialsForGit(
                        userNameGit, password );
        clone.setCredentialsProvider( credentialsProvider );
        clone.setURI( remoteRepository );
        Git git = clone.call();

        ListTagCommand listTagCommand = git.tagList();
        List<Ref> listRefs = listTagCommand.call();

        Ref ref = listRefs.get( indexChosen );

        ResetCommand resetCommand = git.reset();
        resetCommand.setMode( ResetType.HARD );
        resetCommand.setRef( ref.getName() );
        resetCommand.call();

        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider( credentialsProvider );
        pushCommand.setForce( true );

        List<PushResult> listPushResults = (List<PushResult>) pushCommand
                        .call();
        List<String> listPushResultsMessages = new ArrayList<>();
        for ( PushResult pushResult : listPushResults )
        {
            listPushResultsMessages.add( pushResult.getMessages() );
        }
        FilesUtils.deleteDirectory( gitworkDir );
        return listPushResultsMessages;
    }
}
