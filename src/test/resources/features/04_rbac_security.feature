# language: en
@regression @rbac @security
Feature: Role-Based Access Control (RBAC) & User Management
  As an administrator
  I want to restrict admin routes and manage user profiles
  So that unauthorized users cannot alter system data

  Scenario: Authenticated user views their own profile (/me)
    Given the auth service is online
    And I am logged in as "admin@platform.com" with password "Admin@123456"
    When I request my profile endpoint "/api/v1/users/me"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data.email" should be "admin@platform.com"

  Scenario: Admin retrieves paginated user list
    Given the auth service is online
    And I am logged in as "admin@platform.com" with password "Admin@123456"
    When I send a GET request to "/api/v1/users" with query params "page=0&size=10"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "data.content" should not be null

  Scenario: Unauthenticated request to protected endpoint is rejected
    Given the auth service is online
    When I send an unauthenticated GET request to "/api/v1/users/me"
    Then the response HTTP status should be 401
    And the JSON field "success" should be false
