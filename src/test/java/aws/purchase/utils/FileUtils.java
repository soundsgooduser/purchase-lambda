package aws.purchase.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public final class FileUtils {
    private static final String BUILD_DIRECTORY = "target";

    private FileUtils() {
    }

    public static File find(final String fileName) throws FileNotFoundException {
        final File directory = new File(BUILD_DIRECTORY);
        return Arrays.asList(directory.listFiles()).stream()
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException(fileName));
    }
}
