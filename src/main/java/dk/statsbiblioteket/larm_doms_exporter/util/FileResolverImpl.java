package dk.statsbiblioteket.larm_doms_exporter.util;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResolverImpl implements FileResolver {

    File rootDirectory;
    int fileDepth;

    public FileResolverImpl(String rootDirectoryString, int fileDepth) {
          this.fileDepth = fileDepth;
          this.rootDirectory = new File(rootDirectoryString);
          if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
              throw new IllegalArgumentException(rootDirectoryString + " does not represent a directory.");
          }
    }

    @Override
    public File findFile(final String uuid) {
        Path filePath = rootDirectory.toPath();
        if (fileDepth > 0) {
            for (int i = 1; i <= fileDepth; i++) {
                filePath = filePath.resolve(Paths.get("" + uuid.charAt(i - 1)));
            }
        }
        File mediaDir = filePath.toFile();
        if (!mediaDir.exists() || !mediaDir.isDirectory()) {
            return null;
        }
        File[] mediaFiles = mediaDir.listFiles(pathname -> pathname.getName().startsWith(uuid));
        if (mediaFiles != null && mediaFiles.length == 1) {
            return mediaFiles[0];
        } else {
            return null;
        }
    }



}
