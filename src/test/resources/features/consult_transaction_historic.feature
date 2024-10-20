Feature: Consult the historic of transaction
  Scenario: Successful consultation of transactions
    Given I have a bank account with number "4"
    When I send GET request to "/api/v1/bank-account/4/transaction/historic"
    Then The response status should be 200 for get the transaction historic