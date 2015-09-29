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

package fr.treeptik.cloudunit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Image
                implements Serializable
{

    public final static Integer DISABLED = 0;

    public final static Integer ENABLED = 1;

    public final static String MODULE = "module";

    public final static String SERVER = "server";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer id;

    private String name;

    private String path;

    private String cmd;

    private String version;

    private Integer status;

    private String imageType;

    private String managerName;

    @JsonIgnore
    @OneToMany( mappedBy = "image" )
    private List<Module> modules;

    @JsonIgnore
    @OneToMany( mappedBy = "image" )
    private List<Server> servers;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public List<Module> getModules()
    {
        return modules;
    }

    public void setModules( List<Module> modules )
    {
        this.modules = modules;
    }

    public List<Server> getServers()
    {
        return servers;
    }

    public void setServers( List<Server> servers )
    {
        this.servers = servers;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getCmd()
    {
        return cmd;
    }

    public void setCmd( String cmd )
    {
        this.cmd = cmd;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType( String imageType )
    {
        this.imageType = imageType;
    }

    public String getManagerName()
    {
        return managerName;
    }

    public void setManagerName( String managerName )
    {
        this.managerName = managerName;
    }

    @Override
    public String toString()
    {
        return "Image [id=" + id + ", name=" + name + ", path=" + path
                        + ", cmd=" + cmd + ", version=" + version + ", status="
                        + status + ", imageType=" + imageType + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Image other = (Image) obj;
        if ( name == null )
        {
            if ( other.name != null )
                return false;
        }
        else if ( !name.equals( other.name ) )
            return false;
        return true;
    }

}
