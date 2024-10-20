package fr.bank.api.output

import io.ktor.http.HttpStatusCode

sealed class TransactionHistoricResponse {
    data class Success(val transactionsHistoric: List<TransactionHistoricDto>): TransactionHistoricResponse()
    data class Error(val error: String, val status: HttpStatusCode): TransactionHistoricResponse()
}