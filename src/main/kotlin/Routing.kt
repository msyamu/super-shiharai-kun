package com.example

import com.example.di.AppDependencies
import com.example.presentation.route.healthRoutes
import com.example.presentation.route.invoiceRoutes
import com.example.presentation.route.userRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting(dependencies: AppDependencies) {
    routing {
        healthRoutes()
        userRoutes(dependencies.userController)
        invoiceRoutes(dependencies.invoiceController)
    }
}
