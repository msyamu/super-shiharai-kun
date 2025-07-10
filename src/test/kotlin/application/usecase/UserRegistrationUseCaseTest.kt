package application.usecase

import com.example.application.usecase.UserRegistrationUseCase
import com.example.domain.model.NewUser
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.domain.error.UserAlreadyExistsException
import com.example.presentation.dto.UserRegistrationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class UserRegistrationUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val userRegistrationUseCase = UserRegistrationUseCase(userRepository)

    @Test
    fun `should register user successfully when email is unique`() = runTest {
        // Given
        val request = UserRegistrationRequest(
            companyName = "テスト株式会社",
            name = "田中太郎",
            email = "tanaka@example.com",
            password = "password123"
        )

        val savedUser = User(
            id = 1,
            companyName = "テスト株式会社",
            name = "田中太郎",
            email = "tanaka@example.com",
            password = "hashedPassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { userRepository.findByEmail("tanaka@example.com") } returns null
        coEvery { userRepository.save(any<NewUser>()) } returns savedUser

        // When
        val result = userRegistrationUseCase.execute(request)

        // Then
        assertEquals(savedUser, result)
        coVerify { userRepository.findByEmail("tanaka@example.com") }
        coVerify { userRepository.save(any<NewUser>()) }
    }

    @Test
    fun `should throw exception when email already exists`() = runTest {
        // Given
        val request = UserRegistrationRequest(
            companyName = "テスト株式会社",
            name = "田中太郎",
            email = "existing@example.com",
            password = "password123"
        )

        val existingUser = User(
            id = 1,
            companyName = "既存会社",
            name = "既存ユーザー",
            email = "existing@example.com",
            password = "hashedPassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { userRepository.findByEmail("existing@example.com") } returns existingUser

        // When & Then
        val exception = assertThrows<UserAlreadyExistsException> {
            userRegistrationUseCase.execute(request)
        }

        assertEquals("User with email 'existing@example.com' already exists", exception.message)
        coVerify { userRepository.findByEmail("existing@example.com") }
        coVerify(exactly = 0) { userRepository.save(any<NewUser>()) }
    }

    @Test
    fun `should create NewUser with hashed password`() = runTest {
        // Given
        val request = UserRegistrationRequest(
            companyName = "セキュリティ会社",
            name = "セキュリティ太郎",
            email = "security@example.com",
            password = "securePassword123"
        )

        val savedUser = User(
            id = 1,
            companyName = "セキュリティ会社",
            name = "セキュリティ太郎",
            email = "security@example.com",
            password = "hashedSecurePassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { userRepository.findByEmail("security@example.com") } returns null
        coEvery { userRepository.save(any<NewUser>()) } returns savedUser

        // When
        userRegistrationUseCase.execute(request)

        // Then
        coVerify {
            userRepository.save(match<NewUser> { newUser ->
                newUser.companyName == "セキュリティ会社" &&
                newUser.name == "セキュリティ太郎" &&
                newUser.email == "security@example.com" &&
                newUser.password != "securePassword123"
            })
        }
    }
}
