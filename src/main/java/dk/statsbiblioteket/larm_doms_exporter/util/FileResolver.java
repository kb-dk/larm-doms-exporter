package dk.statsbiblioteket.larm_doms_exporter.util;

import java.io.File;

public interface FileResolver {

    /**
     * Finds the unique media file corresponding to the given doms uuid.
     * @param uuid the doms uuid (without any namespace prefix)
     * @return the File which is guaranteed to exist or null if no unique file could be found.
     */
    File findFile(String uuid);

}
