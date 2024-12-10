package fr.bank.domain.model

import java.time.LocalDate

data class TransactionHistoric(
    val amount: Amount,
    val date: LocalDate = LocalDate.now(),
    val balance: Balance,
    val operationType: OperationEnum,
)
