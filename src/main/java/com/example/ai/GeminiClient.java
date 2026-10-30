package com.example.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class GeminiClient {
    private final String apiKey;
    private final String model;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public GeminiClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    public String proposePageObjectFromFigma(String figmaSummary) throws Exception {
        String prompt = """
            You are assisting with a Java Selenium Page Object generator.
            You only have a Figma design tree, not the application's HTML DOM.
            Return JSON only, with this exact structure:
            {
              "className": "LoginPage",
              "elements": [
                {
                  "name": "Email input",
                  "fieldName": "emailInput",
                  "methodName": "enterEmail",
                  "tag": "input",
                  "action": "TYPE",
                  "locatorStrategy": "xpath",
                  "locatorValue": "//input[@type='email']",
                  "reason": "Inferred from the email input shown in the design"
                }
              ]
            }

            Rules:
            - Generate a Page Object class name in PascalCase ending with Page.
            - Use only elements and text evidenced by the supplied Figma design summary.
            - Figma does not reveal actual HTML ids, names, classes, or attributes. Do not claim these are verified.
            - Suggest plausible Selenium locators based on visible text and semantic role where possible.
            - For buttons or links with visible text, prefer XPath based on normalized text, for example //button[normalize-space()='Sign in'].
            - For inputs, suggest a semantic locator only when the design gives enough evidence; otherwise use a clearly marked placeholder selector and explain the assumption in reason.
            - locatorStrategy must be one of: id, name, cssSelector, xpath, className.
            - Do not invent IDs, data-testid values, or CSS classes as if they are known to exist.
            - Choose action TYPE for input/textarea/select fields, CLICK for buttons and links, READ for meaningful text elements, otherwise NONE.
            - Generate valid Java identifiers for fieldName and methodName.
            - Include a reason that clearly states the locator is inferred from Figma and needs DOM validation.
            - Do not generate Java source code. Return JSON only.
            - Keep the output limited to at most 20 elements.

            FIGMA DESIGN SUMMARY:
            %s
            """.formatted(figmaSummary);

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.putArray("contents")
                .addObject()
                .putArray("parts")
                .addObject()
                .put("text", prompt);

        ObjectNode generationConfig = requestBody.putObject("generationConfig");
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("temperature", 0.1);

        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        URI uri = URI.create(
                "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + encodedKey);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = http.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Gemini API failed with HTTP " + response.statusCode()
                    + ". Verify API key, model name, quota and network. Response: "
                    + truncate(response.body(), 700));
        }

        JsonNode root = mapper.readTree(response.body());
        String text = root.path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asText("");

        if (text.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response: " + truncate(response.body(), 500));
        }
        return text;
    }

    private String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max);
    }
}
