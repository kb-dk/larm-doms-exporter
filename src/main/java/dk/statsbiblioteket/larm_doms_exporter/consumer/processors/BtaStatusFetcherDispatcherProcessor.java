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

import java.util.Date;

/**
 *
 */
public class BtaStatusFetcherDispatcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(BtaStatusFetcherDispatcherProcessor.class);

    @Override
    protected void processThis(DomsExportRecord record, ExportContext context, ExportRequestState state) throws ProcessorException {
        HibernateUtil btaHibernateUtil = HibernateUtil.getInstance(context.getBtaHibernateConfigurationFile().getAbsolutePath());
        BroadcastTranscodingRecordDAO btaDao = new BroadcastTranscodingRecordDAO(btaHibernateUtil);
        BroadcastTranscodingRecord btaRecord = btaDao.read(record.getID());
        if (btaRecord == null) {
            logger.info("No BTA record found for " + record.getID() + ". Not exporting now.");
            this.setChildElement(null);
            return;
        } else {
            Date walltime = new Date(btaRecord.getBroadcastStartTime().getTime() + btaRecord.getStartOffset()*1000L);
            state.setWalltime(walltime);
            switch (btaRecord.getTranscodingState()) {
                case PENDING:
                    logger.info(record.getID() + " is awaiting transcoding. " + ". Not exporting now.");
                    this.setChildElement(null);
                    break;
                case REJECTED:
                    logger.info(record.getID() + " has been rejected for transcoding. Not exporting.");
                    this.setChildElement(null);
                    record.setState(ExportStateEnum.REJECTED);
                    context.getDomsExportRecordDAO().update(record);
                    break;
                case FAILED:
                    logger.info(record.getID() + " failed during transcoding. Not exporting.");
                    this.setChildElement(null);
                    record.setState(ExportStateEnum.REJECTED);
                    context.getDomsExportRecordDAO().update(record);
                    break;
                case COMPLETE:
                    logger.info(record.getID() + " has been successfully transcoded. Will now check if export is necessary.");
                    break;
            }
        }
    }
}
