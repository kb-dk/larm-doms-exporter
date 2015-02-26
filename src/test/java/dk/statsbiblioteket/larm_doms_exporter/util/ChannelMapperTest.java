package dk.statsbiblioteket.larm_doms_exporter.util;

import java.io.File;

import org.junit.Test;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ChannelMapperTest {
	private String chaosChannelMappingConfigFilename = "chaos_channelmapping.xml";
	
    @Test
    public void testGetChaosChannel() throws Exception {
        String ch1 = "drp1";
        ExportContext context = new ExportContext();
        File chaosChannelMappingConfigFile = new File(context.getClass().getClassLoader().getResource(chaosChannelMappingConfigFilename).toURI());
        context.setChaosChannelMappingConfigFile(chaosChannelMappingConfigFile);
        ChannelMapper channelMapper = ChannelMapper.getChannelMapper(context);
        assertEquals("DR P1", channelMapper.getChaosChannel(ch1));
    }

    @Test
    public void testGetChaosChannelUnknown() throws Exception {
        String ch1 = "drp1234";
        ExportContext context = new ExportContext();
        File chaosChannelMappingConfigFile = new File(context.getClass().getClassLoader().getResource(chaosChannelMappingConfigFilename).toURI());
        context.setChaosChannelMappingConfigFile(chaosChannelMappingConfigFile);
        ChannelMapper channelMapper = ChannelMapper.getChannelMapper(context);
        assertEquals("Ukendt", channelMapper.getChaosChannel(ch1));
    }
}
