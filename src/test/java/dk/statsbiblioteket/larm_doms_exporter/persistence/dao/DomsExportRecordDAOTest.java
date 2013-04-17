package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import org.junit.Test;

import java.util.Date;
import java.util.List;

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

    @Test
    public void testGetLatestTimestamp() {
        HibernateUtilIF hibernateUtilIF = HibernateUtil.getInstance("src/test/config/hibernate.in-memory_unittest.cfg.xml");
        DomsExportRecordDAO dao = new DomsExportRecordDAO(hibernateUtilIF);
        DomsExportRecord record1 = new DomsExportRecord();
        record1.setID("uuid:foobar");
        Date date1 = new Date(1000L);
        record1.setLastDomsTimestamp(date1);
        record1.setLastExportTimestamp(date1);
        record1.setState(ExportStateEnum.COMPLETE);
        dao.create(record1);
        DomsExportRecord record2 = new DomsExportRecord();
        record2.setID("uuid:foobarfoo");
        Date date2 = new Date(1500L);
        record2.setLastDomsTimestamp(date2);
        record2.setLastExportTimestamp(date2);
        record2.setState(ExportStateEnum.COMPLETE);
        dao.create(record2);
        assertEquals(1500L, (long) dao.getMostRecentExportedTimestamp());
    }

    @Test
    public void testGetQueue() {
        HibernateUtilIF hibernateUtilIF = HibernateUtil.getInstance("src/test/config/hibernate.in-memory_unittest.cfg.xml");
        DomsExportRecordDAO dao = new DomsExportRecordDAO(hibernateUtilIF);
        DomsExportRecord record1 = new DomsExportRecord();
        record1.setID("uuid:foobar");
        Date date1 = new Date(1000L);
        record1.setLastDomsTimestamp(date1);
        record1.setLastExportTimestamp(date1);
        record1.setState(ExportStateEnum.PENDING);
        dao.create(record1);
        DomsExportRecord record2 = new DomsExportRecord();
        record2.setID("uuid:foobarfoo");
        Date date2 = new Date(1500L);
        record2.setLastDomsTimestamp(date2);
        record2.setLastExportTimestamp(date2);
        record2.setState(ExportStateEnum.PENDING);
        dao.create(record2);
        List<DomsExportRecord> queue = dao.getPendingExports();
        assertEquals(2, queue.size());
        assertEquals(1000L, queue.get(0).getLastDomsTimestamp().getTime());
    }

}
