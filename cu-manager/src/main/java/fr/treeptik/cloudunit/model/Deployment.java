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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.treeptik.cloudunit.utils.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Deployment
                implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @Temporal( TemporalType.TIMESTAMP )
    @JsonSerialize( using = JsonDateSerializer.class )
    private Date date;

    @JsonIgnore
    @ManyToOne
    private Application application;

    private Type type;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate( Date date )
    {
        this.date = date;
    }

    public Application getApplication()
    {
        return application;
    }

    public void setApplication( Application application )
    {
        this.application = application;
    }

    public Type getType()
    {
        return type;
    }

    public void setType( Type type )
    {
        this.type = type;
    }

}
