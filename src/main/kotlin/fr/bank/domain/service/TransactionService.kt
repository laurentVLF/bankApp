package fr.bank.domain.service

import fr.bank.api.output.DepositResponse
import fr.bank.api.output.TransactionHistoricDto
import fr.bank.api.output.TransactionHistoricResponse
import fr.bank.api.output.WithdrawResponse
import fr.bank.domain.model.Amount
import fr.bank.domain.model.TransactionHistoric
import fr.bank.domain.repository.BankAccountRepository
import io.ktor.http.HttpStatusCode
import org.slf4j.LoggerFactory

class TransactionService(
    private val repository: BankAccountRepository
) {

    private val logger = LoggerFactory.getLogger(TransactionService::class.java)

    fun makeDeposit(accountNumber: String?, amount: Amount) =
        accountNumber?.let {
            try {
                val bankAccount = repository.getByAccountNumber(accountNumber)
                when (bankAccount == null) {
                    true -> DepositResponse.Error(error = "Account not found", status = HttpStatusCode.NotFound)
                    false -> {
                        bankAccount.deposit(amount)
                        repository.save(bankAccount)
                        DepositResponse.Success
                    }
                }
            } catch (e: Exception) {
                logger.error("Error while making deposit", e)
                errorHandler(e) { message, httpStatusCode ->
                    DepositResponse.Error(error = message, status = httpStatusCode)
                } as DepositResponse
            }
        } ?: DepositResponse.Error(error = "Request invalid", status = HttpStatusCode.BadRequest)

    fun makeWithdraw(accountNumber: String?, amount: Amount): WithdrawResponse =
        accountNumber?.let {
            try {
                val bankAccount = repository.getByAccountNumber(accountNumber)
                when (bankAccount) {
                    null -> WithdrawResponse.Error(error = "Account not found", status = HttpStatusCode.NotFound)
                    else -> {
                        bankAccount.withdraw(amount)
                        repository.save(bankAccount)
                        WithdrawResponse.Success
                    }
                }
            } catch (e: IllegalArgumentException) {
                logger.error("Withdrawal failed due to invalid argument: ${e.message}")
                WithdrawResponse.Error(error = e.message ?: "Invalid argument", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                logger.error("Error while making withdraw", e)
                errorHandler(e) { message, httpStatusCode ->
                    WithdrawResponse.Error(error = message, status = httpStatusCode)
                } as WithdrawResponse
            }
        } ?: WithdrawResponse.Error(error = "Request invalid", status = HttpStatusCode.BadRequest)


    fun getTransactionsHistoric(accountNumber: String?) = accountNumber?.let {
        try {
            val bankAccount = repository.getByAccountNumber(accountNumber)
            when (bankAccount == null) {
                true -> TransactionHistoricResponse.Error(error = "Account not found", status = HttpStatusCode.NotFound)
                false -> {
                    val transactionsHistoric = bankAccount.getHistoricByAccountNumber()
                    TransactionHistoricResponse.Success(transactionsHistoric.toDtos())
                }
            }
        } catch (e: Exception) {
            logger.error("Error while get transactions historic", e)
            errorHandler(e) { message, httpStatusCode ->
                TransactionHistoricResponse.Error(error = message, status = httpStatusCode)
            } as TransactionHistoricResponse
        }
    } ?: TransactionHistoricResponse.Error(error = "Request invalid", status = HttpStatusCode.BadRequest)

    private fun errorHandler(ex: Exception, responseCreator: (String, HttpStatusCode) -> Any): Any {
        val error = when (ex) {
            is IllegalArgumentException -> "Illegal argument exception: ${ex.message}" to HttpStatusCode.BadRequest
            else -> "Error unexpected" to HttpStatusCode.InternalServerError
        }
        logger.error("Error message: ${error.first}")
        return responseCreator(error.first, error.second)
    }

    private fun List<TransactionHistoric>.toDtos() = this.map { TransactionHistoricDto(it.amount.toDto(), it.date.toString(), it.balance.toDto(), it.operationType) }


}