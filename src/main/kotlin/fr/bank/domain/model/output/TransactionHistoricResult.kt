package fr.bank.domain.model.output

import fr.bank.domain.model.TransactionHistoric

sealed class TransactionHistoricResult {
    data class Success(
        val transactionHistories: List<TransactionHistoric> = emptyList<TransactionHistoric>().toMutableList(),
    ) : TransactionHistoricResult()

    data class Error(val error: String, val exception: Exception) : TransactionHistoricResult()
}
