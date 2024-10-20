package fr.bank.domain.model

import fr.bank.api.output.AmountDto

data class Amount(val value: Double) {
    fun toDto() = AmountDto(value = value)
}
