package fr.bank.domain.model

import org.slf4j.LoggerFactory

// The bounded context is the bank account management
// BankAccount is the Aggregate
class BankAccount(
    val accountNumber: String,
    private val transactionHistoric: MutableList<TransactionHistoric> = emptyList<TransactionHistoric>().toMutableList(),
    var balance: Balance
) {

    private val logger = LoggerFactory.getLogger(BankAccount::class.java)


    fun deposit(amount: Amount) {
        balance = balance.add(amount)
        transactionHistoric.add(
            TransactionHistoric(
                amount = amount,
                balance = balance,
                operationType = OperationEnum.DEPOSIT
            )
        ).also {
            logger.info("Deposit made. Transaction history: $transactionHistoric")
            logger.info("Deposit made. New balance: ${balance.value}")
        }
    }

    fun withdraw(amount: Amount) {
        balance = balance.subtract(amount)
        transactionHistoric.add(
            TransactionHistoric(
                amount = amount,
                balance = balance,
                operationType = OperationEnum.WITHDRAWAL
            )
        ).also {
            logger.info("Withdrawal made. Transaction history: $transactionHistoric")
            logger.info("Withdrawal made. New balance: ${balance.value}")
        }
    }

    fun getHistoricByAccountNumber(): List<TransactionHistoric> = transactionHistoric
}
