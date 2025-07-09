package com.example.application.usecase

import com.example.infrastructure.config.Constants
import com.example.application.error.AuthenticationException
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.presentation.dto.LoginRequest
import org.mindrot.jbcrypt.BCrypt

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(request: LoginRequest): User {
        val user = userRepository.findByEmail(request.email)
            ?: throw AuthenticationException(Constants.INVALID_CREDENTIALS_ERROR)

        if (!BCrypt.checkpw(request.password, user.password)) {
            throw AuthenticationException(Constants.INVALID_CREDENTIALS_ERROR)
        }

        return user
    }
}
