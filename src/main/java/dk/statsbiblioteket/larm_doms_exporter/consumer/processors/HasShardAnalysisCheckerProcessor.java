package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.util.CentralWebserviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HasShardAnalysisCheckerProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(HasShardAnalysisCheckerProcessor.class);

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            structureXmlString = domsAPI.getDatastreamContents(record.getID(), "PROGRAM_STRUCTURE");
        } catch (Exception e) {
            logger.info("No PROGRAM_STRUCTURE datastream in " + record.getID() + ". Not exporting.");
            this.setChildElement(null);
            context.getDomsExportRecordDAO().delete(record);
        }
        logger.info(record.getID() + " has a program structure (shard analysis). Proceeding.");
        logger.debug("Program Structure for " + record.getID() + structureXmlString);
    }
}
