package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.ViewBundle;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.larm_doms_exporter.util.CentralWebserviceFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class SignificantChangeCheckerProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(SignificantChangeCheckerProcessor.class);
    private static TransformerFactory transFact = TransformerFactory.newInstance();

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        if (record.getLastExportTimestamp() == null) {
            logger.info(record.getID() + " has never been exported before. Proceeding.");
        } else if (checkForNewVersion(record, context, state)) {
            logger.info(record.getID() + " has changed siginificantly. Proceeding.");
        } else {
            logger.info(record.getID() + " has not changed siginificantly. Not exporting.");
            this.setChildElement(null);
            record.setLastExportTimestamp(record.getLastDomsTimestamp());
            record.setState(ExportStateEnum.COMPLETE);
            context.getDomsExportRecordDAO().update(record);
        }
    }

    private boolean checkForNewVersion(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        Long oldExportTimestamp = record.getLastExportTimestamp().getTime();
        Long newExportTimestamp = record.getLastDomsTimestamp().getTime();
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        ViewBundle bundle = null;
        try {
            bundle = doms.getViewBundle(record.getID(), context.getDomsViewAngle());
        } catch (Exception e) {
            throw new ProcessorException("Could not obtain DOMS view bundle for " +  record.getID(), e);
        }
        String bundleString = bundle.getContents();
        String oldStructure;
        String newStructure;
        try {
            oldStructure = extractLDEstructure(killNewerVersions(bundleString, oldExportTimestamp));
            newStructure = extractLDEstructure(killNewerVersions(bundleString, newExportTimestamp));
        } catch (TransformerException e) {
            throw new ProcessorException("Failed to extract the LDE structure from the object bundle for pid " +  record.getID(), e);
        }

        XMLUnit.setIgnoreWhitespace(true);
        try {
            Diff smallDiff = new Diff(oldStructure, newStructure);
            if (smallDiff.similar()) {
                return false;
            }
        } catch (Exception e) {
            throw new ProcessorException("Failed to parse the LDE structure from the object bundle for pid " +  record.getID() + " for comparison");
        }
        return true;
    }

    String extractLDEstructure(String bundleString) throws TransformerException {
        StringWriter resultWriter = new StringWriter();
        StreamResult transformResult = new StreamResult(resultWriter);
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/extractLDEstructure.xslt"));
        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.transform(new StreamSource(new StringReader(bundleString)),
                transformResult);
        resultWriter.flush();
        return resultWriter.toString();
    }

    public String killNewerVersions(String bundleString, long timestamp) throws TransformerException {
        String timeString = getTimeString(timestamp);
        javax.xml.transform.Source xmlSource =
                new StreamSource(new StringReader(bundleString));
        javax.xml.transform.Source xsltSource1 =
                new StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/killNewerVersions.xslt"));
        StringWriter tempWriter = new StringWriter();
        StreamResult transformResult1 = new StreamResult(tempWriter);
        javax.xml.transform.Transformer trans1 =
                transFact.newTransformer(xsltSource1);

        trans1.setParameter("timestamp", timeString);
        trans1.transform(xmlSource, transformResult1);
        return transformResult1.getWriter().toString();
    }

    String getTimeString(long timestamp) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
        return format.format(new Date(timestamp));
    }

}
