package fr.bank.api.input

import kotlinx.serialization.Serializable

@Serializable
data class WithdrawRequest(val amount: Double)
