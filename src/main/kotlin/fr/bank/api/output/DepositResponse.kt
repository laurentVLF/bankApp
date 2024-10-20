package fr.bank.api.output

import io.ktor.http.HttpStatusCode

sealed class DepositResponse {
    data object Success: DepositResponse()
    data class Error(val error: String, val status: HttpStatusCode) : DepositResponse()
}