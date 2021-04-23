package dk.statsbiblioteket.larm_doms_exporter.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileResolverImplTest {

    @Test
    public void findFile() {
        FileResolver fileResolver = new FileResolverImpl("src/test/mediaDir", 4);
        assertNotNull(fileResolver.findFile("ab4efoobar"));
        assertNull(fileResolver.findFile("ab4ebarfoo"));
        assertNull(fileResolver.findFile("abdgblah"));
        assertNull(fileResolver.findFile("abcded"));
    }
}