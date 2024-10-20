package fr.bank.steps

import fr.bank.api.output.TransactionHistoricDto
import fr.bank.api.plugins.configureRouting
import fr.bank.api.plugins.configureSerialization
import fr.bank.domain.model.Amount
import fr.bank.domain.model.Balance
import fr.bank.domain.model.BankAccount
import fr.bank.domain.model.OperationEnum
import fr.bank.domain.model.TransactionHistoric
import fr.bank.domain.repository.BankAccountRepository
import fr.bank.infrastructure.repository.InMemoryBankAccountRepository
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.java8.En
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent
import org.koin.test.KoinTest

class ConsultTransactionHistoricSteps: KoinTest, En {

    private lateinit var response: HttpResponse
    private lateinit var repository: BankAccountRepository

    init {
        Before { _ ->
            KoinManager.startKoinIfNeeded()
            repository = KoinJavaComponent.getKoin().get<BankAccountRepository>() as InMemoryBankAccountRepository
            repository.addAccount(BankAccount(
                accountNumber = "4",
                balance = Balance(value = 100.0),
                transactionHistoric = mutableListOf(TransactionHistoric(
                    amount = Amount(value = 100.0),
                    balance = Balance(value = 100.0),
                    operationType = OperationEnum.DEPOSIT
                ))
            ))
        }
        After { _ ->
            KoinManager.stopKoinIfNeeded()
        }
    }

    @Given("I have a bank account with number {string}")
    fun iHaveABankAccountWithNumber(accountNumber: String) {}

    @When("I send GET request to {string}")
    fun iSendGETRequestTo(path: String) = testApplication {

        application {
            configureSerialization()
            configureRouting()
        }

        response = runBlocking {
            client.get("http://localhost:80$path") {
                contentType(ContentType.Application.Json)
            }
        }
    }

    @Then("The response status should be {int} for get the transaction historic")
    fun theResponseStatusShouldBe(statusCode: Int) {
        runBlocking {
            statusCode shouldBe response.status.value

            val success = Json.decodeFromString<List<TransactionHistoricDto>>(response.bodyAsText())
            success.size shouldBe 1
            success[0].amount.value shouldBe 100.0
            success[0].balance.value shouldBe 100.0
            success[0].operationType shouldBe OperationEnum.DEPOSIT
        }
    }
}