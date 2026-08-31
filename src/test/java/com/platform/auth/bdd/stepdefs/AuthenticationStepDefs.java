package com.platform.auth.bdd.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthenticationStepDefs {

    private Response latestResponse;
    private String savedAccessToken;
    private String savedRefreshToken;

    @Given("the auth service is online")
    public void theAuthServiceIsOnline() {
        given()
            .when()
            .get("/actuator/health")
            .then()
            .statusCode(200);
    }

    @When("I submit a registration request with:")
    public void iSubmitARegistrationRequestWith(DataTable table) {
        Map<String, String> data = table.asMap(String.class, String.class);
        Map<String, Object> body = new HashMap<>();
        
        body.put("email", data.get("email"));
        body.put("password", data.get("password"));
        body.put("firstName", data.get("firstName"));
        body.put("lastName", data.get("lastName"));

        if (data.containsKey("phoneNumber")) {
            body.put("phoneNumber", data.get("phoneNumber"));
        }

        if (data.containsKey("postcode") || data.containsKey("addressLine1") || data.containsKey("city")) {
            Map<String, Object> address = new HashMap<>();
            if (data.containsKey("addressLine1")) address.put("addressLine1", data.get("addressLine1"));
            if (data.containsKey("city")) address.put("city", data.get("city"));
            if (data.containsKey("postcode")) address.put("postcode", data.get("postcode"));
            body.put("address", address);
        }

        latestResponse = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/api/v1/auth/register");

        if (latestResponse.getStatusCode() == 201) {
            savedAccessToken = latestResponse.jsonPath().getString("data.accessToken");
            savedRefreshToken = latestResponse.jsonPath().getString("data.refreshToken");
        }
    }

    @When("I submit a login request with email {string} and password {string}")
    public void iSubmitALoginRequestWithEmailAndPassword(String email, String password) {
        Map<String, String> body = Map.of(
            "email", email,
            "password", password
        );

        latestResponse = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/api/v1/auth/login");

        if (latestResponse.getStatusCode() == 200) {
            savedAccessToken = latestResponse.jsonPath().getString("data.accessToken");
            savedRefreshToken = latestResponse.jsonPath().getString("data.refreshToken");
        }
    }

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAsWithPassword(String email, String password) {
        iSubmitALoginRequestWithEmailAndPassword(email, password);
        latestResponse.then().statusCode(200);
    }

    @When("I submit a refresh token request using the saved refresh token")
    public void iSubmitARefreshTokenRequestUsingSavedRefreshToken() {
        Map<String, String> body = Map.of("refreshToken", savedRefreshToken);

        latestResponse = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/api/v1/auth/refresh-token");
    }

    @When("I submit a forgot password request for email {string}")
    public void iSubmitAForgotPasswordRequestForEmail(String email) {
        Map<String, String> body = Map.of("email", email);

        latestResponse = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/api/v1/auth/forgot-password");
    }

    @When("I request my profile endpoint {string}")
    public void iRequestMyProfileEndpoint(String endpoint) {
        latestResponse = given()
            .header("Authorization", "Bearer " + savedAccessToken)
            .when()
            .get(endpoint);
    }

    @When("I send a GET request to {string} with query params {string}")
    public void iSendAGetRequestWithQueryParams(String endpoint, String queryParams) {
        latestResponse = given()
            .header("Authorization", "Bearer " + savedAccessToken)
            .when()
            .get(endpoint + "?" + queryParams);
    }

    @When("I submit a logout request with my active token")
    public void iSubmitALogoutRequestWithActiveToken() {
        Map<String, String> body = Map.of("refreshToken", savedRefreshToken != null ? savedRefreshToken : "");
        latestResponse = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + savedAccessToken)
            .body(body)
            .when()
            .post("/api/v1/auth/logout");
    }

    @When("I try to use the saved access token to request {string}")
    public void iTryToUseTheSavedAccessTokenToRequest(String endpoint) {
        latestResponse = given()
            .header("Authorization", "Bearer " + savedAccessToken)
            .when()
            .get(endpoint);
    }

    @When("I send {int} rapid forgot-password requests for email {string}")
    public void iSendRapidForgotPasswordRequests(int count, String email) {
        Map<String, String> body = Map.of("email", email);
        for (int i = 0; i < count; i++) {
            latestResponse = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1/auth/forgot-password");
        }
    }

    @When("I send an unauthenticated GET request to {string}")
    public void iSendAnUnauthenticatedGetRequestTo(String endpoint) {
        latestResponse = given()
            .when()
            .get(endpoint);
    }

    @Then("the response HTTP status should be {int}")
    public void theResponseHttpStatusShouldBe(int expectedStatus) {
        latestResponse.then().statusCode(expectedStatus);
    }

    @And("the JSON field {string} should be true")
    public void theJsonFieldShouldBeTrue(String jsonPath) {
        latestResponse.then().body(jsonPath, equalTo(true));
    }

    @And("the JSON field {string} should be false")
    public void theJsonFieldShouldBeFalse(String jsonPath) {
        latestResponse.then().body(jsonPath, equalTo(false));
    }

    @And("the JSON field {string} should be {string}")
    public void theJsonFieldShouldBe(String jsonPath, String expectedValue) {
        latestResponse.then().body(jsonPath, equalTo(expectedValue));
    }

    @And("the JSON field {string} should contain {string}")
    public void theJsonFieldShouldContain(String jsonPath, String expectedSubstring) {
        latestResponse.then().body(jsonPath, containsString(expectedSubstring));
    }

    @And("the JSON field {string} should not be null")
    public void theJsonFieldShouldNotBeNull(String jsonPath) {
        latestResponse.then().body(jsonPath, notNullValue());
    }

    @And("the JSON array {string} should contain item {string}")
    public void theJsonArrayShouldContainItem(String jsonPath, String expectedItem) {
        latestResponse.then().body(jsonPath, hasItem(expectedItem));
    }
}
