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

package fr.treeptik.cloudunit.maven.plugin.handler;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class ResponseErrorHandler
    implements ResponseHandler<String>
{

    @Override
    public String handleResponse( HttpResponse response )
        throws ClientProtocolException, IOException
    {

        int status = response.getStatusLine().getStatusCode();

        if ( status >= 200 && status < 300 )
        {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString( entity ) : null;
        }
        else
        {
            switch ( status )
            {
                case 500:
                    InputStreamReader reader = null;
                    reader = new InputStreamReader( response.getEntity().getContent() );
                    LineIterator lineIterator = new LineIterator( reader );
                    StringBuilder jsonStringBuilder = new StringBuilder();

                    while ( lineIterator.hasNext() )
                    {
                        jsonStringBuilder.append( lineIterator.nextLine() );
                    }
                    throw new ClientProtocolException( jsonStringBuilder.toString() );
                case 401:
                    throw new ClientProtocolException( "Status 401 - Bad credentials!" );
                case 403:
                    throw new ClientProtocolException( "Status 403 - You must be an admin to execute this command!" );
                case 404:
                    throw new ClientProtocolException(
                                                       "Status 404 - The server can treat the request, please contact an admin" );
                default:
                    throw new ClientProtocolException( "Cloudunit server does not response. Please contact an admin" );
            }
        }

    }

}
