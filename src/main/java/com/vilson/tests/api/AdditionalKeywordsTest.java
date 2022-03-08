package com.vilson.tests.api;

import com.vilson.environment.Environment;
import com.vilson.tests.helpers.TestInitHelpers;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ParamConfig.paramConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.Matchers.*;

public class AdditionalKeywordsTest {

    private RequestSpecification requestSpecification;
    private String docId;

    @BeforeMethod
    public void initialize(ITestContext testContext) {
        Environment environment = (Environment) testContext.getAttribute("environment");
        environment = Objects.requireNonNullElseGet(environment, TestInitHelpers::getProduction);
        String path = "/services/i/public-document-data/document/{docId}/keyword-search/";
        docId = "PR-386ea743f2a90399fb0e4300ddf37d0697abc743";

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri(environment.getUrl())
                .setBasePath(path)
                .addParam("keyword", "AlphaSense")
                .addParam("slop", 15)
                .addParam("positiveOnly", false)
                .addParam("negativeOnly", false)
                .addParam("released", 1640995199000L)
                .addPathParam("docId", docId)
                .addHeader("accept", "application/json")
                .setConfig(config().paramConfig(paramConfig().replaceAllParameters()));

        requestSpecification = builder.build();
    }

    @Test
    public void givenBothNegativeAndPositiveOnlyAreSetToTrue_shouldRespondWithBadRequest() {
        requestSpecification
                .param("negativeOnly", true)
                .param("positiveOnly", true);

        Response response = given().spec(requestSpecification)
                .get()
                .then()
                .statusCode(400)
                .body("message", containsStringIgnoringCase("positive"),
                        "message", containsStringIgnoringCase("negative"),
                        "message", containsStringIgnoringCase("same time")
                )
                .extract()
                .response();

        Reporter.log(response.asPrettyString(), 2);
    }

    @Test
    public void givenSingleWildcardAsKeyword_ShouldRespondWithBadRequest() {
        requestSpecification.param("keyword", "*");

        Response response = given()
                .spec(requestSpecification)
                .get()
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .extract()
                .response();

        Reporter.log(response.asPrettyString(), 2);
    }

    @Test
    public void givenDocumentId_allHitsShouldReferenceThatId() {
        Response response = given()
                .spec(requestSpecification)
                .get()
                .then()
                .statusCode(200)
                .body("searchResults.statements*.accessionNumber", everyItem(equalTo(docId)))
                .extract()
                .response();

        Reporter.log(response.asPrettyString(), 2);
    }

    @Test
    public void givenNonRecurringHits_originalStatementCountShouldBeEqualToLengthOfHitsList() {
        Response response = given()
                .spec(requestSpecification)
                .get()
                .then()
                .statusCode(200)
                .body("searchResults.statements.recurring", everyItem(is(false)))
                .body("searchResults.originalStatementCount",
                        res -> equalTo(res.path("searchResults.statements.size()")))
                .extract()
                .response();

        Reporter.log(response.asPrettyString(), 2);
    }
}
