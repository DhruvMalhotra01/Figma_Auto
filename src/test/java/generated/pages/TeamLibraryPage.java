package generated.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/** Generated from Figma design context. Locators are suggestions and require live DOM validation. */
public class TeamLibraryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By buildYourOwnTeamLibraryText = By.xpath("//*[normalize-space()='Build your own team library']");
    private final By editTeamColorStylesText = By.xpath("//*[normalize-space()='Edit team color styles']");
    private final By createColorStylesText = By.xpath("//*[normalize-space()='Create color styles']");
    private final By useColorStylesText = By.xpath("//*[normalize-space()='Use color styles']");
    private final By button = By.xpath("//button[normalize-space()='Button']");
    private final By editTextStylesText = By.xpath("//*[normalize-space()='Edit text styles']");
    private final By createTextStylesText = By.xpath("//*[normalize-space()='Create text styles']");
    private final By useTextStylesText = By.xpath("//*[normalize-space()='Use text styles']");
    private final By createComponentsText = By.xpath("//*[normalize-space()='Create components']");
    private final By useComponentsText = By.xpath("//*[normalize-space()='Use components']");
    private final By publishYourTeamLibraryText = By.xpath("//*[normalize-space()='Publish your Team Library']");

    public TeamLibraryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Element: Build your own team library text */
    public String getBuildYourOwnTeamLibraryText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(buildYourOwnTeamLibraryText)).getText();
    }

    /** Element: Edit team color styles text */
    public String getEditTeamColorStylesText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(editTeamColorStylesText)).getText();
    }

    /** Element: Create color styles text */
    public String getCreateColorStylesText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(createColorStylesText)).getText();
    }

    /** Element: Use color styles text */
    public String getUseColorStylesText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(useColorStylesText)).getText();
    }

    /** Element: Button */
    public void clickButton() {
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    }

    /** Element: Edit text styles text */
    public String getEditTextStylesText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(editTextStylesText)).getText();
    }

    /** Element: Create text styles text */
    public String getCreateTextStylesText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(createTextStylesText)).getText();
    }

    /** Element: Use text styles text */
    public String getUseTextStylesText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(useTextStylesText)).getText();
    }

    /** Element: Create components text */
    public String getCreateComponentsText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(createComponentsText)).getText();
    }

    /** Element: Use components text */
    public String getUseComponentsText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(useComponentsText)).getText();
    }

    /** Element: Publish your Team Library text */
    public String getPublishYourTeamLibraryText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(publishYourTeamLibraryText)).getText();
    }

}
