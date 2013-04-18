package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.DomsExportRecordDAO;
import dk.statsbiblioteket.larm_doms_exporter.util.CentralWebserviceFactory;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 *
 */
public class IsRadioProgramCheckerProcessort extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(IsRadioProgramCheckerProcessort.class);

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        CentralWebservice domsWS = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            structureXmlString = domsWS.getDatastreamContents(record.getID(), "PBCORE");
            if (structureXmlString == null) {
                throw new ProcessorException("No PBCORE found for " + record.getID());
            }
            state.setPbcoreString(structureXmlString);
        } catch (Exception e) {
            throw new ProcessorException("Failed to get PBCORE for " + record.getID(),e);
        }
        if (!isRadio(state.getPbcoreString())) {
            logger.info(record.getID() + " is not a radio program. Not exporting.");
            this.setChildElement(null);
            record.setState(ExportStateEnum.REJECTED);
            context.getDomsExportRecordDAO().update(record);
        } else {
            logger.info(record.getID() + " is a radio program. Proceeding.");
        }
    }

    private boolean isRadio(String pbcoreString) {
        XPathSelector xpath = DOM.createXPathSelector("pb", "http://www.pbcore.org/PBCore/PBCoreNamespace.html");
        Document doc = DOM.stringToDOM(pbcoreString, true);
        String formatMediaType = xpath.selectString(doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:formatMediaType/text()");
        return formatMediaType.trim().equalsIgnoreCase("Sound");
    }
}
