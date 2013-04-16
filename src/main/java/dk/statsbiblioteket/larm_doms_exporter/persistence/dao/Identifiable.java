package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/28/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Identifiable<PK> {


    private PK ID;

    @Id
    public PK getID() {
        return ID;
    }

    public void setID(PK ID) {
        this.ID = ID;
    }
}
