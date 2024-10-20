package fr.bank.steps

import fr.bank.api.input.DepositRequest
import fr.bank.api.plugins.configureRouting
import fr.bank.api.plugins.configureSerialization
import fr.bank.domain.model.Balance
import fr.bank.domain.model.BankAccount
import fr.bank.domain.repository.BankAccountRepository
import fr.bank.infrastructure.repository.InMemoryBankAccountRepository
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.java8.En
import io.kotest.matchers.shouldBe
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent
import org.koin.test.KoinTest

class MakeDepositSteps: KoinTest, En {

    private lateinit var response: HttpResponse
    private lateinit var repository: BankAccountRepository
    private lateinit var depositRequest: DepositRequest

    init {
        Before { _ ->
            KoinManager.startKoinIfNeeded()
            repository = KoinJavaComponent.getKoin().get<BankAccountRepository>() as InMemoryBankAccountRepository
            repository.addAccount(BankAccount(accountNumber = "2", balance = Balance(value = 100.0)))
        }
        After { _ ->
            KoinManager.stopKoinIfNeeded()
        }
    }

    @Given("I have a bank account with number {string} and wish deposit {double} in")
    fun iHaveABankAccountWithNumberAndWishDepositIn(accountNumber: String, amount: Double) {
        depositRequest = DepositRequest(amount = 110.0)
    }

    @When("I send POST request to {string} for deposit")
    fun iSendPOSTRequestToForDeposit(path: String) = testApplication {

        application {
            configureSerialization()
            configureRouting()
        }

        response = runBlocking {
            client.post("http://localhost:80$path") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(depositRequest))
            }
        }
    }

    @Then("The response status should be {int} for add the new amount")
    fun theResponseStatusShouldBeForAddTheNewAmount(statusCode: Int) {
        statusCode shouldBe response.status.value
    }
}