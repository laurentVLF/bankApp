package fr.bank.infrastructure.repository

import fr.bank.domain.model.Balance
import fr.bank.domain.model.BankAccount
import fr.bank.domain.repository.BankAccountRepository

class InMemoryBankAccountRepository : BankAccountRepository {
    //private val accounts = emptyList<BankAccount>().toMutableList()
    private val bankAccountInMemory = BankAccount(
        accountNumber = "1",
        balance = Balance(
            value = 100.0)
    )
    private val accounts = mutableListOf(bankAccountInMemory)
    override fun save(bankAccount: BankAccount): BankAccount {
        val existingAccountIndex = accounts.indexOfFirst { it.accountNumber == bankAccount.accountNumber }

        if (existingAccountIndex != -1) {
            accounts[existingAccountIndex] = bankAccount
        } else {
            accounts.add(bankAccount)
        }

        return bankAccount
    }

    override fun getByAccountNumber(accountNumber: String): BankAccount? {
        return accounts.find { it.accountNumber == accountNumber }
    }

    override fun addAccount(bankAccount: BankAccount) {
        accounts.add(bankAccount)
    }

}