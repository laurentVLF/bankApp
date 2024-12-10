Feature: Make a withdraw

  Scenario Outline: Successful subtract of a new amount in bank account
    Given I have a bank account with number "<accountNumber>" and wish withdraw <amount> in
    When I send POST request to "/api/v1/bank-account/<accountNumber>/transaction/withdraw" for withdraw
    Then The response status should be <status> for subtract the new amount

    Examples:
      | accountNumber | amount | status |
      | 3             | 100.0  | 200    |
      | 0             | 100.0  | 404    |
      | 3             | -100.0 | 400    |