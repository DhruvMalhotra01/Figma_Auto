package generated.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/** Generated from Figma design context. Locators are suggestions and require live DOM validation. */
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By welcomeHeading = By.xpath("//*[normalize-space()='Welcome Back 👋']");
    private final By welcomeMessageText = By.xpath("//*[contains(text(), \"Today is a new day\")]");
    private final By emailInput = By.xpath("//input[@type='email' or @placeholder='Email']");
    private final By passwordInput = By.xpath("//input[@type='password']");
    private final By forgotPasswordLink = By.xpath("//a[normalize-space()='Forgot Password?']");
    private final By mainButton = By.xpath("//button[normalize-space()='Sign in' or normalize-space()='Main Button']");
    private final By signUpLink = By.xpath("//*[contains(text(), 'Sign up')]");
    private final By copyrightFooterText = By.xpath("//*[contains(text(), 'ALL RIGHTS RESERVED')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Element: Welcome Heading */
    public String getWelcomeText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeHeading)).getText();
    }

    /** Element: Welcome Message */
    public String getWelcomeMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessageText)).getText();
    }

    /** Element: Email Input Field */
    public void enterEmail(String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        element.clear();
        element.sendKeys(value);
    }

    /** Element: Password Input Field */
    public void enterPassword(String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        element.sendKeys(value);
    }

    /** Element: Forgot Password Link */
    public void clickForgotPassword() {
        wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink)).click();
    }

    /** Element: Main Button */
    public void clickMainButton() {
        wait.until(ExpectedConditions.elementToBeClickable(mainButton)).click();
    }

    /** Element: Sign Up Link */
    public void clickSignUp() {
        wait.until(ExpectedConditions.elementToBeClickable(signUpLink)).click();
    }

    /** Element: Copyright Footer */
    public String getCopyrightText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(copyrightFooterText)).getText();
    }

}
