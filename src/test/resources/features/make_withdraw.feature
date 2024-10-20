Feature: Make a withdraw
  Scenario: Successful subtract of a new amount in bank account
    Given I have a bank account with number "3" and wish withdraw 100.0 in
    When I send POST request to "/api/v1/bank-account/3/transaction/withdraw" for withdraw
    Then The response status should be 200 for subtract the new amount