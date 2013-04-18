package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 4/18/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class IsRadioProgramCheckerProcessort extends ProcessorChainElement {
    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
