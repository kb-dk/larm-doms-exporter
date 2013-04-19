package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;

/**
*
 */
public class MarkAsCompleteProcessor extends ProcessorChainElement {
    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        record.setLastExportTimestamp(record.getLastDomsTimestamp());
        record.setState(ExportStateEnum.COMPLETE);
        context.getDomsExportRecordDAO().update(record);
    }
}
