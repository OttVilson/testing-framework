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

    @Test
    public void AdditionalKeywordsFlowTest() {
        String docUrl = composeUrl(environment, "PR-386ea743f2a90399fb0e4300ddf37d0697abc743");
        driver.get(docUrl);
        DocsPageFactory docsPage = new DocsPageFactory(driver);

        docsPage.searchAdditionalKeywordsFromDocument("AlphaSense");
        docsPage.scrollAdditionalKeywordsResultsListByPercentage(100);

        WebElement lastSnippet = docsPage.getLastVisibleSnippet();
        String idSuffixCorrespondingToHit = docsPage.getRelevantIdFromSnippet(lastSnippet);
        String idOfLastHit = docsPage.getIdOfLastHitInDocumentViewer();
        Assert.assertTrue(idOfLastHit.startsWith(idSuffixCorrespondingToHit),
                "The last snippet should have access to the id suffix of the hits in document viewer.");

        boolean elementsWithIdSuffixAreMarkedAsHit =
                docsPage.areElementsWithIdSuffixMarkedAsHit(idSuffixCorrespondingToHit);
        Assert.assertTrue(elementsWithIdSuffixAreMarkedAsHit);
        boolean elementsAreNotHighlightedBeforeClick =
                !docsPage.areElementsWithIdSuffixHighlighted(idSuffixCorrespondingToHit);
        Assert.assertTrue(elementsAreNotHighlightedBeforeClick);

        lastSnippet.click();
        boolean elementsAreHighlightedAfterClick =
                docsPage.areElementsWithIdSuffixHighlighted(idSuffixCorrespondingToHit);
        Assert.assertTrue(elementsAreHighlightedAfterClick);

        driver.close();
    }

    private static String composeUrl(Environment environment, String docId) {
        String baseUrl = environment.getUrl();
        String path = "doc";
        return String.format("%s/%s/%s", baseUrl, path, docId);
    }
}
