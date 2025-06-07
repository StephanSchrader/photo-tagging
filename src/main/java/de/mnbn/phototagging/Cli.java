package de.mnbn.phototagging;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Command(name = "photo-tagging", mixinStandardHelpOptions = true,
        version = "photo-tagging 1.0",
        description = "Generate tags and headline for photos"
)
public class Cli implements Callable<Integer> {

    @Parameters(index = "0", description = "The file or folder to generate the photo metadata.")
    private Path source;

    @Option(names = {"-r", "--recursive"}, description = "If _file_ is a folder, than the entire subtree is scanned.")
    private boolean recursive = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Cli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {

        if (!Files.exists(source)) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Source: ''{0}'' doesn't exists", source);
            return 1;
        }

        if (Files.isDirectory(source)) {
            try (Stream<Path> files = Files.walk(source, recursive ? 30 : 1)) {
                files.filter(p -> !Files.isDirectory(p)).forEach(this::call);
            }

            return 0;
        } else {
            call(source);
        }

        return 0;
    }

    private void call(Path file) {
        try {
            new GenerateMetadataCmd().execute(file);
        } catch (RuntimeException e) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, e.getMessage());
        }
    }
}
