package fr.bank.steps

import fr.bank.api.input.DepositRequest
import fr.bank.domain.model.Balance
import fr.bank.domain.model.BankAccount
import fr.bank.domain.model.OperationEnum
import fr.bank.domain.repository.BankAccountRepository
import fr.bank.infrastructure.repository.InMemoryBankAccountRepository
import fr.bank.steps.configuration.TestApplicationManager
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.java8.En
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.koin.java.KoinJavaComponent
import org.koin.test.KoinTest

class MakeDepositSteps : KoinTest, En {
    private lateinit var response: HttpResponse
    private lateinit var repository: BankAccountRepository
    private lateinit var depositRequest: DepositRequest

    init {
        Before { _ ->
            repository = KoinJavaComponent.getKoin().get<BankAccountRepository>() as InMemoryBankAccountRepository
            repository.addAccount(BankAccount(accountNumber = "2", balance = Balance(value = 100.0)))
        }
        After { _ ->
            repository.clear()
        }
    }

    @Given("I have a bank account with number {string} and wish deposit {double} in")
    fun iHaveABankAccountWithNumberAndWishDepositIn(
        accountNumber: String,
        amount: Double,
    ) {
        depositRequest = DepositRequest(amount = amount)
    }

    @When("I send POST request to {string} for deposit")
    fun iSendPOSTRequestToForDeposit(path: String) {
        response =
            TestApplicationManager.sendGetRequest(
                path = path,
                httpMethod = HttpMethod.Post,
                type = OperationEnum.DEPOSIT,
                request = depositRequest,
            )
    }

    @Then("The response status should be {int} for add the new amount")
    fun theResponseStatusShouldBeForAddTheNewAmount(statusCode: Int) {
        response.status.value shouldBe statusCode
    }
}
