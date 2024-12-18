package fr.bank.domain.model.output

import fr.bank.domain.model.TransactionHistory

sealed class TransactionHistoricResult {
    data class Success(
        val transactionHistories: List<TransactionHistory> = emptyList<TransactionHistory>().toMutableList(),
    ) : TransactionHistoricResult()

    data class Error(val error: String, val exception: Exception) : TransactionHistoricResult()
}
