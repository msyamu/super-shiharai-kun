package presentation.dto

import com.example.presentation.dto.UserRegistrationRequest
import io.ktor.server.plugins.requestvalidation.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class UserRegistrationRequestTest {

    @Test
    fun `should return Valid when all fields are valid`() {
        // Given
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should return Invalid when companyName is blank`() {
        // Given
        val request = UserRegistrationRequest(
            companyName = "",
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Company name cannot be blank", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when companyName exceeds max length`() {
        // Given
        val longCompanyName = "a".repeat(256)
        val request = UserRegistrationRequest(
            companyName = longCompanyName,
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Company name cannot exceed 255 characters", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when name is blank`() {
        // Given
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "",
            email = "test@example.com",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Name cannot be blank", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when name exceeds max length`() {
        // Given
        val longName = "a".repeat(256)
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = longName,
            email = "test@example.com",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Name cannot exceed 255 characters", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when email is blank`() {
        // Given
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = "",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Email cannot be blank", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when email format is invalid`() {
        // Given
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = "invalid-email",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Invalid email format", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when email exceeds max length`() {
        // Given
        val longEmailPrefix = "a".repeat(256 - "@example.com".length)
        val longEmail = "${longEmailPrefix}@example.com"
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = longEmail,
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Email cannot exceed 255 characters", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when password is blank`() {
        // Given
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = "test@example.com",
            password = ""
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Password cannot be blank", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when password is too short`() {
        // Given
        val shortPassword = "a".repeat(7)
        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = "test@example.com",
            password = shortPassword
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Password must be at least 8 characters", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Valid when fields are at boundary values`() {
        // Given
        val maxCompanyName = "a".repeat(255)
        val maxUserName = "b".repeat(255)
        val emailPrefix = "c".repeat(255 - "@example.com".length)
        val maxEmail = "${emailPrefix}@example.com"
        val minPassword = "d".repeat(8)
        
        val request = UserRegistrationRequest(
            companyName = maxCompanyName,
            name = maxUserName,
            email = maxEmail,
            password = minPassword
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }
}