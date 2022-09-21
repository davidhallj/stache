package io.github.davidhallj.stache.util;

import java.io.File;
import java.nio.file.Path;

public class StacheTestUtil {

    public static boolean deleteDirectory(Path directoryToBeDeleted) {
        return deleteDirectory(directoryToBeDeleted.toFile());
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }



}
