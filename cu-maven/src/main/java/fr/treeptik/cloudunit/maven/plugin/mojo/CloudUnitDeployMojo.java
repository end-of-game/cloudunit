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

package fr.treeptik.cloudunit.maven.plugin.mojo;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.common.base.Strings;

import fr.treeptik.cloudunit.maven.plugin.exception.CheckException;

@Mojo( name = "deploy" )
public class CloudUnitDeployMojo
    extends CloudunitMojo
{

    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        connect();
        if ( !isApplicationExists() )
        {
            if ( createIfNotExists )
            {
                getLog().info( "Initialize " + applicationName + " application" );
                try
                {
                    create();
                    addModule();
                    deployApp();
                }
                catch ( CheckException e )
                {
                    throw new MojoExecutionException( "Failed to initialize the application caused by "
                        + e.getLocalizedMessage(), e );
                }
            }

            else
            {
                getLog().error( "This application does not exist. Please create them or launch build with createIfNotExists command" );
                throw new MojoExecutionException( "Failed to deploy" );
            }

        }
        else
        {

            if ( snapshotOnDeploy )
            {
                createSnapshot();
            }

            deployApp();
        }
    }

    private void deployApp()
        throws MojoExecutionException
    {
        String absolutePathWarFile = getAbsolutePathWarFile();
        if ( !Strings.isNullOrEmpty( absolutePathWarFile ) )
        {
            try
            {
                deploy( absolutePathWarFile );
                getLog().info( "Your archive has been successfully deployed on " + applicationName );
            }
            catch ( IOException e )
            {
                getLog().error( "Deployment failed" );
            }
        }

    }

}
