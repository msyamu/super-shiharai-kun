package com.example.presentation.dto

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
) : BaseRequest {
    
    companion object {
        private const val MAX_EMAIL_LENGTH = 255
        private const val MAX_PASSWORD_LENGTH = 255
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
    }
    
    override fun validate(): ValidationResult {
        return when {
            email.isBlank() ->
                ValidationResult.Invalid("Email cannot be blank")
            email.length > MAX_EMAIL_LENGTH ->
                ValidationResult.Invalid("Email cannot exceed $MAX_EMAIL_LENGTH characters")
            !email.matches(EMAIL_REGEX) ->
                ValidationResult.Invalid("Invalid email format")
            password.isBlank() ->
                ValidationResult.Invalid("Password cannot be blank")
            password.length > MAX_PASSWORD_LENGTH ->
                ValidationResult.Invalid("Password cannot exceed $MAX_PASSWORD_LENGTH characters")
            else -> ValidationResult.Valid
        }
    }
}
