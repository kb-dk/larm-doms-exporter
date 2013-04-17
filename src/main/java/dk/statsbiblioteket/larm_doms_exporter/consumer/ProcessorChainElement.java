package dk.statsbiblioteket.larm_doms_exporter.consumer;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ProcessorChainElement.class);
    private ProcessorChainElement childElement;

    public static ProcessorChainElement makeChain(ProcessorChainElement... elements) {

        ProcessorChainElement previous = null;
        for (ProcessorChainElement element : elements) {
            if (previous != null) {
                previous.setChildElement(element);
            }
            previous = element;
        }
        return elements[0];
    }


    public void setChildElement(ProcessorChainElement childElement) {
        this.childElement = childElement;
    }

    public void processIteratively(DomsExportRecord record, ExportContext context, ExportRequestState state)
            throws ProcessorException {
        logger.info("Processing with " + this.getClass() + " on " + record.getID());
        processThis(record, context, state);
        if (childElement != null) childElement.processIteratively(record, context, state);
    }

    protected abstract void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException;

}
