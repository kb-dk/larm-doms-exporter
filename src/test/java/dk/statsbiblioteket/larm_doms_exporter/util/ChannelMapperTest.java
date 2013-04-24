package dk.statsbiblioteket.larm_doms_exporter.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ChannelMapperTest {
    @Test
    public void testGetChaosChannel() throws Exception {
        String ch1 = "drp1";
        assertEquals("DR P1", ChannelMapper.getChaosChannel(ch1));
    }

    @Test
    public void testGetChaosChannelUnknown() throws Exception {
        String ch1 = "drp1234";
        assertEquals("Ukendt", ChannelMapper.getChaosChannel(ch1));
    }
}
