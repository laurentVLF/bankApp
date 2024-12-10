package fr.bank.api.output

import io.ktor.http.HttpStatusCode

sealed class WithdrawResponse {
    data object Success : WithdrawResponse()

    data class Error(val error: String, val status: HttpStatusCode) : WithdrawResponse()
}
