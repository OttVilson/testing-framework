package com.vilson.tests.model.pom;

// https://stackoverflow.com/questions/26304224/find-element-by-attribute
// https://stackoverflow.com/questions/44054500/how-to-type-text-into-code-mirror-line-using-selenium
// https://stackoverflow.com/questions/26304224/find-element-by-attribute
// https://stackoverflow.com/questions/44054500/how-to-type-text-into-code-mirror-line-using-selenium
// https://stackoverflow.com/questions/59637048/how-to-find-element-by-part-of-its-id-name-in-selenium-with-python
// https://stackoverflow.com/questions/1604471/how-can-i-find-an-element-by-css-class-with-xpath

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class DocsPageFactory {

    @FindBy(xpath = "//div[@id=\"searchInDocument\"]//div[contains(concat(\" \", normalize-space(@class), \" \"), \" CodeMirror \")]")
    private WebElement keywordSearchCodeMirror;
    @FindBy(xpath = "//div[@id=\"searchInDocument\"]//textarea")
    private WebElement keywordSearchTextArea;

    @FindBy(xpath = "//div[contains(concat(\" \", normalize-space(@class), \" \"), \" snippetListContainer \")]" +
            "//div[contains(concat(\" \", normalize-space(@class), \" \"), \" statementsListInteractive \")]")
    private WebElement scrollableSnippetListWrapper;
    @FindBy(xpath = "//div[contains(concat(\" \", normalize-space(@class), \" \"), \" snippetListContainer \")]" +
            "//div[contains(concat(\" \", normalize-space(@class), \" \"), \" ReactVirtualized__Grid__innerScrollContainer \")]")
    private WebElement snippetList;
    @FindBy(xpath = "(//div[contains(concat(\" \", normalize-space(@class), \" \"), \" snippetItem \")])[last()]")
    private WebElement lastSnippet;

    @FindBy(xpath = "(//span[@hit=\"regular\"])[last()]")
    private WebElement lastHitInDocumentViewerIFrame;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String highlightedClass = "x-grid3-row-blue";

    public DocsPageFactory(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    public void searchAdditionalKeywordsFromDocument(String keyword) {
        getKeywordSearchAreaFocus();
        keywordSearchTextArea.sendKeys(keyword + Keys.ENTER);
    }

    private void getKeywordSearchAreaFocus() {
        wait.until(ExpectedConditions.elementToBeClickable(keywordSearchCodeMirror));
        keywordSearchCodeMirror.click();
    }

    public void scrollAdditionalKeywordsResultsListByPercentage(int percentage) {
        wait.until(ExpectedConditions.visibilityOfAllElements(snippetList, scrollableSnippetListWrapper));

        int totalLength = removeUnit(snippetList.getCssValue("height"));
        int scrollByHowMuch = (totalLength * percentage) / 100;
        String script = "arguments[0].scrollTop = " + scrollByHowMuch;
        ((JavascriptExecutor) driver).executeScript(script, scrollableSnippetListWrapper);
    }

    public String getRelevantIdFromSnippet(WebElement snippet) {
        WebElement button = snippet.findElement(By.className("snippet-copy-button"));
        String dataClipboardText = button.getAttribute("data-clipboard-text");
        return getId(dataClipboardText);
    }

    public WebElement getLastVisibleSnippet() {
        wait.until(ExpectedConditions.visibilityOfAllElements(lastSnippet));
        return lastSnippet;
    }

    public boolean areElementsWithIdSuffixMarkedAsHit(String idSuffix) {
        return withinIFrame(() -> {
            List<WebElement> hits = elementsByIdSuffix(idSuffix);
            return hits.stream().allMatch(w -> w.getAttribute("hit").equals("regular"));
        });
    }

    public boolean areElementsWithIdSuffixHighlighted(String idSuffix) {
        return withinIFrame(() -> {
            List<WebElement> elements = elementsByIdSuffix(idSuffix);
            return elements.stream()
                    .map(w -> w.getAttribute("class").split("\\s+"))
                    .map(List::of)
                    .allMatch(l -> l.contains(highlightedClass));
        });
    }

    public String getIdOfLastHitInDocumentViewer() {
        return withinIFrame(() -> lastHitInDocumentViewerIFrame.getAttribute("id"));
    }

    private <T> T withinIFrame(Supplier<T> procedure) {
        driver.switchTo().frame("content-1");
        T result = procedure.get();
        driver.switchTo().parentFrame();
        return result;
    }

    private List<WebElement> elementsByIdSuffix(String idSuffix) {
        return driver.findElements(By.xpath("//span[starts-with(@id, \"" + idSuffix + "\")]"));
    }

    private int removeUnit(String lengthInPixels) {
        int indexOfUnitStart = lengthInPixels.lastIndexOf("px");
        String numberWithoutUnit = lengthInPixels.substring(0, indexOfUnitStart);
        return Integer.parseInt(numberWithoutUnit);
    }

    private String getId(String dataClipboardText) {
        String attribute = "stmt=";
        int startIndex = dataClipboardText.indexOf(attribute);
        int stopIndex = dataClipboardText.indexOf("&", startIndex);
        return dataClipboardText.substring(startIndex + attribute.length(), stopIndex);
    }
}
