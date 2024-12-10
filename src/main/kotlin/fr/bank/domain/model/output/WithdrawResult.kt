package fr.bank.domain.model.output

sealed class WithdrawResult {
    data object Success : WithdrawResult()

    data class Error(val error: String, val exception: Exception) : WithdrawResult()
}
