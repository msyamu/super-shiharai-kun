package com.example.infrastructure.api.route

import com.example.infrastructure.config.getUserIdFromJwt
import com.example.presentation.controller.InvoiceController
import com.example.presentation.dto.InvoiceRegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.invoiceRoutes(invoiceController: InvoiceController) {
    route("/api/v1/invoices") {
        authenticate("jwt") {
            post {
                val userId = call.getUserIdFromJwt()
                val request = call.receive<InvoiceRegistrationRequest>()
                val response = invoiceController.registerInvoice(userId, request)
                call.respond(HttpStatusCode.Created, response)
            }

            get {
                val userId = call.getUserIdFromJwt()
                val startDate = call.request.queryParameters["startDate"]
                val endDate = call.request.queryParameters["endDate"]
                val response = invoiceController.getInvoices(userId, startDate, endDate)
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}
