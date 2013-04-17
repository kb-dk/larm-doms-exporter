package dk.statsbiblioteket.larm_doms_exporter.consumer;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportOptionsParser;
import dk.statsbiblioteket.larm_doms_exporter.cli.OptionParseException;
import dk.statsbiblioteket.larm_doms_exporter.cli.UsageException;
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
        HibernateUtil hibernateUtil = HibernateUtil.getInstance(context.getHibernateConfigurationFile().getAbsolutePath());
        DomsExportRecordDAO dao = new DomsExportRecordDAO(hibernateUtil);
        List<DomsExportRecord> queue = dao.getPendingExports();
        for (DomsExportRecord record: queue) {
               processRecord(record);
        }
    }

    private static void processRecord(DomsExportRecord record) {

    }

}
