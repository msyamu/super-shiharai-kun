package com.example

import com.example.di.DependencyInjection
import com.example.infrastructure.database.DatabaseFactory
import com.example.infrastructure.config.configureErrorHandling
import com.example.infrastructure.config.configureCallLogging
import com.example.infrastructure.config.configureAuthentication
import com.example.infrastructure.config.AppConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    System.setProperty("kotlinx.coroutines.dispatcher.name", "shiharai-kun")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()

    configureCallLogging()

    install(ContentNegotiation) {
        json()
    }

    // CORS設定 (local環境のみ)
    if (AppConfig.Server.environment == "local") {
        install(CORS) {
            anyHost()
            allowCredentials = true
            allowNonSimpleContentTypes = true
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
        }
    }

    // OpenAPI and Swagger (local環境のみ)
    if (AppConfig.Server.environment == "local") {
        routing {
            openAPI(path = "openapi", swaggerFile = "openapi.yaml")
            swaggerUI(path = "swagger", swaggerFile = "openapi.yaml")
        }
    }

    val dependencies = DependencyInjection.provideDependencies()
    configureAuthentication(dependencies.jwtService)
    configureErrorHandling()

    configureRouting(dependencies)
}
