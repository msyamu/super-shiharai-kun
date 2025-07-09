package presentation.dto

import com.example.presentation.dto.LoginRequest
import io.ktor.server.plugins.requestvalidation.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class LoginRequestTest {

    @Test
    fun `should return Valid when all fields are valid`() {
        // Given
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should return Invalid when email is blank`() {
        // Given
        val request = LoginRequest(
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
        val request = LoginRequest(
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
    fun `should return Invalid when password is blank`() {
        // Given
        val request = LoginRequest(
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
    fun `should return Valid with various valid email formats`() {
        val validEmails = listOf(
            "user@example.com",
            "test.email@example.co.jp",
            "user+tag@example.org",
            "user_name@example-domain.com",
            "123@example.com",
            "a@b.co"
        )

        validEmails.forEach { email ->
            // Given
            val request = LoginRequest(
                email = email,
                password = "password123"
            )

            // When
            val result = request.validate()

            // Then
            assertEquals(ValidationResult.Valid, result, "Email $email should be valid")
        }
    }

    @Test
    fun `should return Invalid with invalid email - no at symbol`() {
        // Given
        val request = LoginRequest(
            email = "plainaddress",
            password = "password123"
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Invalid email format", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid with invalid email - missing username`() {
        // Given
        val request = LoginRequest(
            email = "@example.com",
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
        val request = LoginRequest(
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
    fun `should return Invalid when password exceeds max length`() {
        // Given
        val longPassword = "a".repeat(256)
        val request = LoginRequest(
            email = "test@example.com",
            password = longPassword
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Password cannot exceed 255 characters", (result as ValidationResult.Invalid).reasons.first())
    }
}