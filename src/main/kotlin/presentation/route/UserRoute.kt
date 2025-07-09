package com.example.presentation.route

import com.example.presentation.controller.UserController
import com.example.presentation.dto.LoginRequest
import com.example.presentation.dto.UserRegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userController: UserController) {
    route("/api/v1/auth") {
        post("/signup") {
            val request = call.receive<UserRegistrationRequest>()
            val response = userController.signup(request)
            call.respond(HttpStatusCode.Created, response)
        }
        
        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = userController.login(request)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}