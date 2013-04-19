package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 4/18/13
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class DoExportProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(DoExportProcessor.class);

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        File outputDir = context.getOutputDirectory();
        File outputFile = new File(outputDir, record.getID()+".xml");
        logger.info("Writing new dummy export file " + outputFile.getAbsolutePath());
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new ProcessorException("Could not create output file");
        }
    }
}
