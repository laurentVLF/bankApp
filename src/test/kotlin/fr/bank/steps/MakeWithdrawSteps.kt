package fr.bank.steps

import fr.bank.api.input.WithdrawRequest
import fr.bank.domain.model.Amount
import fr.bank.domain.model.BankAccountBuilder
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

class MakeWithdrawSteps : KoinTest, En {
    private lateinit var response: HttpResponse
    private lateinit var repository: BankAccountRepository
    private lateinit var withdrawRequest: WithdrawRequest

    init {
        Before { _ ->
            val bankAccount = BankAccountBuilder().accountNumber(accountNumber = "3").buildWithAddAmount(amount = Amount(value = 100.0))
            repository = KoinJavaComponent.getKoin().get<BankAccountRepository>() as InMemoryBankAccountRepository
            repository.addAccount(bankAccount = bankAccount)
        }
        After { _ ->
            repository.clear()
        }
    }

    @Given("I have a bank account with number {string} and wish withdraw {double} in")
    fun iHaveABankAccountWithNumberAndWishWithdrawIn(
        accountNumber: String,
        amount: Double,
    ) {
        withdrawRequest = WithdrawRequest(amount = amount)
    }

    @When("I send POST request to {string} for withdraw")
    fun iSendPOSTRequestToForWithdraw(path: String) {
        response =
            TestApplicationManager.sendRequest(
                path = path,
                httpMethod = HttpMethod.Post,
                type = OperationEnum.WITHDRAWAL,
                request = withdrawRequest,
            )
    }

    @Then("The response status should be {int} for subtract the new amount")
    fun theResponseStatusShouldBeForSubtractTheNewAmount(statusCode: Int) {
        response.status.value shouldBe statusCode
    }
}
