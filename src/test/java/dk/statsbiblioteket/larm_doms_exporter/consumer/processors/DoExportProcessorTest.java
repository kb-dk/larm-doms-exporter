package dk.statsbiblioteket.larm_doms_exporter.consumer.processors;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.ExportRequestState;
import dk.statsbiblioteket.larm_doms_exporter.persistence.DomsExportRecord;
import dk.statsbiblioteket.util.xml.DOM;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DoExportProcessorTest {

    @Ignore("Manual test")
    @Test
    public void testProcessThis() throws Exception {
        DoExportProcessor doExportProcessor = new DoExportProcessor();
        DomsExportRecord record = mock(DomsExportRecord.class);
        when(record.getID()).thenReturn("test");
        ExportContext context = mock(ExportContext.class);
        ExportRequestState state = mock(ExportRequestState.class);
        CentralWebservice centralWebservice = mock(CentralWebservice.class);
        Document pbcore = DOM.streamToDOM(getClass().getResourceAsStream("/pbcore.xml"), true);
        Document programBroadcast = DOM.streamToDOM(getClass().getResourceAsStream("/programBroadcast.xml"), true);


        when(state.getProgramStart()).thenReturn(new Date());
        File channelMappingFile = new File(getClass().getResource("/chaos_channelmapping.xml").getFile());
        when(context.getChaosChannelMappingConfigFile()).thenReturn(channelMappingFile);
        when(context.getDomsCentralWebservice()).thenReturn(centralWebservice);
        when(context.getGeckonStreamingserverFolderpath()).thenReturn("test");
        when(state.getPbcoreDocument()).thenReturn(pbcore);
        when(state.getProgramBroadcastDocument()).thenReturn(programBroadcast);
        when(state.getMediaFileName()).thenReturn("test.test");
        when(state.isRadio()).thenReturn(true);

        doExportProcessor.processThis(record, context, state);
    }
}
