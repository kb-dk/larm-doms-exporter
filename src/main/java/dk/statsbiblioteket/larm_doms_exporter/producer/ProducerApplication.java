package dk.statsbiblioteket.larm_doms_exporter.producer;

import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportOptionsParser;
import dk.statsbiblioteket.larm_doms_exporter.cli.OptionParseException;
import dk.statsbiblioteket.larm_doms_exporter.cli.UsageException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.DomsExportRecordDAO;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Application class which reads information on newly transcoded programs from the BTA database and loads them into
 * the LDE database.
 */
public class ProducerApplication {

    private static Logger logger = LoggerFactory.getLogger(ProducerApplication.class);
    public static final String USAGE_MESSAGE = "Usage: java " +
            " " + ProducerApplication.class.getName() + " \n" +
            " --lde_hibernate_configfile=$confDir/hibernate.cfg.lde.xml\n" +
            " --bta_hibernate_configfile=$confDir/hibernate.cfg.bta.xml\n" +
            " --infrastructure_configfile=$confDir/lde.infrastructure.properties\n" +
            " --behavioural_configfile=$confDir/lde.behaviour.properties\n" +
            " --chaos_channelmapping_configfile=$confDir/chaos_channelmapping.xml";

    /**
     * This application pulls information on newly transcoded programs from the BTA database and puts them in the LDE
     * database, where they form a persistent queue of programs to be exported to LARM. If the record is already in
     * the LDE database it is marked as PENDING. If it is either PENDING in BTA or COMPLETE in BTA and has a
     * broadcast-start-time then it is created as PENDING in LDE. If it is complete in BTA but has no broadcast-start-time
     * then it is imported to LDE as PENDING and the FixerProcessor will estimate Walltime.
     * 
     * 2015-12-04
     * If it is complete in BTA but has no broadcast-start-time
     * then it is imported to LDE as REJECTED. (LDE cannot export records for which it cannot calculate a Walltime.)
     *
     * Usage: java " +
     "  dk.statsbiblioteket.larm_doms_exporter.producer.ProducerApplication \n" +
     " --lde_hibernate_configfile=$confDir/hibernate.cfg.lde.xml\n" +
     " --bta_hibernate_configfile=$confDir/hibernate.cfg.bta.xml\n" +
     " --infrastructure_configfile=$confDir/lde.infrastructure.properties\n" +
     " --behavioural_configfile=$confDir/lde.behaviour.properties\n" +
     " --chaos_channelmapping_configfile=$confDir/chaos_channelmapping.xml";
     *
     */
    public static void main(String[] args) throws UsageException, OptionParseException, InvalidCredentialsException, MethodFailedException {
        logger.info("Entered main method of " + ProducerApplication.class.getName());
        ExportOptionsParser optionsParser = new ExportOptionsParser();
        ExportContext context = null;
        try {
            context = optionsParser.parseOptions(args);
        } catch (UsageException e) {
            usage();
            System.exit(1);
        }
        logger.info("Context initialised: '" + context.toString() + "'");
        queueRecordsForExport(context);
        logger.info("Exiting " + ProducerApplication.class.getName());
    }

    private static void queueRecordsForExport(ExportContext context) {
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(context.getLdeHibernateConfigurationFile().getAbsolutePath());
        DomsExportRecordDAO ldeDao = new DomsExportRecordDAO(hibernateUtil);
        Long startingTimestamp = ldeDao.getMostRecentExportedTimestamp();
        if (startingTimestamp == null) {
            startingTimestamp = 0L;
            logger.info("This is the initial pull.");
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
        int rejected = 0;
        for (BroadcastTranscodingRecord btaRecord: btaRecords) {
            DomsExportRecord ldeDatabaseRecord = ldeDao.readOrCreate(btaRecord.getID());
            if (ldeDatabaseRecord.getLastDomsTimestamp() != null && !ldeDatabaseRecord.getState().equals(ExportStateEnum.PENDING)) {  //preexisting record
                ldeDatabaseRecord.setLastDomsTimestamp(new Date(btaRecord.getDomsLatestTimestamp()));
                ldeDatabaseRecord.setState(ExportStateEnum.PENDING);
                ldeDao.update(ldeDatabaseRecord);
                pending++;
            }
            else if (btaRecord.getTranscodingState().equals(TranscodingStateEnum.COMPLETE) || btaRecord.getTranscodingState().equals(TranscodingStateEnum.PENDING)) {
                ldeDatabaseRecord.setLastDomsTimestamp(new Date(btaRecord.getDomsLatestTimestamp()));
                ldeDatabaseRecord.setState(ExportStateEnum.PENDING);
                ldeDao.update(ldeDatabaseRecord);
                pending++;
            } else {
                ldeDatabaseRecord.setLastDomsTimestamp(new Date(btaRecord.getDomsLatestTimestamp()));
                ldeDatabaseRecord.setState(ExportStateEnum.REJECTED);
                ldeDao.update(ldeDatabaseRecord);
                rejected++;
            }
        }
        logger.info("Added as pending or changed to pending " + pending + " records.");
        logger.info("Rejected {} records", rejected);
    }

    private static void usage() {
        logger.error(USAGE_MESSAGE);
        System.out.println(USAGE_MESSAGE);
    }

}
