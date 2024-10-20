package fr.bank.domain.service

import fr.bank.api.input.DepositRequest
import fr.bank.api.input.WithdrawRequest
import fr.bank.api.output.DepositResponse
import fr.bank.api.output.TransactionHistoricResponse
import fr.bank.api.output.WithdrawResponse
import fr.bank.domain.model.Amount
import fr.bank.domain.model.Balance
import fr.bank.domain.model.BankAccount
import fr.bank.domain.repository.BankAccountRepository
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TransactionServiceTest {

    private val repository: BankAccountRepository = mockk()
    private val transactionService: TransactionService = TransactionService(repository)

    @Nested
    inner class MakeDepositTest {

        @Test
        fun `should succeed when depositing into a valid account`() {
            every { repository.getByAccountNumber("1") } returns BankAccount("1", balance = Balance(0.0))
            every { repository.save(any()) } returns BankAccount("1", balance = Balance(100.0))

            givenADepositRequest()
                .withAAccountNumber()
                .whenCallingDeposit()
                .shouldBeSuccess()
        }

        @Test
        fun `should fail when depositing into a non-existent account`() {
            every { repository.getByAccountNumber("1") } returns null

            givenADepositRequest()
                .withAAccountNumber()
                .whenCallingDeposit()
                .shouldBeFailed()
        }

        @Test
        fun `should fail when depositing a negative amount`() {
            every { repository.getByAccountNumber("1") } returns BankAccount("1", balance = Balance(0.0))

            givenADepositRequest()
                .withAAccountNumber()
                .whenCallingDeposit()
                .shouldBeFailed()
        }

        @Test
        fun `should fail when repository throws exception`() {
            every { repository.getByAccountNumber("1") } throws Exception()

            givenADepositRequest()
                .withAAccountNumber()
                .whenCallingDeposit()
                .shouldBeFailed()
        }

        private fun givenADepositRequest() = DepositRequest(100.0)
        private fun DepositRequest.withAAccountNumber() = this to "1"
        private fun Pair<DepositRequest, String>.whenCallingDeposit() = transactionService.makeDeposit(accountNumber = this.second, amount = Amount(this.first.amount))
        private fun DepositResponse.shouldBeSuccess() = assert(this is DepositResponse.Success)
        private fun DepositResponse.shouldBeFailed() = assert(this is DepositResponse.Error)
    }

    @Nested
    inner class MakeWithdrawTest {

        @Test
        fun `should succeed when withdrawing from a valid account with sufficient balance`() {
            every { repository.getByAccountNumber("1") } returns BankAccount("1", balance = Balance(100.0))
            every { repository.save(any()) } returns BankAccount("1", balance = Balance(0.0))

            givenAWithDrawRequest()
                .withAAccountNumber()
                .whenCallingWithdraw()
                .shouldBeSuccess()
        }

        @Test
        fun `should fail when withdrawing an amount larger than the balance`() {
            every { repository.getByAccountNumber("1") } returns BankAccount("1", balance = Balance(100.0))

            givenAWithDrawRequest()
                .withAAccountNumber()
                .whenCallingWithdraw()
                .shouldBeFailed()
        }

        @Test
        fun `should fail when withdrawing a negative amount`() {
            every { repository.getByAccountNumber("1") } returns BankAccount("1", balance = Balance(100.0))

            givenAWithDrawRequest()
                .withAAccountNumber()
                .whenCallingWithdraw()
                .shouldBeFailed()
        }

        @Test
        fun `should fail when withdrawing from a non-existent account`() {
            every { repository.getByAccountNumber("1") } returns null

            givenAWithDrawRequest()
                .withAAccountNumber()
                .whenCallingWithdraw()
                .shouldBeFailed()
        }

        @Test
        fun `should fail when repository throws an exception during withdraw`() {
            every { repository.getByAccountNumber("1") } throws Exception()

            givenAWithDrawRequest()
                .withAAccountNumber()
                .whenCallingWithdraw()
                .shouldBeFailed()
        }

        private fun givenAWithDrawRequest() = WithdrawRequest(100.0)
        private fun WithdrawRequest.withAAccountNumber() = this to "1"
        private fun Pair<WithdrawRequest, String>.whenCallingWithdraw() = transactionService.makeWithdraw(accountNumber = this.second, amount = Amount(this.first.amount))
        private fun WithdrawResponse.shouldBeSuccess() = assert(this is WithdrawResponse.Success)
        private fun WithdrawResponse.shouldBeFailed() = assert(this is WithdrawResponse.Error)
    }

    @Nested
    inner class TransactionsHistoricTest {

        private lateinit var bankAccount: BankAccount

        @Test
        fun `should retrieve transactions historic with a successful deposit`() {
            givenABankAccountWithBalance()
                .whenCallingTransactionsHistoric()
                .thenTheTransactionHistoryShouldContain(1)
                .andTheBalanceShouldBe(100.0)
        }

        @Test
        fun `should retrieve transactions historic after a failed withdraw attempt`() {
            givenABankAccountWithBalance()
                .whenMakingAWithdraw(300.0)
                .thenTheWithdrawShouldFail()
                .whenCallingTransactionsHistoric()
                .thenTheTransactionHistoryShouldContain(1)
                .andTheBalanceShouldBe(100.0)
        }

        @Test
        fun `should retrieve transactions historic after a failed deposit attempt`() {
            givenABankAccountWithBalance()
                .whenMakingADeposit(-300.0)
                .thenTheDepositShouldFail()
                .whenCallingTransactionsHistoric()
                .thenTheTransactionHistoryShouldContain(1)
                .andTheBalanceShouldBe(100.0)
        }

        @Test
        fun `should retrieve transactions historic after a successful withdraw`() {
            givenABankAccountWithBalance()
                .whenMakingAWithdraw(50.0)
                .thenTheWithdrawShouldSucceed()
                .whenCallingTransactionsHistoric()
                .thenTheTransactionHistoryShouldContain(2)
                .andTheBalanceShouldBe(50.0)
        }

        @Test
        fun `should retrieve transactions historic after a successful deposit`() {
            givenABankAccountWithBalance()
                .whenMakingADeposit(50.0)
                .thenTheDepositShouldSucceed()
                .whenCallingTransactionsHistoric()
                .thenTheTransactionHistoryShouldContain(2)
                .andTheBalanceShouldBe(150.0)
        }

        private fun givenABankAccountWithBalance() {
            bankAccount = BankAccount("1", balance = Balance(value = 0.0))
            every { repository.getByAccountNumber("1") } returns bankAccount
            every { repository.save(any()) } returns bankAccount

            transactionService.makeDeposit(accountNumber = "1", amount = Amount(100.0))
        }

        private fun Any.whenCallingTransactionsHistoric(accountNumber: String = "1"): TransactionHistoricResponse {
            return transactionService.getTransactionsHistoric(accountNumber)
        }

        private fun Any.whenMakingAWithdraw(amount: Double, accountNumber: String = "1"): WithdrawResponse {
            return transactionService.makeWithdraw(accountNumber = accountNumber, amount = Amount(amount))
        }

        private fun Any.whenMakingADeposit(amount: Double, accountNumber: String = "1"): DepositResponse {
            return transactionService.makeDeposit(accountNumber = accountNumber, amount = Amount(amount))
        }

        private fun TransactionHistoricResponse.thenTheTransactionHistoryShouldContain(expectedCount: Int): TransactionHistoricResponse  {
            assert(this is TransactionHistoricResponse.Success)
            (this as TransactionHistoricResponse.Success).transactionsHistoric.size shouldBe expectedCount
            return this
        }

        private fun TransactionHistoricResponse.andTheBalanceShouldBe(expectedBalance: Double): TransactionHistoricResponse {
            assert(this is TransactionHistoricResponse.Success)
            (this as TransactionHistoricResponse.Success).transactionsHistoric.last().balance.value shouldBe expectedBalance
            return this
        }

        private fun WithdrawResponse.thenTheWithdrawShouldFail() {
            assert(this is WithdrawResponse.Error)
            (this as WithdrawResponse.Error).status shouldBe HttpStatusCode.BadRequest
        }

        private fun DepositResponse.thenTheDepositShouldFail() {
            assert(this is DepositResponse.Error)
            (this as DepositResponse.Error).status shouldBe HttpStatusCode.BadRequest
        }

        private fun WithdrawResponse.thenTheWithdrawShouldSucceed() {
            assert(this is WithdrawResponse.Success)
        }

        private fun DepositResponse.thenTheDepositShouldSucceed() {
            assert(this is DepositResponse.Success)
        }
    }

}