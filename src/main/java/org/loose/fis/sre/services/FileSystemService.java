package org.loose.fis.sre.services;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemService {
    public static String APPLICATION_FOLDER = ".registration-example";
    private static final String USER_FOLDER = System.getProperty("user.home");

    public static Path getPathToFile(String... path) {
        return getApplicationHomeFolder().resolve(Paths.get(".", path));
    }

    public static Path getApplicationHomeFolder() {
        return Paths.get(USER_FOLDER, APPLICATION_FOLDER);
    }

    static void initDirectory() {
        Path applicationHomePath = getApplicationHomeFolder();
        if (!Files.exists(applicationHomePath))
            applicationHomePath.toFile().mkdirs();
    }
}
