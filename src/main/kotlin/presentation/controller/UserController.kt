package com.example.presentation.controller

import com.example.application.usecase.LoginUseCase
import com.example.application.usecase.UserRegistrationUseCase
import com.example.domain.model.User
import com.example.infrastructure.service.JwtService
import com.example.presentation.dto.LoginRequest
import com.example.presentation.dto.LoginResponse
import com.example.presentation.dto.UserRegistrationRequest
import com.example.presentation.dto.UserResponse

class UserController(
    private val userRegistrationUseCase: UserRegistrationUseCase,
    private val loginUseCase: LoginUseCase,
    private val jwtService: JwtService
) {
    suspend fun signup(request: UserRegistrationRequest): UserResponse {
        val user = userRegistrationUseCase.execute(request)
        return user.toResponse()
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        val user = loginUseCase.execute(request)
        val token = jwtService.generateToken(user)
        return LoginResponse(token = token, user = user.toResponse())
    }
    
    private fun User.toResponse(): UserResponse {
        return UserResponse(
            id = id,
            companyName = companyName,
            name = name,
            email = email
        )
    }
}
