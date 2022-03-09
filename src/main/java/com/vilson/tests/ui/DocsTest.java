package com.vilson.tests.ui;

import com.vilson.environment.Environment;
import com.vilson.tests.helpers.TestInitHelpers;
import com.vilson.tests.model.pom.DocsPageFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Objects;

public class DocsTest {
    private WebDriver driver;
    private Environment environment;

    @BeforeMethod
    public void initialize(ITestContext testContext) {
        environment = (Environment) testContext.getAttribute("environment");
        environment = Objects.requireNonNullElseGet(environment, TestInitHelpers::getProduction);
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (environment.isHeadless())
            options.addArguments("headless");
        driver = new ChromeDriver(options);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void AdditionalKeywordsFlowTest() {
        String docUrl = composeUrl(environment, "PR-386ea743f2a90399fb0e4300ddf37d0697abc743");

        Reporter.log(String.format("Opening web page %s with browser %s.", docUrl, driver.getClass().getSimpleName()));
        driver.get(docUrl);
        DocsPageFactory docsPage = new DocsPageFactory(driver);

        Reporter.log("Typing in the word AlphaSense to Additional Keywords input field.", 2);
        docsPage.searchAdditionalKeywordsFromDocument("AlphaSense");
        Reporter.log("Scrolling down to the last hit.", 2);
        docsPage.scrollAdditionalKeywordsResultsListByPercentage(100);

        WebElement lastSnippet = docsPage.getLastVisibleSnippet();
        String idSuffixCorrespondingToHit = docsPage.getRelevantIdFromSnippet(lastSnippet);
        Reporter.log(
                String.format("The id related to last hit in Document Viewer starts with %s.", idSuffixCorrespondingToHit),
                2);
        String idOfLastHit = docsPage.getIdOfLastHitInDocumentViewer();
        Assert.assertTrue(idOfLastHit.startsWith(idSuffixCorrespondingToHit),
                "The last snippet should have access to the id suffix of the hits in document viewer.");

        Reporter.log("Checking that the relevant sentence in document viewer is marked as hit.", 2);
        boolean elementsWithIdSuffixAreMarkedAsHit =
                docsPage.areElementsWithIdSuffixMarkedAsHit(idSuffixCorrespondingToHit);
        Assert.assertTrue(elementsWithIdSuffixAreMarkedAsHit);
        Reporter.log("Checking that the relevant sentence in document viewer is not highlighted before clicking on the " +
                "keyword search result.", 2);
        boolean elementsAreNotHighlightedBeforeClick =
                !docsPage.areElementsWithIdSuffixHighlighted(idSuffixCorrespondingToHit);
        Assert.assertTrue(elementsAreNotHighlightedBeforeClick);

        Reporter.log("Click on the last search result (snippet).", 2);
        lastSnippet.click();

        Reporter.log("Check that the sentence in Document Viewer is now highlighted.", 2);
        boolean elementsAreHighlightedAfterClick =
                docsPage.areElementsWithIdSuffixHighlighted(idSuffixCorrespondingToHit);
        Assert.assertTrue(elementsAreHighlightedAfterClick);

        Reporter.log("Closing the browser.");
    }

    private static String composeUrl(Environment environment, String docId) {
        String baseUrl = environment.getUrl();
        String path = "doc";
        return String.format("%s/%s/%s", baseUrl, path, docId);
    }
}
