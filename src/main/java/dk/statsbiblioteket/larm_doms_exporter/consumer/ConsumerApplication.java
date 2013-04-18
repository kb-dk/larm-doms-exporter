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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**

 */
public class ConsumerApplication {

    private static Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) throws UsageException, OptionParseException {
        logger.info("Entered main method of " + ConsumerApplication.class.getName());
        ExportOptionsParser optionsParser = new ExportOptionsParser();
        ExportContext context = optionsParser.parseOptions(args);
        logger.info("Context initialised: '" + context.toString() + "'");
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(context.getLdeHibernateConfigurationFile().getAbsolutePath());
        DomsExportRecordDAO dao = new DomsExportRecordDAO(hibernateUtil);
        context.setDomsExportRecordDAO(dao);
        List<DomsExportRecord> queue = dao.getPendingExports();
        for (DomsExportRecord record: queue) {
            try {
                processRecord(record, context);
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
                radioChecker,
                hasShardChecker,
                btaStatus,
                significanceChecker,
                doExporter,
                markAsCompleter
        );
        completeChain.processIteratively(record, context, new ExportRequestState() );
    }

}
