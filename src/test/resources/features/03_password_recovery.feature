# language: en
@regression @password @recovery
Feature: Secure Password Reset & Recovery
  As a user who forgot their password
  I want to request a password reset token and set a new password
  So that I can regain access to my account

  Scenario: Request password reset token for registered account
    Given the auth service is online
    When I submit a forgot password request for email "admin@platform.com"
    Then the response HTTP status should be 200
    And the JSON field "success" should be true
    And the JSON field "message" should contain "Password reset instructions sent"
