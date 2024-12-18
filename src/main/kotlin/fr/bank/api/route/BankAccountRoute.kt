package fr.bank.api.route

import fr.bank.api.input.DepositRequest
import fr.bank.api.input.WithdrawRequest
import fr.bank.api.output.AmountDto
import fr.bank.api.output.BalanceDto
import fr.bank.api.output.TransactionHistoricDto
import fr.bank.api.output.TransactionHistoricResponse
import fr.bank.domain.model.Amount
import fr.bank.domain.model.Balance
import fr.bank.domain.model.TransactionHistory
import fr.bank.domain.model.output.DepositResult
import fr.bank.domain.model.output.TransactionHistoricResult
import fr.bank.domain.model.output.WithdrawResult
import fr.bank.domain.service.NotFoundException
import fr.bank.domain.service.TransactionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.bankAccountRoute() {
    val service by inject<TransactionService>()

    route("/api/v1/bank-account") {
        post("/{accountNumber}/transaction/deposit") {
            try {
                val accountNumber = call.parameters["accountNumber"]
                val requestBody = call.receive<DepositRequest>()
                when (
                    val response =
                        service.makeDeposit(accountNumber = accountNumber, amount = Amount(value = requestBody.amount))
                ) {
                    is DepositResult.Success -> call.response.status(HttpStatusCode.OK)
                    is DepositResult.Error -> {
                        val error =
                            errorHandler(response.exception) { message, httpStatusCode ->
                                TransactionHistoricResponse.Error(error = message, status = httpStatusCode)
                            } as TransactionHistoricResponse.Error
                        call.respond(
                            status = error.status,
                            message = error.error,
                        )
                    }
                }
            } catch (ex: Exception) {
                when (ex) {
                    is IllegalArgumentException -> call.respond(HttpStatusCode.BadRequest, ex.message!!)
                }
            }
        }

        post("/{accountNumber}/transaction/withdraw") {
            try {
                val accountNumber = call.parameters["accountNumber"]
                val requestBody = call.receive<WithdrawRequest>()
                when (
                    val response =
                        service.makeWithdraw(accountNumber = accountNumber, amount = Amount(value = requestBody.amount))
                ) {
                    is WithdrawResult.Success -> call.response.status(HttpStatusCode.OK)
                    is WithdrawResult.Error -> {
                        val error =
                            errorHandler(response.exception) { message, httpStatusCode ->
                                TransactionHistoricResponse.Error(error = message, status = httpStatusCode)
                            } as TransactionHistoricResponse.Error
                        call.respond(
                            status = error.status,
                            message = error.error,
                        )
                    }
                }
            } catch (ex: Exception) {
                when (ex) {
                    is IllegalArgumentException -> call.respond(HttpStatusCode.BadRequest, ex.message!!)
                }
            }
        }

        get("/{accountNumber}/transaction/historic") {
            val accountNumber = call.parameters["accountNumber"]
            when (val response = service.getTransactionsHistoric(accountNumber = accountNumber)) {
                is TransactionHistoricResult.Success -> {
                    val success = TransactionHistoricResponse.Success(transactionsHistoric = response.transactionHistories.toDto())
                    call.respond(HttpStatusCode.OK, success.transactionsHistoric)
                }
                is TransactionHistoricResult.Error -> {
                    val error =
                        errorHandler(response.exception) { message, httpStatusCode ->
                            TransactionHistoricResponse.Error(error = message, status = httpStatusCode)
                        } as TransactionHistoricResponse.Error
                    call.respond(
                        status = error.status,
                        message = error.error,
                    )
                }
            }
        }
    }
}

fun List<TransactionHistory>.toDto(): List<TransactionHistoricDto> =
    this.map {
        TransactionHistoricDto(
            amount = it.amount.toDto(),
            date = it.date.toString(),
            balance = it.balance.toDto(),
            operationType = it.operationType,
        )
    }

fun Amount.toDto() = AmountDto(value = value)

fun Balance.toDto() = BalanceDto(value = value)

private fun errorHandler(
    ex: Exception,
    responseCreator: (String, HttpStatusCode) -> Any,
): Any {
    val error =
        when (ex) {
            is IllegalArgumentException -> "Illegal argument exception: ${ex.message}" to HttpStatusCode.BadRequest
            is NotFoundException -> "Not found exception: ${ex.message}" to HttpStatusCode.NotFound
            else -> "Error unexpected" to HttpStatusCode.InternalServerError
        }
    return responseCreator(error.first, error.second)
}
