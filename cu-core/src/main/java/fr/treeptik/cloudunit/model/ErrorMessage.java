package fr.treeptik.cloudunit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ErrorMessage
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    public final static Integer CHECKED_MESSAGE = 0;

    public final static Integer UNCHECKED_MESSAGE = 1;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer id;

    @Temporal( TemporalType.TIMESTAMP )
    private Date date;

    private String message;

    private Integer status;

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

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus( Integer status )
    {
        this.status = status;
    }

}