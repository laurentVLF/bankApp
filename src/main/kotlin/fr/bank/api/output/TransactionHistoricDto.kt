package fr.bank.api.output

import fr.bank.domain.model.OperationEnum
import kotlinx.serialization.Serializable

@Serializable
data class TransactionHistoricDto(
    val amount: AmountDto,
    val date: String,
    val balance: BalanceDto,
    val operationType: OperationEnum,
)

@Serializable
data class AmountDto(val value: Double)

@Serializable
data class BalanceDto(val value: Double)
