# language: en
@regression @login @jwt
Feature: Authentication & JWT Lifecycle Management
  As a registered user or administrator
  I want to log in with my email and password and receive JWT tokens
  So that I can securely access protected platform endpoints

  Scenario: Successful Admin Login with system default credentials
    Given the auth service is online
    When I submit a login request with email "admin@platform.com" and password "Admin@123456"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data.user.email" should be "admin@platform.com"
    And the JSON array "data.user.roles" should contain item "ROLE_ADMIN"
    And the JSON field "data.accessToken" should not be null
    And the JSON field "data.refreshToken" should not be null

  Scenario: Failed Login with invalid password
    Given the auth service is online
    When I submit a login request with email "admin@platform.com" and password "WrongPassword@999"
    Then the response HTTP status should be 401
    And the JSON field "success" should be false
    And the JSON field "message" should contain "Invalid email or password"

  Scenario: Refresh expired JWT access token using valid refresh token
    Given the auth service is online
    And I am logged in as "admin@platform.com" with password "Admin@123456"
    When I submit a refresh token request using the saved refresh token
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data.accessToken" should not be null
    And the JSON field "data.tokenType" should be "Bearer"

  Scenario: Immediate access token invalidation and blacklisting upon logout
    Given the auth service is online
    And I am logged in as "admin@platform.com" with password "Admin@123456"
    When I submit a logout request with my active token
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    When I try to use the saved access token to request "/api/v1/users/me"
    Then the response HTTP status should be 401
