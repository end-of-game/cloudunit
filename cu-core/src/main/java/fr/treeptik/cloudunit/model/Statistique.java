package fr.treeptik.cloudunit.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Statistique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private Date startTime;

    public Statistique() {
        this.id = 0;
        this.startTime = new Date(System.currentTimeMillis());
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getStartTimeAsString() {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return format.format(startTime);
    }
}
