package io.openright.parental.domain.application;

import io.openright.infrastructure.util.ExceptionUtil;
import io.openright.infrastructure.util.IOUtil;
import io.openright.parental.domain.applicant.Applicant;
import io.openright.parental.server.ParentalBenefitsConfig;
import io.openright.parental.server.ParentalBenefitsTestConfig;
import io.openright.parental.server.test.ParentalBenefitsDevServer;
import io.openright.parental.server.test.SampleData;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationWebTest {

    private static final ParentalBenefitsConfig config = ParentalBenefitsTestConfig.instance();
    private static final ParentalBenefitsDevServer server = new ParentalBenefitsDevServer(config);
    private static final RemoteWebDriver browser = createChromeDriver();


    @BeforeClass
    public static void setup() throws Exception {
        server.start();
        browser.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void stopBrowser() {
        browser.close();
    }

    private Applicant applicant = SampleData.sampleApplicant();

    @Before
    public void registerApplicant() {
        server.getApplicantService().insert(applicant);
    }

    @Test
    public void shouldSubmitTestApplication() throws Exception {
        browser.get(server.getURI().toString());

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
        browser.findElement(By.id("personId")).sendKeys(applicant.getId());
        browser.findElement(By.id("personId")).submit();
    }

    public static ChromeDriver createChromeDriver() {
        System.setProperty("webdriver.chrome.driver", getChromeDriver().getPath());
        return new ChromeDriver();
    }

    private static File getChromeDriver() {
        File driverFile = new File("target/chromedriver.exe");
        if (!driverFile.exists()) {
            downloadDriver(driverFile);
        }
        return driverFile;
    }

    private static void downloadDriver(File driverFile) {
        try {
            URL chromeDriverUrl = new URL("http://chromedriver.storage.googleapis.com/");
            String chromeDriverVersion = IOUtil.toString(new URL(chromeDriverUrl, "LATEST_RELEASE"))
                    .orElseThrow(() -> new RuntimeException("Can't read " + chromeDriverUrl));

            URL latestDriverVersion = new URL(chromeDriverUrl, chromeDriverVersion + "/chromedriver_win32.zip");
            File zipFile = IOUtil.copy(latestDriverVersion, new File("target/"));
            IOUtil.extractZipEntry(zipFile, driverFile);
        } catch (MalformedURLException e) {
            throw ExceptionUtil.soften(e);
        }
    }
}
