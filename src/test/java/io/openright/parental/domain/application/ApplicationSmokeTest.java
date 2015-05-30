package io.openright.parental.domain.application;

import io.openright.infrastructure.util.ExceptionUtil;
import io.openright.parental.server.ParentalBenefitsTestConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationSmokeTest {

    private static final ParentalBenefitsTestConfig config = ParentalBenefitsTestConfig.instance();
    private static final RemoteWebDriver browser = createRemoteDriver();


    @BeforeClass
    public static void setup() throws Exception {
        browser.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void stopBrowser() {
        browser.close();
    }


    @Test
    public void shouldSubmitTestApplication() throws Exception {
        browser.get("http://dev-foreldrepenger.herokuapp.com");

        login();
        clickButton("Fyll inn enkel testsøknad");

        selectRadioButton("application[fillApplication]", "true");
        selectRadioButton("application[additionalInfo]", "false");

        browser.findElementByName("application[startDate]").sendKeys("12/12/2015");
        String endDate = browser.findElementByName("application[maternityLeave][][endDate]")
                .getAttribute("value");
        assertThat(endDate).isEqualTo("2015-12-21");

        String hash = browser.getCurrentUrl().split("#")[1];
        clickButton("Fullfør søknad");

        WebElement applicationsTable = browser.findElementById("applications");
        WebElement editButton = applicationsTable.findElement(By.cssSelector("tbody tr button"));
        assertThat(hash)
                .isEqualTo("applications/edit/" + editButton.getAttribute("data-application-id"));
    }

    private void selectRadioButton(String name, String value) {
        browser.findElement(By.cssSelector("input[type=radio][name = '" + name + "'][value = '" + value + "']"))
            .click();
    }

    private void clickButton(String text) {
        browser.findElement(By.xpath("//button[text() = '" + text + "']")).click();
    }

    private void login() {
        browser.findElement(By.id("personId")).sendKeys("17048316526");
        browser.findElement(By.id("personId")).submit();
    }

    public static RemoteWebDriver createRemoteDriver() {
        try {
            URL url = new URL("http://" + config.getSauceLabsAuthentication() + "@ondemand.saucelabs.com:80/wd/hub");
            return new RemoteWebDriver(url, DesiredCapabilities.chrome());
        } catch (MalformedURLException e) {
            throw ExceptionUtil.soften(e);
        }
    }

}
