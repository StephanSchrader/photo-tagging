package de.mnbn.phototagging;

import net.coobird.thumbnailator.Thumbnailator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class GenerateMetadataCmd {

    public void execute(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("Must be a regular file: " + file.toString());
        }

        byte[] imageData;
        try (var in = Files.newInputStream(file)) {
            var out = new ByteArrayOutputStream();
            Thumbnailator.createThumbnail(in, out, 672, 672);
            imageData = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var modelLLaVa = "llava:v1.6";
        var modelBakllava = "bakllava";
        var modelJanus = "nn200433/Janus-Pro-1B";

        var image = Base64.getEncoder().encodeToString(imageData);

        var prompt = "Ihre Rolle ist die eines Zeitungsredakteurs. Beschreiben Sie das Thema des Bildes in einer Überschrift mit weniger als 8 Wörtern.";

        try (HttpClient http = HttpClient.newBuilder().build()) {
            var payload = """
                    {
                        "model": "%s",
                        "prompt": "%s",
                        "stream": false,
                        "temperature": 10,
                        "images": ["%s"]
                    }
                    """.formatted(modelJanus, prompt, image);

            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:11434/api/generate"))
                    .POST(BodyPublishers.ofString(payload))
                    .build();

            var response = http.send(request, BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
