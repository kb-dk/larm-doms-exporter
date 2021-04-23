package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.util.xml.DefaultNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Check that the channel is whitelisted. If not, it is rejected.
 * If not blacklisted either, an error is reported as well.
 */
public class IsChannelWhitelistedCheckerProcessor extends ProcessorChainElement {
    private static Logger logger = LoggerFactory.getLogger(IsChannelWhitelistedCheckerProcessor.class);

    private static XPathFactory xpathFactory = XPathFactory.newInstance();

    private static NamespaceContext program_broadcast_namespaceContext =
            new DefaultNamespaceContext(null, "pbc", "http://doms.statsbiblioteket.dk/types/program_broadcast/0/1/#");

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        List<String> whitelistedChannels = readFileToList(context.getWhitelistedChannelsFile());
        List<String> blacklistedChannels = readFileToList(context.getBlacklistedChannelsFile());

        String channelName = null;
        try {
            channelName = getChannelName(state);
        } catch (Exception e) {
            String warning = String.format(Locale.ROOT,"Failed to get channel name from record metadata. Record %s is rejected", record.getID());
            System.out.println(warning);
            logger.warn(warning, e);
            rejectRecord(record, context);
        }
        if(blacklistedChannels.contains("\"" + channelName + "\"")){
            rejectRecord(record, context);
        }
        else if(!whitelistedChannels.contains("\"" + channelName + "\"")){
            String warning = String.format(Locale.ROOT, "Channel %s is not known. Record %s is rejected. Go to the following page for instructions: %s",
                    channelName, record.getID(), context.getUnknownChannelPage());
            System.out.println(warning);
            logger.warn(warning);
            rejectRecord(record, context);
        }
    }

    private void rejectRecord(DomsExportRecord record, ExportContext context) {
        this.setNextElement(null);
        record.setState(ExportStateEnum.REJECTED);
        context.getDomsExportRecordDAO().update(record);
    }

    private static String getChannelName(ExportRequestState state) throws XPathExpressionException, ProcessorException {
        String xpathString = "/pbc:programBroadcast/pbc:channelId";
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(program_broadcast_namespaceContext);
        String channelName = (String) xpath.evaluate(xpathString, state.getProgramBroadcastDocument(), XPathConstants.STRING);
        if(channelName == null){
            throw new ProcessorException("channelId not found in programBroadcast metadata");
        }
        return channelName;
    }

    private static List<String> readFileToList(File file) throws ProcessorException {
        List<String> result = new ArrayList<>();
        try(Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.displayName(Locale.ROOT))){
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                result.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new ProcessorException("Config file not found at: " + file.getAbsolutePath(), e);
        }
        return result;
    }
}
