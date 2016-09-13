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

package fr.treeptik.cloudunit.maven.plugin.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.treeptik.cloudunit.model.*;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.google.inject.spi.Message;


public class JsonConverter
{

    public static Application getApplication( String response )
    {
        Application application = new Application();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            application = mapper.readValue( response, Application.class );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return application;
    }

    public static Image getImage( String response )
    {
        Image image = new Image();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            image = mapper.readValue( response, Image.class );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return image;
    }

    public static User getUser( String response )
    {
        User user = new User();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            user = mapper.readValue( response, User.class );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return user;
    }

    public static List<Application> getApplications( String response )
    {
        List<Application> applications = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            applications = mapper.readValue( response, new TypeReference<List<Application>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return applications;
    }

    public static List<Snapshot> getSnapshot( String response )
    {
        List<Snapshot> snapshots = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            snapshots = mapper.readValue( response, new TypeReference<List<Snapshot>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return snapshots;
    }

    public static List<User> getUsers( String response )
    {
        List<User> users = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            users = mapper.readValue( response, new TypeReference<List<User>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return users;
    }

    public static List<Image> getImages( String response )
    {
        List<Image> images = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            images = mapper.readValue( response, new TypeReference<List<Image>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return images;
    }

    public static List<String> getTags( String response )
    {
        List<String> tags = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            tags = mapper.readValue( response, new TypeReference<List<String>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return tags;
    }

    public static Server getServer( String response )
    {
        Server server = new Server();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            server = mapper.readValue( response, Server.class );
        }
        catch ( IOException e )
        {

            e.printStackTrace();
        }
        return server;
    }

    public static Module getModule( String response )
    {
        Module module = new Module();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            module = mapper.readValue( response, Module.class );
        }
        catch ( IOException e )
        {

            e.printStackTrace();
        }
        return module;
    }

    public static List<Message> getMessage( String response )
    {
        ObjectMapper mapper = new ObjectMapper();
        List<Message> messages = null;
        try
        {
            messages = mapper.readValue( response, new TypeReference<List<Message>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return messages;
    }

    public static List<String> getAliases( String response )
    {
        List<String> tags = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            tags = mapper.readValue( response, new TypeReference<List<String>>()
            {
            } );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return tags;
    }

}
