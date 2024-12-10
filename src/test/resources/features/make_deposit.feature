Feature: Make a deposit

  Scenario Outline: Addition of a new amount in bank account
    Given I have a bank account with number "<accountNumber>" and wish deposit <amount> in
    When I send POST request to "/api/v1/bank-account/<accountNumber>/transaction/deposit" for deposit
    Then The response status should be <status> for add the new amount

    Examples:
      | accountNumber | amount | status |
      | 2             | 100.0  | 200    |
      | 0             | 100.0  | 404    |
      | 2             | -100.0 | 400    |
