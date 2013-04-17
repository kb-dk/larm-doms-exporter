package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;

import java.util.List;

/**
 *
 */
public class DomsExportRecordDAO extends GenericHibernateDAO<DomsExportRecord, String> {

    public DomsExportRecordDAO(HibernateUtilIF hibernateUtilIF) {
        super(DomsExportRecord.class, hibernateUtilIF);
    }

    /**
     * Get the most recent DOMS timestamp for all records in state "COMPLETE" or null if there are none.
     * @return the timestamp.
     */
    public Long getMostRecentExportedTimestamp() {
       List<DomsExportRecord> records = getSession().createQuery("FROM DomsExportRecord WHERE state = ? ORDER BY lastDomsTimestamp DESC ").setParameter(0, ExportStateEnum.COMPLETE).list();
       if (records.isEmpty()) {
           return null;
       } else {
           return records.get(0).getLastDomsTimestamp().getTime();
       }
    }
}
