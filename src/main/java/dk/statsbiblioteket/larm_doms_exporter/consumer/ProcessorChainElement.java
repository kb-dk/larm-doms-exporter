package dk.statsbiblioteket.larm_doms_exporter.consumer;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ProcessorChainElement.class);
    private ProcessorChainElement nextElement;

    public static ProcessorChainElement makeChain(ProcessorChainElement... elements) {

        ProcessorChainElement previous = null;
        for (ProcessorChainElement element : elements) {
            if (previous != null) {
                previous.setNextElement(element);
            }
            previous = element;
        }
        return elements[0];
    }


    public void setNextElement(ProcessorChainElement nextElement) {
        this.nextElement = nextElement;
    }

    public void processIteratively(DomsExportRecord record, ExportContext context, ExportRequestState state)
            throws ProcessorException {
        logger.info("Processing with " + this.getClass() + " on " + record.getID());
        processThis(record, context, state);
        if (nextElement != null) {
            nextElement.processIteratively(record, context, state);
        }
    }

    protected abstract void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException;

}
