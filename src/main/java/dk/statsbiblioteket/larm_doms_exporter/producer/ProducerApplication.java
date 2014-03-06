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
    public static final String USAGE_MESSAGE = "Usage: java " +
            "  dk.statsbiblioteket.larm_doms_exporter.producer.ProducerApplication \n" +
            " --lde_hibernate_configfile=$confDir/hibernate.cfg.lde.xml\n" +
            " --bta_hibernate_configfile=$confDir/hibernate.cfg.bta.xml\n" +
            " --infrastructure_configfile=$confDir/lde.infrastructure.properties\n" +
            " --behavioural_configfile=$confDir/lde.behaviour.properties";

    /**
     * This application pulls information on newly transcoded programs from the BTA database and puts them in the LDE
     * database, where they form a persistent queue of programs to be exported to LARM.
     *
     * Usage: java " +
     "  dk.statsbiblioteket.larm_doms_exporter.producer.ProducerApplication \n" +
     " --lde_hibernate_configfile=$confDir/hibernate.cfg.lde.xml\n" +
     " --bta_hibernate_configfile=$confDir/hibernate.cfg.bta.xml\n" +
     " --infrastructure_configfile=$confDir/lde.infrastructure.properties\n" +
     " --behavioural_configfile=$confDir/lde.behaviour.properties
     *
     */
    public static void main(String[] args) throws UsageException, OptionParseException, InvalidCredentialsException, MethodFailedException {
        boolean initialPull = false;
        logger.info("Entered main method of " + ProducerApplication.class.getName());
        ExportOptionsParser optionsParser = new ExportOptionsParser();
        ExportContext context = null;
        try {
            context = optionsParser.parseOptions(args);
        } catch (Exception e) {
            usage();
            System.exit(1);
        }
        logger.info("Context initialised: '" + context.toString() + "'");
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(context.getLdeHibernateConfigurationFile().getAbsolutePath());
        DomsExportRecordDAO ldeDao = new DomsExportRecordDAO(hibernateUtil);
        Long startingTimestamp = ldeDao.getMostRecentExportedTimestamp();
        if (startingTimestamp == null) {
            initialPull = true;
            startingTimestamp = 0L;
            logger.info("This is the initial pull. DOMS objects unmodified since " + new Date(context.getSeedTimestamp()) + " are assumed to have " +
                    "already been exported.");
        }
        startingTimestamp++;
        dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil btaHibernateUtil = dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil.getInstance(context.getBtaHibernateConfigurationFile().getAbsolutePath());
        BroadcastTranscodingRecordDAO btaDao = new BroadcastTranscodingRecordDAO(btaHibernateUtil);
        logger.info("Retrieving all transcoded records from bta with timestamp after " + startingTimestamp + " == " + new Date(startingTimestamp));
        List<BroadcastTranscodingRecord> btaRecords = btaDao.getAllTranscodings(startingTimestamp, TranscodingStateEnum.COMPLETE);
        List<BroadcastTranscodingRecord> btaRecordsPending = btaDao.getAllTranscodings(startingTimestamp, TranscodingStateEnum.PENDING);
        btaRecords.addAll(btaRecordsPending);
        logger.info("Retrieved " + btaRecords.size() + " records in state COMPLETE or PENDING from bta.");
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

    private static void usage() {
        logger.error(USAGE_MESSAGE);
        System.out.println(USAGE_MESSAGE);
    }

}
