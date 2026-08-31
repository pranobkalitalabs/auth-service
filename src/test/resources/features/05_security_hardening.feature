# language: en
@regression @security @ratelimit
Feature: Security Hardening & Rate Limiting Engine
  As a platform security administrator
  I want to throttle rapid brute-force requests and protect auth endpoints
  So that malicious actors cannot overload the system or brute-force user accounts

  Scenario: Trigger HTTP 429 Too Many Requests on rapid endpoint abuse
    Given the auth service is online
    When I send 4 rapid forgot-password requests for email "admin@platform.com"
    Then the response HTTP status should be 429
    And the JSON field "success" should be false
    And the JSON field "message" should contain "Rate limit exceeded"
