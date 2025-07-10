package application.usecase

import com.example.application.usecase.LoginUseCase
import com.example.domain.error.AuthenticationException
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.presentation.dto.LoginRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

class LoginUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val loginUseCase = LoginUseCase(userRepository)

    @Test
    fun `should login user successfully with valid credentials`() = runTest {
        // Given
        val hashedPassword = BCrypt.hashpw("validpassword", BCrypt.gensalt())
        val existingUser = User(
            id = 1,
            companyName = "テスト会社",
            name = "テスト太郎",
            email = "test@example.com",
            password = hashedPassword,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val request = LoginRequest(
            email = "test@example.com",
            password = "validpassword"
        )

        coEvery { userRepository.findByEmail("test@example.com") } returns existingUser

        // When
        val result = loginUseCase.execute(request)

        // Then
        assertEquals(existingUser, result)
        coVerify { userRepository.findByEmail("test@example.com") }
    }

    @Test
    fun `should throw exception for non-existent user`() = runTest {
        // Given
        val request = LoginRequest(
            email = "nonexistent@example.com",
            password = "anypassword"
        )

        coEvery { userRepository.findByEmail("nonexistent@example.com") } returns null

        // When & Then
        val exception = assertThrows<AuthenticationException> {
            loginUseCase.execute(request)
        }
        assertEquals("Invalid email or password", exception.message)
        coVerify { userRepository.findByEmail("nonexistent@example.com") }
    }

    @Test
    fun `should throw exception for invalid password`() = runTest {
        // Given
        val hashedPassword = BCrypt.hashpw("correctpassword", BCrypt.gensalt())
        val existingUser = User(
            id = 1,
            companyName = "テスト会社",
            name = "テスト太郎", 
            email = "test@example.com",
            password = hashedPassword,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val request = LoginRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        coEvery { userRepository.findByEmail("test@example.com") } returns existingUser

        // When & Then
        val exception = assertThrows<AuthenticationException> {
            loginUseCase.execute(request)
        }
        assertEquals("Invalid email or password", exception.message)
        coVerify { userRepository.findByEmail("test@example.com") }
    }

    @Test
    fun `should handle empty email`() = runTest {
        // Given
        val request = LoginRequest(
            email = "",
            password = "anypassword"
        )

        coEvery { userRepository.findByEmail("") } returns null

        // When & Then
        val exception = assertThrows<AuthenticationException> {
            loginUseCase.execute(request)
        }
        assertEquals("Invalid email or password", exception.message)
        coVerify { userRepository.findByEmail("") }
    }

    @Test
    fun `should handle empty password`() = runTest {
        // Given
        val hashedPassword = BCrypt.hashpw("correctpassword", BCrypt.gensalt())
        val existingUser = User(
            id = 1,
            companyName = "テスト会社",
            name = "テスト太郎",
            email = "test@example.com", 
            password = hashedPassword,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val request = LoginRequest(
            email = "test@example.com",
            password = ""
        )

        coEvery { userRepository.findByEmail("test@example.com") } returns existingUser

        // When & Then
        val exception = assertThrows<AuthenticationException> {
            loginUseCase.execute(request)
        }
        assertEquals("Invalid email or password", exception.message)
        coVerify { userRepository.findByEmail("test@example.com") }
    }
}