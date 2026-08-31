# language: en
@regression @registration @auth
Feature: User Registration with UK Address Geocoding
  As a new customer
  I want to register an account with my personal details and UK postcode
  So that I can log into the platform and have my address verified

  Scenario: Register a new user with valid UK postcode (SW1A 2AA)
    Given the auth service is online
    When I submit a registration request with:
      | email        | cucumber.user@example.com |
      | password     | Password@123             |
      | firstName    | Sherlock                 |
      | lastName     | Holmes                   |
      | addressLine1 | 221B Baker Street        |
      | city         | London                   |
      | postcode     | SW1A 2AA                 |
    Then the response HTTP status should be 201
    And the JSON field "success" should be true
    And the JSON field "data.user.email" should be "cucumber.user@example.com"
    And the JSON field "data.user.firstName" should be "Sherlock"
    And the JSON field "data.accessToken" should not be null
    And the JSON field "data.refreshToken" should not be null

  Scenario: Attempt registration with an already registered email
    Given the auth service is online
    When I submit a registration request with:
      | email        | admin@platform.com |
      | password     | Password@123       |
      | firstName    | Duplicate          |
      | lastName     | User               |
      | addressLine1 | 10 Downing Street  |
      | city         | London             |
      | postcode     | SW1A 2AA           |
    Then the response HTTP status should be 400
    And the JSON field "success" should be false
    And the JSON field "message" should contain "already in use"

  Scenario: Attempt registration with invalid UK postcode format
    Given the auth service is online
    When I submit a registration request with:
      | email        | invalid.postcode@example.com |
      | password     | Password@123                |
      | firstName    | Bad                         |
      | lastName     | Postcode                    |
      | postcode     | 123456789                   |
    Then the response HTTP status should be 400
    And the JSON field "success" should be false
