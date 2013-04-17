package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.DomsExportRecordDAO;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.HibernateUtilIF;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;


/**
 *
 */
public class DomsExportRecordDAOTest {

    @Test
    public void create() {
        HibernateUtilIF hibernateUtilIF = HibernateUtil.getInstance("src/test/config/hibernate.in-memory_unittest.cfg.xml");
        DomsExportRecordDAO dao = new DomsExportRecordDAO(hibernateUtilIF);
        DomsExportRecord record = new DomsExportRecord();
        record.setID("uuid:foobar");
        record.setLastDomsTimestamp(new Date());
        record.setState(ExportStateEnum.PENDING);
        dao.create(record);
        DomsExportRecord record1 = dao.read("uuid:foobar");
        assertEquals(record.getState(), record1.getState());
    }

}
