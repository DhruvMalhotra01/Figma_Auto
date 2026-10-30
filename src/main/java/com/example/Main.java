package com.example;

import com.example.ai.GeminiClient;
import com.example.figma.FigmaClient;
import com.example.generator.PageObjectGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("""
                Usage:
                  Main <Figma URL or file key>

                Example:
                  Main https://www.figma.com/design/FILE_KEY/design-name
                """);
            System.exit(1);
        }

        String figmaToken = requiredEnv("FIGMA_TOKEN");
        String geminiKey = requiredEnv("GEMINI_API_KEY");
        String model = System.getenv().getOrDefault(
                "GEMINI_MODEL", "gemini-3.5-flash");
        String figmaInput = args[0];

        // Step 1: Read the Figma design structure.
        FigmaClient figmaClient = new FigmaClient(figmaToken);
        String designSummary = figmaClient.getDesignSummary(figmaInput);
        System.out.println("Figma design summary loaded.");
        System.out.println(designSummary);

        // Step 2: Ask Gemini to suggest Page Object elements using Figma only.
        GeminiClient ai = new GeminiClient(geminiKey, model);
        String aiJson = ai.proposePageObjectFromFigma(designSummary);
        System.out.println("\nAI Page Object proposal:\n" + aiJson);

        // Step 3: Parse the proposal.
        JsonNode proposal = MAPPER.readTree(aiJson);
        JsonNode elements = proposal.path("elements");
        if (!elements.isArray() || elements.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned no elements. Review the AI response and prompt.");
        }

        List<JsonNode> generatedElements = new ArrayList<>();
        for (JsonNode element : elements) {
            if (element.path("name").asText().isBlank()
                    || element.path("fieldName").asText().isBlank()
                    || element.path("methodName").asText().isBlank()
                    || element.path("locatorStrategy").asText().isBlank()
                    || element.path("locatorValue").asText().isBlank()) {
                System.out.println("SKIPPED: incomplete AI proposal: " + element);
                continue;
            }
            generatedElements.add(element);
            System.out.println("SUGGESTED: " + element.path("name").asText()
                    + " -> " + element.path("locatorStrategy").asText()
                    + " = " + element.path("locatorValue").asText());
        }

        if (generatedElements.isEmpty()) {
            throw new IllegalStateException(
                    "No usable elements were returned by Gemini. No Page Object generated.");
        }

        // Step 4: Generate the Java Page Object. Locators are suggestions, not DOM-validated.
        String className = proposal.path("className").asText("GeneratedPage");
        Path output = new PageObjectGenerator().generate(
                className,
                generatedElements,
                Path.of("src", "test", "java", "generated", "pages"));

        System.out.println("\nGenerated Page Object: " + output.toAbsolutePath());
        System.out.println("IMPORTANT: Figma-only locators are suggestions. Review and validate them against the actual webpage DOM before using them in Selenium tests.");
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable: " + name);
        }
        return value;
    }
}
