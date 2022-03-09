package com.vilson.tests.helpers;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.testng.Reporter;

// https://www.pawangaria.com/post/api/write-restassured-request-and-response-to-log-file/
public class RestAssuredLogsFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {
        Response response = filterContext.next(requestSpec, responseSpec);

        Reporter.log(requestSpec.getBaseUri(), 2);
        Reporter.log(requestSpec.getBasePath(), 2);
        Reporter.log(requestSpec.getMethod(), 2);
        Reporter.log(requestSpec.getBody(), 2);
        return response;
    }
}
