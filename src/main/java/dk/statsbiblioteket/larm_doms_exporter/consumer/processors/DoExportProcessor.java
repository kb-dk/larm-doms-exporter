package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.util.ChannelMapper;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.DefaultNamespaceContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Does the actual xml export.
 *
 *
 *  Compare with the following snippet from the old exporter:
 * .append("    &#60;doms_id&#62;"              + radioProgramMetadata.shardPid                                              + "&#60;/doms_id&#62;\n")
 * .append("    &#60;channel_name&#62;"         + radioProgramMetadata.getPbcoreProgramMetadata().channel                    + "&#60;/channel_name&#62;\n")
 * .append("    &#60;title&#62;"                + radioProgramMetadata.getPbcoreProgramMetadata().titel                      + "&#60;/title&#62;\n")
 * .append("    &#60;title_original&#62;"       + radioProgramMetadata.getPbcoreProgramMetadata().originaltitel              + "&#60;/title_original&#62;\n")
 * .append("    &#60;title_episode&#62;"        + radioProgramMetadata.getPbcoreProgramMetadata().episodetitel               + "&#60;/title_episode&#62;\n")
 * .append("    &#60;start_time&#62;"           + fmt.print(radioProgramMetadata.getPbcoreProgramMetadata().start.getTime()) + "&#60;/start_time&#62;\n")
 * .append("    &#60;end_time&#62;"             + fmt.print(radioProgramMetadata.getPbcoreProgramMetadata().end.getTime())   + "&#60;/end_time&#62;\n")
 * .append("    &#60;description_short&#62;"    + radioProgramMetadata.getPbcoreProgramMetadata().descriptionKortOmtale      + "&#60;/description_short&#62;\n")
 * .append("    &#60;description_long1&#62;"    + radioProgramMetadata.getPbcoreProgramMetadata().descriptionLangOmtale1     + "&#60;/description_long1&#62;\n")
 * .append("    &#60;description_long2&#62;"    + radioProgramMetadata.getPbcoreProgramMetadata().descriptionLangOmtale2     + "&#60;/description_long2&#62;\n")
 * .append("    &#60;creator&#62;"              + radioProgramMetadata.getPbcoreProgramMetadata().forfattere                 + "&#60;/creator&#62;\n")
 * .append("    &#60;contributor&#62;"          + radioProgramMetadata.getPbcoreProgramMetadata().medvirkende                + "&#60;/contributor&#62;\n")
 * .append("    &#60;contributor_director&#62;" + radioProgramMetadata.getPbcoreProgramMetadata().instruktion                + "&#60;/contributor_director&#62;\n")
 */
public class DoExportProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(DoExportProcessor.class);
    public static String XML_TEMPLATE = null;
    public static final String HAS_SHARD = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasShard";
    SimpleDateFormat domsDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    static SimpleDateFormat chaosDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    static {
        InputStream is = ClassLoader.class.getResourceAsStream("/CHAOS_envelope_template.xml");
        try {
            XML_TEMPLATE = IOUtils.toString(is, "UTF-8");
            logger.debug("Read XML template: " + XML_TEMPLATE);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading XML template file", e);
        }
    }

    public static class PBCoreNamespaceResolver implements NamespaceContext {

        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix.equals("pbcore")) return  "http://www.pbcore.org/PBCore/PBCoreNamespace.html";
            else   return XMLConstants.NULL_NS_URI;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            throw new RuntimeException("Not yet implemented");

        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            throw new RuntimeException("Not yet implemented");

        }
    }

    private static XPathFactory xpathFactory = XPathFactory.newInstance();

    private static NamespaceContext pbcoreNamespaceContext = new PBCoreNamespaceResolver();

    private static NamespaceContext program_broadcast_namespaceContext =
            new DefaultNamespaceContext(null, "pbc", "http://doms.statsbiblioteket.dk/types/program_broadcast/0/1/#");

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {



        Date earliestBroadcastDate = new Date(context.getEarliestExportBroadcastTimestamp());
        Date earliestTimestamp = new Date(context.getInProductionTimestamp());
        if (state.getProgramStart().before(earliestBroadcastDate) && record.getLastDomsTimestamp().before(earliestTimestamp)) {
            logger.info("Program " + record.getID() + " has Broadcast date " + state.getProgramStart()
                    + " and DOMS timestamp " + record.getLastDomsTimestamp() + " and so will be marked as complete" +
                    " without exporting");
            this.setNextElement(new MarkAsCompleteProcessor());
            return;
        }
        //TODO move some of this functionalty out of here and into a prior processor. In particular, write data to a
        //general OutputStream stored in the state or record. This will make it much easier to test the class.
        //Renaming will need to be moved to a postprocessingProcessor.
        File outputDir = context.getOutputDirectory();
        File tempOutputDir = new File(outputDir, "tempDir");
        tempOutputDir.mkdirs();
        File outputFile = new File(outputDir, record.getID().replaceAll("uuid:","")+".xml");
        if (outputFile.exists()) {
            throw new ProcessorException("Not exporting again at this time because " + outputFile.getAbsolutePath()
            + " already exists. Object will be left in state PENDING and processed again next time.");
        }
        File tempOutputFile = new File(tempOutputDir, outputFile.getName());
        String result = getOutputString(record, context, state);
        logger.info("Writing new export file " + outputFile.getAbsolutePath());
        try {
            FileOutputStream os = new FileOutputStream(tempOutputFile);
            os.write(result.getBytes());
            os.flush();
            os.close();
            context.incrementNumExports();
            logger.debug("{} records exported so far.");
        } catch (IOException e) {
            throw new ProcessorException("Could not write output file: " + tempOutputFile.getAbsolutePath() + ". Export of this program is left" +
                    "in the state PENDING.", e);
        }
        tempOutputFile.renameTo(outputFile);
    }


    private String getOutputString(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        String result = XML_TEMPLATE;
        try {
        	ChannelMapper channelMapper = ChannelMapper.getChannelMapper(context);
            result = substituteShardPid(result, record, context, state);
            result = substituteEndTimeString(result, record, context, state);
            result = substituteStartTimeString(result, record, context, state);
            result = substituteChannel(result, record, context, state, channelMapper);
            result = substituteGeneric(result, record, context, state, "###TITLE###","/pbcore:PBCoreDescriptionDocument/pbcore:pbcoreTitle[pbcore:titleType='titel']/pbcore:title");
            result = substituteGeneric(result, record, context, state, "###ABSTRACT###","/pbcore:PBCoreDescriptionDocument/pbcore:pbcoreDescription[pbcore:descriptionType='kortomtale']/pbcore:description");
            result = substituteGeneric(result, record, context, state, "###DESCRIPTION1###","/pbcore:PBCoreDescriptionDocument/pbcore:pbcoreDescription[pbcore:descriptionType='langomtale1']/pbcore:description");
            result = substituteGeneric(result, record, context, state, "###DESCRIPTION2###", "/pbcore:PBCoreDescriptionDocument/pbcore:pbcoreDescription[pbcore:descriptionType='langomtale2']/pbcore:description");
            result = substituteGeneric(result, record, context, state, "###MAJORGENRE###", "substring-after(/pbcore:PBCoreDescriptionDocument/pbcore:pbcoreGenre/pbcore:genre[contains(text(), 'hovedgenre')]/text(), 'hovedgenre: ')");
            result = substituteGeneric(result, record, context, state, "###MINORGENRE###", "substring-after(/pbcore:PBCoreDescriptionDocument/pbcore:pbcoreGenre/pbcore:genre[contains(text(), 'undergenre')]/text(), 'undergenre: ')");
            result = substitutePublisher(result, record, context, state, channelMapper);
            result = substituteLogoFilename(result, record, context, state, channelMapper);
            result = substituteEmpty(result, "###CREATOR###");
            result = substituteEmpty(result, "###LOCATIONS###");
            result = substituteProgramPid(result, record, context, state);
            result = substituteFilename(result, record, context, state);
            result = substituteFileTimeStamp(result, record, context, state);
            result = substituteWalltime(result, record, context, state);
            result = substituteStreamingServerFolder(result, record, context, state);
            result = substituteStreamingServerId(result, record, context, state);
        } catch (Exception e) {
            throw new ProcessorException("Error processing " + record.getID(), e);
        }
        return result;
    }

    private String substituteWalltime(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) {
        Pattern pattern = Pattern.compile("###WALLTIME###", Pattern.DOTALL);

        if (state.getWalltime() != null) {
            return pattern.matcher(template).replaceAll(chaosDateFormat.format(state.getWalltime()));
        } else {
            return pattern.matcher(template).replaceAll("");
        }
    }

    private String substituteStreamingServerId (String template, DomsExportRecord record, ExportContext context, ExportRequestState state) {
        Pattern pattern = Pattern.compile("###STREAMING_SERVER_ID###", Pattern.DOTALL);
        return pattern.matcher(template).replaceAll(context.getGeckonStreamingserverDestinationId() + "");
    }


    private String substituteStreamingServerFolder (String template, DomsExportRecord record, ExportContext context, ExportRequestState state) {
        Pattern pattern = Pattern.compile("###STREAMING_SERVER_FOLDER###", Pattern.DOTALL);
        return pattern.matcher(template).replaceAll(context.getGeckonStreamingserverFolderpath());
    }


    private String substituteFileTimeStamp(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) {
        Pattern pattern = Pattern.compile("###FILETIMESTAMP###", Pattern.DOTALL);
        if (state.getOutputFileTimeStamp() != null) {
            final Date date = new Date(state.getOutputFileTimeStamp());
            return pattern.matcher(template).replaceAll(chaosDateFormat.format(date));
        } else {
            return pattern.matcher(template).replaceAll(chaosDateFormat.format(new Date()));
        }
    }

    private String substituteShardPid(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        String patternString = "###SHARD_ID###";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        CentralWebservice domsWS = context.getDomsCentralWebservice();
        try {
            List<Relation> relations = domsWS.getNamedRelations(record.getID(), HAS_SHARD);
            String shardPid = "";
            if (relations.size() == 1) {
                shardPid = relations.get(0).getObject().replace("uuid:","");
            }
            return pattern.matcher(template).replaceAll(shardPid);
        } catch (Exception e) {
            throw new ProcessorException("Error while checking for the existence of a hasShard relation", e);
        }
    }

    private String substituteStartTimeString(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) throws XPathExpressionException, ParseException {
        String patternString = "###START_TIME###";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        String programStartString = getBroadcastStartTimeString(state);
        String chaosStartString = chaosDateFormat.format(domsDateFormat.parse(programStartString));
        return pattern.matcher(template).replaceAll(chaosStartString);
    }

    static String getBroadcastStartTimeString(ExportRequestState state) throws XPathExpressionException {
        String xpathString = "//pbcore:dateAvailableStart";
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(pbcoreNamespaceContext);
        String startTimeString = null;
        try {
            startTimeString = (String) xpath.evaluate(xpathString, state.getPbcoreDocument(), XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            try {
                logger.warn("Could not find start time with xpath '{}' on \n{}\n.", xpathString, DOM.domToString(state.getPbcoreDocument()) );
                throw(e);
            } catch (TransformerException x) {
                logger.warn("", x);
            }
        }
        return startTimeString;
    }

    private String substituteEndTimeString(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) throws XPathExpressionException, ParseException {
        String patternString = "###END_TIME###";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        String xpathString = "//pbcore:dateAvailableEnd";
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(pbcoreNamespaceContext);
        String programStartString = (String) xpath.evaluate(xpathString, state.getPbcoreDocument(), XPathConstants.STRING);
        String chaosStartString = chaosDateFormat.format(domsDateFormat.parse(programStartString));
        return pattern.matcher(template).replaceAll(chaosStartString);
    }

    private String substituteChannel(String template, DomsExportRecord record, ExportContext context, ExportRequestState state, ChannelMapper channelMapper) throws XPathExpressionException {
        String patternString = "###CHANNEL###";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        String matchingString = getChannelName(state);
        return pattern.matcher(template).replaceAll(channelMapper.getChaosChannel(matchingString));
    }

    static String getChannelName(ExportRequestState state) throws XPathExpressionException {
//        String xpathString = "/pbcore:PBCoreDescriptionDocument/pbcore:pbcorePublisher[pbcore:publisherRole='channel_name']/pbcore:publisher";
        String xpathString = "/pbc:programBroadcast/pbc:channelId";
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(program_broadcast_namespaceContext);
        return (String) xpath.evaluate(xpathString, state.getProgramBroadcast(), XPathConstants.STRING);
    }

    private String substitutePublisher(String template, DomsExportRecord record, ExportContext context, ExportRequestState state, ChannelMapper channelMapper) throws XPathExpressionException {
        String patternString = "###PUBLISHER###";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        String matchingString = getChannelName(state);
        return pattern.matcher(template).replaceAll(channelMapper.getPublisher(matchingString));
    }

    private String substituteLogoFilename(String template, DomsExportRecord record, ExportContext context, ExportRequestState state, ChannelMapper channelMapper) throws XPathExpressionException {
        String patternString = "###LOGO_FILENAME###";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        String matchingString = getChannelName(state);
        return pattern.matcher(template).replaceAll(channelMapper.getLogoFileName(matchingString));
    }
    
    private String substituteGeneric(String template, DomsExportRecord record, ExportContext context, ExportRequestState state, String patternString, String xpathString) throws XPathExpressionException {
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(pbcoreNamespaceContext);
        String matchingString = (String) xpath.evaluate(xpathString, state.getPbcoreDocument(), XPathConstants.STRING);
        matchingString = StringEscapeUtils.escapeXml11(matchingString);
        matchingString = Matcher.quoteReplacement(matchingString);
        return pattern.matcher(template).replaceAll(matchingString);
    }

    private String substituteEmpty(String template, String patternString) {
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        return pattern.matcher(template).replaceAll("");
    }

    private String substituteProgramPid(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) {
        Pattern pattern = Pattern.compile("###PROGRAM_ID###", Pattern.DOTALL);
        return pattern.matcher(template).replaceAll(record.getID().replace("uuid:",""));
    }

    private String substituteFilename(String template, DomsExportRecord record, ExportContext context, ExportRequestState state) {
        Pattern pattern = Pattern.compile("###FILENAME###", Pattern.DOTALL);
        return pattern.matcher(template).replaceAll(state.getMediaFileName());
    }
}
