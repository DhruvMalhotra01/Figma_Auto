package com.example.browser;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

public class DomExtractor {
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> extractInteractiveElements(WebDriver driver) {
        String script = """
            const selectors = 'input, button, textarea, select, a, [role="button"], [role="textbox"]';
            return Array.from(document.querySelectorAll(selectors))
              .filter(e => {
                const style = window.getComputedStyle(e);
                const r = e.getBoundingClientRect();
                return style.display !== 'none' &&
                       style.visibility !== 'hidden' &&
                       r.width > 0 && r.height > 0;
              })
              .slice(0, 100)
              .map((e, i) => {
                const label = e.labels && e.labels.length
                  ? Array.from(e.labels).map(x => x.innerText.trim()).join(' ')
                  : '';
                return {
                  index: i,
                  tag: e.tagName.toLowerCase(),
                  type: e.getAttribute('type') || '',
                  id: e.id || '',
                  name: e.getAttribute('name') || '',
                  placeholder: e.getAttribute('placeholder') || '',
                  ariaLabel: e.getAttribute('aria-label') || '',
                  role: e.getAttribute('role') || '',
                  text: (e.innerText || e.value || '').trim().slice(0, 100),
                  label: label.slice(0, 100),
                  title: e.getAttribute('title') || '',
                  testId: e.getAttribute('data-testid') || '',
                  classes: (typeof e.className === 'string' ? e.className : '').slice(0, 150),
                  href: e.tagName.toLowerCase() === 'a' ? (e.getAttribute('href') || '') : ''
                };
              });
            """;

        Object result = ((JavascriptExecutor) driver).executeScript(script);
        return (List<Map<String, Object>>) result;
    }
}
