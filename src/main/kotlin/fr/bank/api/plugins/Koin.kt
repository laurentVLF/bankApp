package fr.bank.api.plugins

import fr.bank.domain.service.TransactionService
import fr.bank.domain.repository.BankAccountRepository
import fr.bank.infrastructure.repository.InMemoryBankAccountRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val myModule = module {
    single { TransactionService(get()) }
    single<BankAccountRepository> { InMemoryBankAccountRepository() }
}

fun Application.configureKoin() {
    install(Koin) {
        modules(myModule)
    }
}