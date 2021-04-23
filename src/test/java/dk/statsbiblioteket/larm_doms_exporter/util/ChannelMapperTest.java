package dk.statsbiblioteket.larm_doms_exporter.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

import org.junit.jupiter.api.Test;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import org.xml.sax.SAXParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 *
 */
public class ChannelMapperTest {
	private String chaosChannelMappingConfigFilename = "chaos_channelmapping.xml";
    private String tmpChaosChannelMappingConfigFilename = "tmpChaos_channelmapping.xml";

    @Test
    public void testGetChaosChannel() throws Exception {

        String ch1 = "drp1";
        ExportContext context = new ExportContext();
        File chaosChannelMappingConfigFile =
                new File(context.getClass().getClassLoader().getResource(chaosChannelMappingConfigFilename).toURI());
        context.setChaosChannelMappingConfigFile(chaosChannelMappingConfigFile);
        ChannelMapper channelMapper = ChannelMapper.getChannelMapper(context);
        assertEquals("DR P1", channelMapper.getChaosChannel(ch1));
    }

    @Test
    public void testGetChaosChannelParseEmptyFile() throws Exception {
        String ch1 = "drp1234";
        ExportContext context = new ExportContext();
        String cfgPath = context.getClass().getClassLoader().getResource("chaos_channelmapping.xml").toURI().toString().substring(5);
        String tmpCfgPath = cfgPath + "tmp";
        Path moveA = FileSystems.getDefault().getPath(cfgPath);
        Path moveB = FileSystems.getDefault().getPath(tmpCfgPath);
        try {
            Files.move(moveA, moveB, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.createFile(moveA);
                if (!ChannelMapper.ensureConfigurationFileExist(cfgPath)) {
                    assertFalse(true, "File does not exist");
                }
                File chaosChannelMappingConfigFile =
                        new File(context.getClass().getClassLoader().getResource(chaosChannelMappingConfigFilename).toURI());
                context.setChaosChannelMappingConfigFile(chaosChannelMappingConfigFile);
            }
            catch (NullPointerException ex) {
                assertFalse(true,"File does not exist");
                return;
            }

            try {
                ChannelMapper channelMapper = ChannelMapper.getChannelMapper(context);
                assertEquals("Ukendt", channelMapper.getChaosChannel(ch1));
            }
            catch (SAXParseException ex) {
                assertFalse(true, "Config file could not be parsed");
            }
            catch (FileNotFoundException fnfEx) {
                assertFalse(false, "Cannot decode empty configuration file");
            }
        }
        finally {
            Files.move(moveB, moveA, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    public void testGetChaosChannelFileDoesNotExist() throws Exception {
        String ch1 = "drp1234";
        ExportContext context = new ExportContext();
        String cfgPath =
                context.getClass().getClassLoader().getResource("chaos_channelmapping.xml").toURI().toString().
                        substring(5);
        String tmpCfgPath = cfgPath + "tmp";
        Path moveA = FileSystems.getDefault().getPath(cfgPath);
        Path moveB = FileSystems.getDefault().getPath(tmpCfgPath);
        try {
            Files.move(moveA, moveB, StandardCopyOption.REPLACE_EXISTING);
            try {
                if (!ChannelMapper.ensureConfigurationFileExist(cfgPath)) {
                    assertFalse(true, "File does not exist");
                    return;
                }
                File chaosChannelMappingConfigFile =
                        new File(context.getClass().getClassLoader().getResource(chaosChannelMappingConfigFilename).toURI());
                context.setChaosChannelMappingConfigFile(chaosChannelMappingConfigFile);
            }
            catch (NullPointerException ex) {
                assertFalse(false, "File does not exist");
                return;
            }
            catch (FileNotFoundException fnfEx) {
                assertFalse(false, "File does not exist");
                return;
            }
            ChannelMapper channelMapper = ChannelMapper.getChannelMapper(context);
            assertEquals("Ukendt", channelMapper.getChaosChannel(ch1));
        }
        finally {
            Files.move(moveB, moveA, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
