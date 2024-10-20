package fr.bank.domain.repository

import fr.bank.domain.model.BankAccount

interface BankAccountRepository {
    fun save(bankAccount: BankAccount): BankAccount
    fun getByAccountNumber(accountNumber: String): BankAccount?
    fun addAccount(bankAccount: BankAccount)
}