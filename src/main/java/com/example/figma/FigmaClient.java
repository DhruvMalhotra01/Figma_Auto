package com.example.figma;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FigmaClient {
    private static final Pattern FIGMA_URL =
            Pattern.compile("figma\\.com/(?:design|file|proto|board)/([^/?]+)");

    private final String token;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public FigmaClient(String token) {
        this.token = token;
    }

    public String getDesignSummary(String urlOrKey) throws Exception {
        String fileKey = extractFileKey(urlOrKey);
        String encodedKey = URLEncoder.encode(fileKey, StandardCharsets.UTF_8);
        URI uri = URI.create(
                "https://api.figma.com/v1/files/" + encodedKey + "?depth=5");

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(45))
                .header("X-Figma-Token", token)
                .GET().build();

        HttpResponse<String> response = http.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Figma API failed with HTTP " + response.statusCode()
                    + ". Verify FIGMA_TOKEN, file access, file key and API permissions. "
                    + "Response: " + truncate(response.body(), 500));
        }

        JsonNode root = mapper.readTree(response.body());
        StringBuilder summary = new StringBuilder();
        summary.append("Figma file: ")
                .append(root.path("name").asText("Unknown"))
                .append('\n');

        int[] count = {0};
        appendNodes(root.path("document"), summary, 0, count);

        if (count[0] == 0) {
            throw new IllegalStateException(
                    "No Figma nodes were extracted from the file.");
        }
        return summary.toString();
    }

    private void appendNodes(JsonNode node, StringBuilder out,
                             int depth, int[] count) {
        if (node == null || node.isMissingNode() || count[0] >= 180) {
            return;
        }

        String type = node.path("type").asText("");
        String name = node.path("name").asText("");
        if (!type.isBlank()) {
            out.append("  ".repeat(Math.min(depth, 8)))
               .append(type).append(": ")
               .append(name.isBlank() ? "(unnamed)" : name);

            if (node.has("characters") && node.path("characters").isTextual()) {
                String text = node.path("characters").asText().replaceAll("\\s+", " ").trim();
                if (!text.isBlank()) {
                    if (text.length() > 100) text = text.substring(0, 100);
                    out.append(" [text=\"").append(text.replace("\"", "'"))
                       .append("\"]");
                }
            }
            if (node.has("visible")) {
                out.append(" [visible=").append(node.path("visible").asBoolean()).append(']');
            }
            out.append('\n');
            count[0]++;
        }

        JsonNode children = node.path("children");
        if (children.isArray() && depth < 7) {
            for (JsonNode child : children) {
                if (count[0] >= 180) break;
                appendNodes(child, out, depth + 1, count);
            }
        }
    }

    private String extractFileKey(String input) {
        String value = input.trim();
        Matcher matcher = FIGMA_URL.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        if (value.matches("[A-Za-z0-9_-]+")) {
            return value;
        }
        throw new IllegalArgumentException(
                "Could not extract Figma file key from input: " + input);
    }

    private String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max);
    }
}
