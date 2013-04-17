package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 17/04/13
 * Time: 08:10
 * To change this template use File | Settings | File Templates.
 */
public class DomsExportRecordDAO extends GenericHibernateDAO<DomsExportRecord, String> {
    public DomsExportRecordDAO(HibernateUtilIF hibernateUtilIF) {
        super(DomsExportRecord.class, hibernateUtilIF);
    }
}
