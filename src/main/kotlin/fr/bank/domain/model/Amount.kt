package fr.bank.domain.model

data class Amount(val value: Double) {
    init {
        if (value < 0) {
            throw IllegalArgumentException("Value can't be negative")
        }
    }
}
