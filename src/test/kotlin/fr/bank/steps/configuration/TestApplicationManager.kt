package fr.bank.steps.configuration

import fr.bank.api.input.DepositRequest
import fr.bank.api.input.WithdrawRequest
import fr.bank.api.plugins.configureRouting
import fr.bank.api.plugins.configureSerialization
import fr.bank.domain.model.OperationEnum
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object TestApplicationManager {
    private var server: io.ktor.server.engine.ApplicationEngine? = null
    private var httpClient: HttpClient? = null

    @JvmStatic
    fun startApplication() {
        if (server == null) {
            server =
                embeddedServer(Netty, port = 8080) {
                    configureSerialization()
                    configureRouting()
                }.start(wait = false)
        }
        if (httpClient == null) {
            httpClient =
                HttpClient(CIO) {
                    expectSuccess = false
                }
        }
    }

    @JvmStatic
    fun stopApplication() {
        httpClient?.close()
        httpClient = null
        server?.stop(1000, 10000)
        server = null
    }

    @JvmStatic
    fun getClient(): HttpClient {
        return checkNotNull(httpClient) { "Application is not started. Call startApplication() first." }
    }

    @JvmStatic
    fun sendRequest(
        path: String,
        httpMethod: HttpMethod,
        type: OperationEnum? = null,
        request: Any? = null,
    ) = runBlocking {
        val baseUrl = "http://localhost:8080"
        when (httpMethod) {
            HttpMethod.Get -> {
                getClient().get("$baseUrl$path") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }
            HttpMethod.Post -> {
                getClient().post("$baseUrl$path") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    if (type!! == OperationEnum.DEPOSIT) {
                        setBody(Json.encodeToString(request!! as DepositRequest))
                    } else {
                        setBody(Json.encodeToString(request!! as WithdrawRequest))
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("Invalid HTTP method.")
            }
        }
    }
}
