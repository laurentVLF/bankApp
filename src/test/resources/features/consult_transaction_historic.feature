Feature: Consult the historic of transaction

  Scenario Outline: Successful consultation of transactions
    Given I have a bank account with number "<accountNumber>"
    When I send GET request to "/api/v1/bank-account/<accountNumber>/transaction/historic"
    Then The response status should be <status> for get the transaction historic

    Examples:
      | accountNumber | status |
      | 4             | 200    |
      | 0             | 404    |