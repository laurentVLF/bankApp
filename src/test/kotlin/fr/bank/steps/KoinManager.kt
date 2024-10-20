package fr.bank.steps

import fr.bank.domain.repository.BankAccountRepository
import fr.bank.domain.service.TransactionService
import fr.bank.infrastructure.repository.InMemoryBankAccountRepository
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent


object KoinManager {
    private var isStarted = false

    fun startKoinIfNeeded() {
        if (!isStarted) {
            startKoin {
                modules(module {
                    single { TransactionService(get()) }
                    single<BankAccountRepository> { InMemoryBankAccountRepository() }
                })
            }
            isStarted = true
        }
    }

    fun stopKoinIfNeeded() {
        if (isStarted) {
            stopKoin()
            isStarted = false
        }
    }
}
