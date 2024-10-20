package fr.bank.api.route

import fr.bank.api.input.DepositRequest
import fr.bank.api.input.WithdrawRequest
import fr.bank.api.output.DepositResponse
import fr.bank.api.output.TransactionHistoricResponse
import fr.bank.api.output.WithdrawResponse
import fr.bank.domain.model.Amount
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
            val accountNumber = call.parameters["accountNumber"]
            val requestBody = call.receive<DepositRequest>()
            when(val response = service.makeDeposit(accountNumber = accountNumber, amount = Amount(value = requestBody.amount))) {
                is DepositResponse.Success -> call.response.status(HttpStatusCode.OK)
                is DepositResponse.Error -> call.respond(
                    status = response.status,
                    message = response.error
                )
            }
        }

        post("/{accountNumber}/transaction/withdraw") {
            val accountNumber = call.parameters["accountNumber"]
            val requestBody = call.receive<WithdrawRequest>()
            when(val response = service.makeWithdraw(accountNumber = accountNumber, amount = Amount(value = requestBody.amount))) {
                is WithdrawResponse.Success -> call.response.status(HttpStatusCode.OK)
                is WithdrawResponse.Error -> call.respond(
                    status = response.status,
                    message = response.error
                )
            }
        }

        get("/{accountNumber}/transaction/historic") {
            val accountNumber = call.parameters["accountNumber"]
            when(val response = service.getTransactionsHistoric(accountNumber = accountNumber)) {
                is TransactionHistoricResponse.Success -> {
                    call.response.status(HttpStatusCode.OK)
                    call.respond(response.transactionsHistoric)
                }
                is TransactionHistoricResponse.Error -> call.respond(
                    status = response.status,
                    message = response.error
                )
            }
        }

    }

}