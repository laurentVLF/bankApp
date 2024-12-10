package fr.bank.domain.model

import org.slf4j.LoggerFactory

class BankAccount(
    val accountNumber: String,
    private val transactionHistoric: MutableList<TransactionHistoric> = emptyList<TransactionHistoric>().toMutableList(),
    var balance: Balance,
) {
    private val logger = LoggerFactory.getLogger(BankAccount::class.java)

    fun deposit(amount: Amount) {
        balance = balance.add(amount)
        transactionHistoric.add(
            TransactionHistoric(
                amount = amount,
                balance = balance,
                operationType = OperationEnum.DEPOSIT,
            ),
        )
        balance =
            transactionHistoric.reduce { acc, transaction ->
                acc.copy(balance = acc.balance.add(transaction.amount))
            }.balance.also {
                logger.info("Deposit made. Transaction history: $transactionHistoric")
                logger.info("Deposit made. New balance: ${it.value}")
            }
    }

    fun withdraw(amount: Amount) {
        balance = balance.subtract(amount)
        transactionHistoric.add(
            TransactionHistoric(
                amount = amount,
                balance = balance,
                operationType = OperationEnum.WITHDRAWAL,
            ),
        )
        balance =
            transactionHistoric.reduce { acc, transaction ->
                acc.copy(balance = acc.balance.subtract(transaction.amount))
            }.balance.also {
                logger.info("Withdrawal made. Transaction history: $transactionHistoric")
                logger.info("Withdrawal made. New balance: ${it.value}")
            }
    }

    fun getHistoricByAccountNumber(): List<TransactionHistoric> = transactionHistoric
}
