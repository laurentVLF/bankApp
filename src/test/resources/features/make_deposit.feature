Feature: Make a deposit
  Scenario: Successful addition of a new amount in bank account
    Given I have a bank account with number "2" and wish deposit 100.0 in
    When I send POST request to "/api/v1/bank-account/2/transaction/deposit" for deposit
    Then The response status should be 200 for add the new amount