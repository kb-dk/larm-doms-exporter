package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorChainElement;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ProcessorException;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.larm_doms_exporter.persistence.ExportStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

/**
 * Check the BTA status for this program. Failed and Rejected transcodings are also rejected for export.
 * Pending transcodings are left as Pending but the processing stops here.
 * Completed transcodings are allowed to continue processing and this class also calculates the file timestamp and
 * walltime for the export. If walltime cannot be calculated then the export is Rejected.
 */
public class BtaStatusFetcherDispatcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(BtaStatusFetcherDispatcherProcessor.class);

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        HibernateUtil btaHibernateUtil = HibernateUtil.getInstance(context.getBtaHibernateConfigurationFile().getAbsolutePath());
        BroadcastTranscodingRecordDAO btaDao = new BroadcastTranscodingRecordDAO(btaHibernateUtil);
        BroadcastTranscodingRecord btaRecord = btaDao.read(record.getID());
        if (btaRecord == null || btaRecord.getTranscodingState() == null) {
            logger.info("No BTA record found for " + record.getID() + ". Not exporting now.");
            this.setNextElement(null);
            return;
        } else {
            logger.debug("Checking status for BTA record:" + btaRecord);
            switch (btaRecord.getTranscodingState()) {
                case PENDING:
                    logger.info(record.getID() + " is awaiting transcoding. " + ". Not exporting now.");
                    this.setNextElement(null);
                    break;
                case REJECTED:
                    logger.info(record.getID() + " has been rejected for transcoding. Not exporting.");
                    this.setNextElement(null);
                    record.setState(ExportStateEnum.REJECTED);
                    context.getDomsExportRecordDAO().update(record);
                    break;
                case FAILED:
                    logger.info(record.getID() + " failed during transcoding. Not exporting.");
                    this.setNextElement(null);
                    record.setState(ExportStateEnum.REJECTED);
                    context.getDomsExportRecordDAO().update(record);
                    break;
                case COMPLETE:
                    logger.info(record.getID() + " has been successfully transcoded. Will now check if export is necessary.");
                    Date broadcastStartTime = btaRecord.getBroadcastStartTime();
                    logger.debug("Start time {} for {}", broadcastStartTime, record.getID());
                    if (broadcastStartTime != null) {
                        Date newWalltime = new Date(broadcastStartTime.getTime() + btaRecord.getStartOffset()*1000L);
                        logger.debug("Setting walltime {} for {}.", newWalltime, record.getID() );
                        state.setWalltime(newWalltime);
                    } else {
                        //logger.debug("No broadcast start time found for {} so it must be an old BES transcoding. " +
                        //        "Marking as complete.", record.getID());
                        //this.setNextElement(new MarkAsCompleteProcessor());
                        //return;
                        logger.debug("No broadcast start time found for {} so it must be an old BES transcoding. Proceeding" +
                                " with caution.");
                    }
                    final String transcodingCommand = btaRecord.getTranscodingCommand();
                    if (transcodingCommand != null) {
                        String[] splitCommand = transcodingCommand.split("\\s");
                        String outputFileS = splitCommand[splitCommand.length -1].replaceAll("/temp", "");
                        File outputFile = new File(outputFileS);
                        if (outputFile.exists()) {
                            Long fileTimeStamp = outputFile.lastModified();
                            state.setOutputFileTimeStamp(fileTimeStamp);
                            state.setMediaFileName(outputFile.getName());
                        } else {
                            logger.warn("Could not find output file: " + outputFileS);
                            state.setOutputFileTimeStamp(0L);
                            state.setMediaFileName("");
                        }
                    } else {
                        logger.warn("Could not find output file from null transcoding command for {}.", record.getID());
                        state.setOutputFileTimeStamp(0L);
                    }
                    break;
            }
        }
    }
}
