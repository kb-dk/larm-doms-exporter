package dk.statsbiblioteket.larm_doms_exporter.producer;

import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.RecordDescription;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportOptionsParser;
import dk.statsbiblioteket.larm_doms_exporter.cli.OptionParseException;
import dk.statsbiblioteket.larm_doms_exporter.cli.UsageException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.DomsExportRecordDAO;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.larm_doms_exporter.util.CentralWebserviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class ProducerApplication {

    private static Logger logger = LoggerFactory.getLogger(ProducerApplication.class);

    public static void main(String[] args) throws UsageException, OptionParseException, InvalidCredentialsException, MethodFailedException {
        boolean initialPull = false;
        logger.info("Entered main method of " + ProducerApplication.class.getName());
        ExportOptionsParser optionsParser = new ExportOptionsParser();
        ExportContext context = optionsParser.parseOptions(args);
        logger.info("Context initialised: '" + context.toString() + "'");
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(context.getLdeHibernateConfigurationFile().getAbsolutePath());
        DomsExportRecordDAO ldeDao = new DomsExportRecordDAO(hibernateUtil);
        Long startingTimestamp = ldeDao.getMostRecentExportedTimestamp() + 1L;
        if (startingTimestamp == null) {
            initialPull = true;
            startingTimestamp = 0L;
            logger.info("This is the initial pull. DOMS objects unmodified since " + new Date(context.getSeedTimestamp()) + " are assumed to have " +
                    "already been exported.");
        }
        /*CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<RecordDescription> recordDescriptions = requestInBatches(doms, context, startingTimestamp);
        logger.info("Retrieved " + recordDescriptions.size() + " records from DOMS.");
        for (RecordDescription domsRecord: recordDescriptions) {
            DomsExportRecord databaseRecord = ldeDao.readOrCreate(domsRecord.getPid());
            if (databaseRecord.getLastDomsTimestamp() != null) {
                databaseRecord.setLastDomsTimestamp(new Date(domsRecord.getDate()));
                databaseRecord.setState(ExportStateEnum.PENDING);
                ldeDao.update(databaseRecord);
            } else {
                if (initialPull && domsRecord.getDate() < context.getSeedTimestamp()) {
                    databaseRecord.setLastDomsTimestamp(new Date(context.getSeedTimestamp()));
                    databaseRecord.setLastExportTimestamp(new Date(context.getSeedTimestamp()));
                    databaseRecord.setState(ExportStateEnum.COMPLETE);
                    ldeDao.update(databaseRecord);
                } else {
                    databaseRecord.setLastDomsTimestamp(new Date(domsRecord.getDate()));
                    databaseRecord.setState(ExportStateEnum.PENDING);
                    ldeDao.update(databaseRecord);
                }
            }
        }*/
        dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil btaHibernateUtil = dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil.getInstance(context.getBtaHibernateConfigurationFile().getAbsolutePath());
        BroadcastTranscodingRecordDAO btaDao = new BroadcastTranscodingRecordDAO(btaHibernateUtil);
        logger.info("Retrieving all transcoded records from bta with timestamp after " + startingTimestamp + " == " + new Date(startingTimestamp));
        List<BroadcastTranscodingRecord> btaRecords = btaDao.getAllTranscodings(startingTimestamp, TranscodingStateEnum.COMPLETE);
        logger.info("Retrieved " + btaRecords.size() + " transcoded records from bta.");
        int pending = 0;
        int complete = 0;
        for (BroadcastTranscodingRecord btaRecord: btaRecords) {
            DomsExportRecord ldeDatabaseRecord = ldeDao.readOrCreate(btaRecord.getID());
            if (ldeDatabaseRecord.getLastDomsTimestamp() != null) {  //preexisting record
                ldeDatabaseRecord.setLastDomsTimestamp(new Date(btaRecord.getDomsLatestTimestamp()));
                ldeDatabaseRecord.setState(ExportStateEnum.PENDING);
                ldeDao.update(ldeDatabaseRecord);
                pending++;
            }  else {
                if (initialPull && btaRecord.getDomsLatestTimestamp() < context.getSeedTimestamp()) {
                    ldeDatabaseRecord.setLastDomsTimestamp(new Date(context.getSeedTimestamp()));
                    ldeDatabaseRecord.setLastExportTimestamp(ldeDatabaseRecord.getLastDomsTimestamp());
                    ldeDatabaseRecord.setState(ExportStateEnum.COMPLETE);
                    ldeDao.update(ldeDatabaseRecord);
                    complete++;
                } else {
                    ldeDatabaseRecord.setLastDomsTimestamp(new Date(btaRecord.getDomsLatestTimestamp()));
                    ldeDatabaseRecord.setState(ExportStateEnum.PENDING);
                    ldeDao.update(ldeDatabaseRecord);
                    pending++;
                }
            }
        }
        logger.info("Added " + complete + " already-exported records.");
        logger.info("Added as pending or changed to pending " + pending + " records.");
        logger.info("Exiting " + ProducerApplication.class.getName());
    }

    static List<RecordDescription> requestInBatches(CentralWebservice doms, ExportContext context, long since) throws InvalidCredentialsException, MethodFailedException {
        String collection = "doms:RadioTV_Collection";
        String viewAngle = context.getDomsViewAngle();
        String state = "Published";
        int batchSize = 1000;

        List<RecordDescription> records = doms.getIDsModified(since, collection, viewAngle, state,0,batchSize);
        int size = records.size();
        while (size == batchSize){
            RecordDescription lastObject = records.get(records.size() - 1);
            List<RecordDescription> temp = doms.getIDsModified(lastObject.getDate(), collection, viewAngle, state, 0, batchSize);
            size = temp.size();
            records.addAll(temp);
        }
        return records;
    }

}
