package com.example.application.usecase

import com.example.infrastructure.config.Constants
import com.example.presentation.dto.UserRegistrationRequest
import com.example.domain.model.NewUser
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

class UserRegistrationUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(request: UserRegistrationRequest): User {
        val existingUser = userRepository.findByEmail(request.email)
        if (existingUser != null) {
            throw IllegalArgumentException(Constants.EMAIL_ALREADY_EXISTS_ERROR)
        }

        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        val newUser = NewUser(
            companyName = request.companyName,
            name = request.name,
            email = request.email,
            password = hashedPassword
        )

        return userRepository.save(newUser)
    }
}