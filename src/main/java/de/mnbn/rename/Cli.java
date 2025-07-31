package de.mnbn.rename;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Cli {

    public static void main(String[] args) throws Exception {

        var dir = Paths.get(args[0]);
        try (var files = Files.list(dir)) {
            files.forEach(Cli::rename);
        }

    }

    private static void rename(Path file) {
        var lastModifiedFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").withZone(ZoneId.systemDefault());

        try {
            var lastModified = Files.getLastModifiedTime(file);
            var formattedTime = lastModifiedFormatter.format(lastModified.toInstant());

            var fileName = file.getFileName().toString();
            var newFileName = String.format("DSC_%s.%s", formattedTime, extractExtension(fileName));
            if (!newFileName.equals(fileName)) {
                var newFilePath = file.getParent().resolve(newFileName);
                Files.move(file, newFilePath);
                System.out.println("Renamed: " + fileName + " to " + newFileName);
            } else {
                System.out.println("No rename needed for: " + fileName);
            }
        } catch (Exception e) {
            System.getLogger(Cli.class.getName()).log(System.Logger.Level.ERROR, "Error renaming file: " + file, e);
        }

    }

    private static String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return ""; // Return empty string if no extension found
    }

}
