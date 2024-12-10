package fr.bank.steps

import fr.bank.api.output.TransactionHistoricDto
import fr.bank.domain.model.Amount
import fr.bank.domain.model.Balance
import fr.bank.domain.model.BankAccount
import fr.bank.domain.model.OperationEnum
import fr.bank.domain.model.TransactionHistoric
import fr.bank.domain.repository.BankAccountRepository
import fr.bank.infrastructure.repository.InMemoryBankAccountRepository
import fr.bank.steps.configuration.TestApplicationManager
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.java8.En
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent
import org.koin.test.KoinTest

class ConsultTransactionHistoricSteps : KoinTest, En {
    private lateinit var response: HttpResponse
    private lateinit var repository: BankAccountRepository

    init {
        Before { _ ->
            repository = KoinJavaComponent.getKoin().get<BankAccountRepository>() as InMemoryBankAccountRepository
            repository.addAccount(
                BankAccount(
                    accountNumber = "4",
                    balance = Balance(value = 100.0),
                    transactionHistoric =
                        mutableListOf(
                            TransactionHistoric(
                                amount = Amount(value = 100.0),
                                balance = Balance(value = 100.0),
                                operationType = OperationEnum.DEPOSIT,
                            ),
                        ),
                ),
            )
        }
        After { _ ->
            repository.clear()
        }
    }

    @Given("I have a bank account with number {string}")
    fun iHaveABankAccountWithNumber(accountNumber: String) {}

    @When("I send GET request to {string}")
    fun iSendGETRequestTo(path: String) {
        response = TestApplicationManager.sendGetRequest(path = path, httpMethod = HttpMethod.Get)
    }

    @Then("The response status should be {int} for get the transaction historic")
    fun theResponseStatusShouldBe(statusCode: Int) {
        runBlocking {
            response.status.value shouldBe statusCode

            when (statusCode) {
                200 -> {
                    val success = Json.decodeFromString<List<TransactionHistoricDto>>(response.bodyAsText())
                    success.size shouldBe 1
                    success[0].amount.value shouldBe 100.0
                    success[0].balance.value shouldBe 100.0
                    success[0].operationType shouldBe OperationEnum.DEPOSIT
                }
                404 -> {
                    val error = response.bodyAsText()
                    error shouldBe "Not found exception: Account not found"
                }
                else -> {}
            }
        }
    }
}
