package dk.statsbiblioteket.larm_doms_exporter.consumer;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportOptionsParser;
import dk.statsbiblioteket.larm_doms_exporter.cli.OptionParseException;
import dk.statsbiblioteket.larm_doms_exporter.cli.UsageException;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.BtaStatusFetcherDispatcherProcessor;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.DoExportProcessor;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.HasShardAnalysisCheckerProcessor;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.IsRadioProgramCheckerProcessor;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.MarkAsCompleteProcessor;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.SignificantChangeCheckerProcessor;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.DomsExportRecordDAO;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.larm_doms_exporter.producer.ProducerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Application for export as xml envelopes the entries in the persistent (database) queue.
 */
public class ConsumerApplication {

    private static Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);
    public static final String USAGE_MESSAGE = "Usage: java " +
            "  " + ConsumerApplication.class.getName() + "\n" +
            " --lde_hibernate_configfile=$confDir/hibernate.cfg.lde.xml\n" +
            " --bta_hibernate_configfile=$confDir/hibernate.cfg.bta.xml\n" +
            " --infrastructure_configfile=$confDir/lde.infrastructure.properties\n" +
            " --behavioural_configfile=$confDir/lde.behaviour.properties";

    /**
     * This application exports all appropriate objects to LARM as xml files. The details
     * of how it determines which objects are ready to export are documented at
     * https://sbforge.org/display/CHAOS/LARM-DOMS-Exporter
     *
     * Usage: java " +
     "  dk.statsbiblioteket.larm_doms_exporter.consumer.ConsumerApplication \n" +
     " --lde_hibernate_configfile=$confDir/hibernate.cfg.lde.xml\n" +
     " --bta_hibernate_configfile=$confDir/hibernate.cfg.bta.xml\n" +
     " --infrastructure_configfile=$confDir/lde.infrastructure.properties\n" +
     " --behavioural_configfile=$confDir/lde.behaviour.properties
     *
     * @param args
     * @throws UsageException
     * @throws OptionParseException
     */
    public static void main(String[] args) throws UsageException, OptionParseException {
        logger.info("Entered main method of " + ConsumerApplication.class.getName());
        ExportOptionsParser optionsParser = new ExportOptionsParser();
        ExportContext context = null;
        try {
            context = optionsParser.parseOptions(args);
        } catch (UsageException e) {
            usage();
            System.exit(1);
        }
        logger.info("Context initialised: '" + context.toString() + "'");
        processAllExports(context);
    }

    private static void processAllExports(ExportContext context) {
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(context.getLdeHibernateConfigurationFile().getAbsolutePath());
        DomsExportRecordDAO dao = new DomsExportRecordDAO(hibernateUtil);
        context.setDomsExportRecordDAO(dao);
        List<DomsExportRecord> queue = dao.getPendingExports();
        for (DomsExportRecord record: queue) {
            if (context.getNumExports() >= context.getMaxExportsPerRun()) {
                logger.info("Maximum number of exports reached. Next consumer run will continue from here.");
                System.exit(0);
            }
            try {
                processRecord(record, context);
                logger.info("Finished all processing for " + record.getID());
            } catch (Exception e) {  //Fault Barrier
                logger.warn("Export processing failed for " + record.getID(), e);
            }
        }
    }

    private static void processRecord(DomsExportRecord record, ExportContext context) throws ProcessorException {
        ProcessorChainElement radioChecker = new IsRadioProgramCheckerProcessor();
        ProcessorChainElement hasShardChecker = new HasShardAnalysisCheckerProcessor();
        ProcessorChainElement btaStatus = new BtaStatusFetcherDispatcherProcessor();
        ProcessorChainElement significanceChecker = new SignificantChangeCheckerProcessor();
        ProcessorChainElement doExporter = new DoExportProcessor();
        ProcessorChainElement markAsCompleter = new MarkAsCompleteProcessor();
        ProcessorChainElement completeChain = ProcessorChainElement.makeChain(
                radioChecker,      //is radio program
                hasShardChecker,   //has been exported
                btaStatus,         //has been analysed for holes etc.
                significanceChecker, //Change from previous export doms timestamp is significant
                doExporter,          //Do the export
                markAsCompleter      //Update the database
        );
        completeChain.processIteratively(record, context, new ExportRequestState() );
    }

    private static void usage() {
        //TODO consider using library method from apache commons cli
        logger.error(USAGE_MESSAGE);
        System.out.println(USAGE_MESSAGE);
    }

}
