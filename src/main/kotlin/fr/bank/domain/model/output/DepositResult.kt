package fr.bank.domain.model.output

sealed class DepositResult {
    data object Success : DepositResult()

    data class Error(val error: String, val exception: Exception) : DepositResult()
}
