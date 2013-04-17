package dk.statsbiblioteket.larm_doms_exporter.producer;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportOptionsParser;
import dk.statsbiblioteket.larm_doms_exporter.cli.OptionParseException;
import dk.statsbiblioteket.larm_doms_exporter.cli.UsageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ProducerApplication {

    private static Logger logger = LoggerFactory.getLogger(ProducerApplication.class);

    public static void main(String[] args) throws UsageException, OptionParseException {
        logger.info("Entered main method of " + ProducerApplication.class.getName());
        ExportOptionsParser optionsParser = new ExportOptionsParser();
        ExportContext context = optionsParser.parseOptions(args);
        logger.info("Context initialised: '" + context.toString() + "'");
    }

}
