package fr.bank.api.input

import kotlinx.serialization.Serializable

@Serializable
data class DepositRequest(val amount: Double)
