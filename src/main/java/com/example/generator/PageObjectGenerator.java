package com.example.generator;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class PageObjectGenerator {
    public Path generate(String requestedClassName,
                         List<JsonNode> elements,
                         Path outputDirectory) throws IOException {
        String className = javaIdentifier(requestedClassName, "GeneratedPage");
        if (!Character.isUpperCase(className.charAt(0))) {
            className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        }
        if (!className.endsWith("Page")) className += "Page";

        String nl = "\n";
        StringBuilder code = new StringBuilder();
        code.append("package generated.pages;").append(nl).append(nl)
            .append("import org.openqa.selenium.By;").append(nl)
            .append("import org.openqa.selenium.WebDriver;").append(nl)
            .append("import org.openqa.selenium.WebElement;").append(nl)
            .append("import org.openqa.selenium.support.ui.ExpectedConditions;").append(nl)
            .append("import org.openqa.selenium.support.ui.WebDriverWait;").append(nl)
            .append("import java.time.Duration;").append(nl).append(nl)
            .append("/** Generated from Figma design context. Locators are suggestions and require live DOM validation. */").append(nl)
            .append("public class ").append(className).append(" {").append(nl)
            .append("    private final WebDriver driver;").append(nl)
            .append("    private final WebDriverWait wait;").append(nl).append(nl);

        for (JsonNode e : elements) {
            String field = javaIdentifier(e.path("fieldName").asText("element"), "element");
            String strategy = e.path("locatorStrategy").asText();
            String value = e.path("locatorValue").asText();
            code.append("    private final By ").append(field)
                .append(" = By.").append(byMethod(strategy)).append("(\"")
                .append(javaString(value)).append("\");").append(nl);
        }

        code.append(nl).append("    public ").append(className).append("(WebDriver driver) {").append(nl)
            .append("        this.driver = driver;").append(nl)
            .append("        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));").append(nl)
            .append("    }").append(nl).append(nl);

        for (JsonNode e : elements) {
            String field = javaIdentifier(e.path("fieldName").asText("element"), "element");
            String method = javaIdentifier(e.path("methodName").asText("useElement"), "useElement");
            String action = e.path("action").asText("NONE").toUpperCase(Locale.ROOT);
            String name = e.path("name").asText(field).replace("*/", "* /").replace("\n", " ");

            code.append("    /** Element: ").append(name).append(" */").append(nl);
            switch (action) {
                case "TYPE" -> code.append("    public void ").append(method).append("(String value) {").append(nl)
                        .append("        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(")
                        .append(field).append("));").append(nl)
                        .append("        element.clear();").append(nl)
                        .append("        element.sendKeys(value);").append(nl)
                        .append("    }").append(nl).append(nl);
                case "CLICK" -> code.append("    public void ").append(method).append("() {").append(nl)
                        .append("        wait.until(ExpectedConditions.elementToBeClickable(")
                        .append(field).append(")).click();").append(nl)
                        .append("    }").append(nl).append(nl);
                case "READ" -> code.append("    public String ").append(method).append("() {").append(nl)
                        .append("        return wait.until(ExpectedConditions.visibilityOfElementLocated(")
                        .append(field).append(")).getText();").append(nl)
                        .append("    }").append(nl).append(nl);
                default -> code.append("    public By ").append(method).append("Locator() {").append(nl)
                        .append("        return ").append(field).append(";").append(nl)
                        .append("    }").append(nl).append(nl);
            }
        }

        code.append("}").append(nl);
        Files.createDirectories(outputDirectory);
        Path output = outputDirectory.resolve(className + ".java");
        Files.writeString(output, code.toString(), StandardCharsets.UTF_8);
        return output;
    }

    private String byMethod(String strategy) {
        return switch (strategy) {
            case "id" -> "id";
            case "name" -> "name";
            case "cssSelector" -> "cssSelector";
            case "xpath" -> "xpath";
            case "className" -> "className";
            default -> throw new IllegalArgumentException("Unsupported locator strategy: " + strategy);
        };
    }

    private String javaIdentifier(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String cleaned = value.replaceAll("[^A-Za-z0-9_$]", "");
        if (cleaned.isBlank()) return fallback;
        if (!Character.isJavaIdentifierStart(cleaned.charAt(0))) cleaned = "_" + cleaned;
        return cleaned;
    }

    private String javaString(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
