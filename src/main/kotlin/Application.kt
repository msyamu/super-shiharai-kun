package com.example

import com.example.di.DependencyInjection
import com.example.infrastructure.database.DatabaseFactory
import com.example.infrastructure.config.configureErrorHandling
import com.example.infrastructure.config.configureCallLogging
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

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
    
    configureErrorHandling()
    
    val dependencies = DependencyInjection.provideDependencies()
    configureRouting(dependencies)
}
