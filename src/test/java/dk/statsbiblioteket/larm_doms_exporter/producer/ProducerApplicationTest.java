package dk.statsbiblioteket.larm_doms_exporter.producer;

import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.larm_doms_exporter.cli.OptionParseException;
import dk.statsbiblioteket.larm_doms_exporter.cli.UsageException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ProducerApplicationTest {

    @Disabled
    @Test
    public void testMain() throws OptionParseException, IOException, UsageException, InvalidCredentialsException, MethodFailedException {
        String hibernateConfigFile = new File("src/test/config/hibernate.in-memory_unittest.cfg.xml").getAbsolutePath();
        String channelMappingConfigFile = getClass().getResource("/chaos_channelmapping.xml").getPath();
        String behaviourConfigFile = getClass().getResource("/lde.behaviour.properties").getPath();
        String infrastructureConfigFile = getClass().getResource("/lde.infrastructure.properties").getPath();
        String whitelistedChannelsFile = getClass().getResource("/whitelistedChannels.csv").getPath();
        String blacklistedChannelsFile = getClass().getResource("/blacklistedChannels.csv").getPath();
//        String btaRecordIdsFile = getClass().getResource("/btaRecordIds.txt").getPath();
        String btaRecordIdsFile = "";
        String[] args = new String[8];
        args[0] = "--lde_hibernate_configfile=" + hibernateConfigFile;
        args[1] = "--bta_hibernate_configfile=" + hibernateConfigFile;
        args[2] = "--infrastructure_configfile=" + infrastructureConfigFile;
        args[3] = "--behavioural_configfile=" + behaviourConfigFile;
        args[4] = "--chaos_channelmapping_configfile=" + channelMappingConfigFile;
        args[5] = "--whitelisted_channelsfile=" + whitelistedChannelsFile;
        args[6] = "--blacklisted_channelsfile=" + blacklistedChannelsFile;
        args[7] = "--bta_record_ids_file=" + btaRecordIdsFile;

        ProducerApplication.main(args);
    }
}
