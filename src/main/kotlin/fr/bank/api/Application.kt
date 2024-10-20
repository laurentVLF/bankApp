package fr.bank.api

import fr.bank.api.plugins.configureHTTP
import fr.bank.api.plugins.configureKoin
import fr.bank.api.plugins.configureRouting
import fr.bank.api.plugins.configureSerialization
import io.ktor.server.application.Application

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureHTTP()
    configureRouting()
}