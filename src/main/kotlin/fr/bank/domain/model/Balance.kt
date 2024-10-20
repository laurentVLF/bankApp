package fr.bank.domain.model

import fr.bank.api.output.BalanceDto

// object value
data class Balance(val value: Double) {

    init {
        require(value >= 0.0) { "Value can't be negative" }
    }

    fun toDto() = BalanceDto(value = value)

    fun add(amount: Amount): Balance {
        require(amount.value > 0) { "You can't subtract a negative value to a negative balance" }
        return Balance(value = value + amount.value)
    }

    fun subtract(amount: Amount): Balance {
        require(amount.value > 0 ) { "You can't subtract a negative value to a negative balance" }
        val subtractedValue = value - amount.value
        require(subtractedValue >= 0.0) { "Value can't be negative" }
        return Balance(value = subtractedValue)
    }
}
