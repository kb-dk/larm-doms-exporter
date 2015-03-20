package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import dk.statsbiblioteket.util.xml.DOM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathExpressionException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * For the export of old TV programs. This fixes up the Walltime and Media-file name.
 */
public class FixerProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FixerProcessor.class);


    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        state.setPbcoreDocument(DOM.stringToDOM(state.getPbcoreString(), true));
        String programStartTime;
        try {
            programStartTime = DoExportProcessor.getBroadcastStartTimeString(state);
        } catch (XPathExpressionException e) {
            record.setState(ExportStateEnum.FAILED);
            context.getDomsExportRecordDAO().update(record);
            throw new ProcessorException("Error getting start-time. Export state set to FAILED.", e);
        }
        //DOMS start times like 2007-08-19T00:05:00+0200
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSZ");
        Date programDate = null;
        try {
            programDate = sdf.parse(programStartTime);
        } catch (ParseException e) {
            throw new ProcessorException("Could not parse date " + programStartTime);
        }
        state.setProgramStart(programDate);
        if (state.getWalltime() == null) {
            logger.debug("Estimating walltime for program " + record.getID() + " by dead reckoning.");
            try {
                estimateWalltime(record, state, programDate);
            } catch (XPathExpressionException e) {
                throw new ProcessorException("Error estimating walltime for " + record.getID(), e);
            } catch (ParseException e) {
                throw new ProcessorException("Error estimating walltime for " + record.getID(), e);
            }
        }
        if (state.getMediaFileName() == null) {
            logger.debug("Guessing output filename for program " + record.getID() + ".");
            String basename = record.getID().replace("uuid:", "");
            if (state.isRadio()) {
                logger.debug("Rejected export for program " + record.getID() + " because it is a radio program without" +
                        " transcoding record.");
                this.setNextElement(null);
                record.setState(ExportStateEnum.REJECTED);
                context.getDomsExportRecordDAO().update(record);
                return;
            } else {
                basename = basename + ".flv";
            }
            state.setMediaFileName(basename);
        }
    }

    private void estimateWalltime(DomsExportRecord record, ExportRequestState state, Date programStartTime) throws XPathExpressionException, ParseException {
        int startOffset = 0;
        String [] digitvChannels = new String [] {"drhd", "drk", "drram", "drsyns", "drup", "folketinget", "kanaloestjylland", "tv2oj", "dr1", "dr2"};
        String channelName = DoExportProcessor.getChannelName(state);
        Date digitvStartDate = DoExportProcessor.chaosDateFormat.parse("2009-11-01T00:00:00"); //Date when digitv started
        Date tv2dEncryptionDate = DoExportProcessor.chaosDateFormat.parse("2012-01-11T00:00:00"); //Date when TV 2 became a pay channel and recoding was moved back to analogue
        Date youseeStartDate = DoExportProcessor.chaosDateFormat.parse("2012-12-17T00:00:00"); //Date after which everything is digital
        if (programStartTime.before(digitvStartDate)) {
            startOffset = -20;
        } else if (programStartTime.after(digitvStartDate) && Arrays.asList(digitvChannels).contains(channelName)) {
            startOffset = -10;
        } else if (programStartTime.after(digitvStartDate) && programStartTime.before(tv2dEncryptionDate) && channelName.equals("tv2d")) {
            startOffset = -10;
        } else if (programStartTime.after(digitvStartDate) && programStartTime.before(youseeStartDate)) {
            startOffset = -20;
        } else {
            startOffset = -10;
        }
        Date walltime = new Date();
        walltime.setTime(programStartTime.getTime() + 1000L * startOffset);
        state.setWalltime(walltime);
        logger.debug("Set walltime for " + record.getID() + ". Start Time " + programStartTime + ". Walltime " + walltime + ". Offset " + startOffset);
    }

}
