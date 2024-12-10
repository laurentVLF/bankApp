package fr.bank.domain.service

import fr.bank.domain.model.Amount
import fr.bank.domain.model.output.DepositResult
import fr.bank.domain.model.output.TransactionHistoricResult
import fr.bank.domain.model.output.WithdrawResult
import fr.bank.domain.repository.BankAccountRepository
import org.slf4j.LoggerFactory

class TransactionService(
    private val repository: BankAccountRepository,
) {
    private val logger = LoggerFactory.getLogger(TransactionService::class.java)

    fun makeDeposit(
        accountNumber: String?,
        amount: Amount,
    ) = accountNumber?.let {
        try {
            repository.getByAccountNumber(accountNumber)?.let {
                it.deposit(amount)
                repository.save(it)
                DepositResult.Success
            } ?: DepositResult.Error(error = "Account not found", exception = NotFoundException("Account not found")).also {
                logger.error("Account not found")
            }
        } catch (e: Exception) {
            DepositResult.Error(error = "Error while making deposit", exception = e).also {
                logger.error("Error while making deposit", e)
            }
        }
    } ?: DepositResult.Error(error = "Request invalid", exception = IllegalArgumentException("Request invalid")).also {
        logger.error("Request invalid")
    }

    fun makeWithdraw(
        accountNumber: String?,
        amount: Amount,
    ): WithdrawResult =
        accountNumber?.let {
            try {
                repository.getByAccountNumber(accountNumber)?.let {
                    it.withdraw(amount)
                    repository.save(it)
                    WithdrawResult.Success
                } ?: WithdrawResult.Error(error = "Account not found", exception = NotFoundException("Account not found")).also {
                    logger.error("Account not found")
                }
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException -> WithdrawResult.Error(error = e.message ?: "Invalid argument", exception = e)
                    else -> WithdrawResult.Error(error = "Error while making withdraw", exception = e)
                }.also {
                    logger.error("Error while making withdraw", e)
                }
            }
        } ?: WithdrawResult.Error(error = "Request invalid", exception = IllegalArgumentException("Request invalid")).also {
            logger.error("Request invalid")
        }

    fun getTransactionsHistoric(accountNumber: String?) =
        accountNumber?.let {
            try {
                repository.getByAccountNumber(accountNumber)?.let {
                    val transactionsHistoric = it.getHistoricByAccountNumber()
                    TransactionHistoricResult.Success(transactionsHistoric)
                } ?: TransactionHistoricResult.Error(error = "Account not found", exception = NotFoundException("Account not found")).also {
                    logger.error("Account not found")
                }
            } catch (e: Exception) {
                TransactionHistoricResult.Error(error = "Error while get transactions historic", exception = e).also {
                    logger.error("Error while get transactions historic", e)
                }
            }
        } ?: TransactionHistoricResult.Error(error = "Request invalid", exception = IllegalArgumentException("Request invalid")).also {
            logger.error("Request invalid")
        }
}

data class NotFoundException(override val message: String) : Exception(message)
