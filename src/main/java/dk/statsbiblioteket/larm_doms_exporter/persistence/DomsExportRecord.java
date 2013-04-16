package dk.statsbiblioteket.larm_doms_exporter.persistence;

import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.Identifiable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

/**
 * This is a minimal export record which can be extended with more information by subclassing.
 */
@Entity
public class DomsExportRecord extends Identifiable<String> {

    Date lastDomsTimestamp;
    Date lastTranscodingTimestamp;
    ExportStateEnum state;

    @Enumerated(EnumType.STRING)
    public ExportStateEnum getState() {
        return state;
    }

    public void setState(ExportStateEnum state) {
        this.state = state;
    }

    public Date getLastTranscodingTimestamp() {
        return lastTranscodingTimestamp;
    }

    public void setLastTranscodingTimestamp(Date lastTranscodingTimestamp) {
        this.lastTranscodingTimestamp = lastTranscodingTimestamp;
    }

    public Date getLastDomsTimestamp() {
        return lastDomsTimestamp;
    }

    public void setLastDomsTimestamp(Date lastDomsTimestamp) {
        this.lastDomsTimestamp = lastDomsTimestamp;
    }
}
