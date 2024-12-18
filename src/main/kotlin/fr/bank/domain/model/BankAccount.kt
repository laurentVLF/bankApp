package fr.bank.domain.model

import org.slf4j.LoggerFactory

class BankAccount(
    val accountNumber: String,
    private val transactionHistory: MutableList<TransactionHistory> = emptyList<TransactionHistory>().toMutableList(),
) {
    private val logger = LoggerFactory.getLogger(BankAccount::class.java)

    fun deposit(amount: Amount) {
        require(amount.value > 0) { "Deposit amount must be positive" }

        transactionHistory.add(
            TransactionHistory(
                amount = amount,
                balance = calculateBalance().add(amount),
                operationType = OperationEnum.DEPOSIT,
            ),
        ).also {
            logger.info("Deposit made. Transaction history: $transactionHistory")
            logger.info("Deposit made. New balance: ${getBalance().value}")
        }
    }

    fun withdraw(amount: Amount) {
        require(amount.value > 0) { "Withdrawal amount must be positive" }

        val newBalance = calculateBalance().subtract(amount)
        require(newBalance.value >= 0) { "Insufficient funds: balance cannot be negative" }

        transactionHistory.add(
            TransactionHistory(
                amount = amount,
                balance = newBalance,
                operationType = OperationEnum.WITHDRAWAL,
            ),
        ).also {
            logger.info("Withdrawal made. Transaction history: $transactionHistory")
            logger.info("Withdrawal made. New balance: ${getBalance().value}")
        }
    }

    private fun calculateBalance(): Balance {
        return transactionHistory.fold(Balance(value = 0.0)) { acc, transaction ->
            when (transaction.operationType) {
                OperationEnum.DEPOSIT -> acc.add(transaction.amount)
                OperationEnum.WITHDRAWAL -> acc.subtract(transaction.amount)
            }
        }
    }

    fun getBalance(): Balance = calculateBalance()

    fun getHistoricByAccountNumber(): List<TransactionHistory> = transactionHistory
}

class BankAccountBuilder() {
    var accountNumber: String = ""

    fun accountNumber(accountNumber: String) = apply { this.accountNumber = accountNumber }

    fun build(): BankAccount {
        require(accountNumber.isNotEmpty()) { "Account name must not be empty" }
        return BankAccount(
            accountNumber = accountNumber,
            transactionHistory = emptyList<TransactionHistory>().toMutableList(),
        )
    }

    fun buildWithAddAmount(amount: Amount): BankAccount {
        require(accountNumber.isNotEmpty()) { "Account name must not be empty" }
        val bankAccount =
            BankAccount(
                accountNumber = accountNumber,
                transactionHistory = emptyList<TransactionHistory>().toMutableList(),
            )
        bankAccount.deposit(amount)
        return bankAccount
    }
}
