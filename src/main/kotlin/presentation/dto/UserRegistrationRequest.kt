package com.example.presentation.dto

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    val companyName: String,
    val name: String,
    val email: String,
    val password: String
) : BaseRequest {
    
    companion object {
        private const val MAX_COMPANY_NAME_LENGTH = 255
        private const val MAX_USER_NAME_LENGTH = 255
        private const val MAX_EMAIL_LENGTH = 255
        private const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
    }
    
    override fun validate(): ValidationResult {
        return when {
            companyName.isBlank() -> 
                ValidationResult.Invalid("Company name cannot be blank")
            companyName.length > MAX_COMPANY_NAME_LENGTH -> 
                ValidationResult.Invalid("Company name cannot exceed $MAX_COMPANY_NAME_LENGTH characters")
            name.isBlank() -> 
                ValidationResult.Invalid("Name cannot be blank")
            name.length > MAX_USER_NAME_LENGTH -> 
                ValidationResult.Invalid("Name cannot exceed $MAX_USER_NAME_LENGTH characters")
            email.isBlank() -> 
                ValidationResult.Invalid("Email cannot be blank")
            !email.matches(EMAIL_REGEX) -> 
                ValidationResult.Invalid("Invalid email format")
            email.length > MAX_EMAIL_LENGTH -> 
                ValidationResult.Invalid("Email cannot exceed $MAX_EMAIL_LENGTH characters")
            password.isBlank() -> 
                ValidationResult.Invalid("Password cannot be blank")
            password.length < MIN_PASSWORD_LENGTH -> 
                ValidationResult.Invalid("Password must be at least $MIN_PASSWORD_LENGTH characters")
            else -> ValidationResult.Valid
        }
    }
}
