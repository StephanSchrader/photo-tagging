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
        var promptHeadline = "You are a newspaper editor. Describe the topic of the image in a headline with less than 8 words.";
        var promptAbstract = "Your role is a photographer. Create a very precise summary about the image with less than 300 characters.";
        var promptKeywords = "Your role is a photographer. Find %s single keywords describing the image.".formatted(5);

        invokeLLm(modelLLaVa, promptHeadline, image);
        invokeLLm(modelLLaVa, promptAbstract, image);
        invokeLLm(modelLLaVa, promptKeywords, image);
    }

    private void invokeLLm(String modelLLaVa, String prompt, String image) {
        try (HttpClient http = HttpClient.newBuilder().build()) {
            var payload = """
                    {
                        "model": "%s",
                        "prompt": "%s",
                        "stream": false,
                        "temperature": 10,
                        "images": ["%s"]
                    }
                    """.formatted(modelLLaVa, prompt, image);

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
