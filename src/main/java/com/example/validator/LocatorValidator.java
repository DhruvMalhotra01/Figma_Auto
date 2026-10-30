package com.example.validator;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

public class LocatorValidator {
    public void validate(WebDriver driver, JsonNode element) {
        String strategy = element.path("locatorStrategy").asText("");
        String value = element.path("locatorValue").asText("");
        String expectedTag = element.path("tag").asText("").toLowerCase(Locale.ROOT);
        String action = element.path("action").asText("NONE").toUpperCase(Locale.ROOT);

        if (value.isBlank()) {
            throw new IllegalArgumentException("Empty locator value");
        }

        By by = toBy(strategy, value);
        List<WebElement> matches = driver.findElements(by);

        if (matches.size() != 1) {
            throw new IllegalArgumentException(
                    "Expected exactly one match, got " + matches.size());
        }

        WebElement match = matches.get(0);
        String actualTag = match.getTagName().toLowerCase(Locale.ROOT);

        if (!expectedTag.isBlank() && !actualTag.equals(expectedTag)) {
            throw new IllegalArgumentException(
                    "Expected tag " + expectedTag + " but found " + actualTag);
        }

        if (!match.isDisplayed()) {
            throw new IllegalArgumentException("Element is not displayed");
        }

        if ("TYPE".equals(action)
                && !List.of("input", "textarea", "select").contains(actualTag)) {
            throw new IllegalArgumentException(
                    "TYPE action is not suitable for tag " + actualTag);
        }

        if ("CLICK".equals(action)
                && !List.of("button", "a", "input").contains(actualTag)
                && !"button".equals(match.getAttribute("role"))) {
            throw new IllegalArgumentException(
                    "CLICK action may not be suitable for tag " + actualTag);
        }
    }

    public By toBy(String strategy, String value) {
        return switch (strategy) {
            case "id" -> By.id(value);
            case "name" -> By.name(value);
            case "cssSelector" -> By.cssSelector(value);
            case "xpath" -> By.xpath(value);
            case "className" -> By.className(value);
            default -> throw new IllegalArgumentException(
                    "Unsupported locator strategy: " + strategy);
        };
    }
}
