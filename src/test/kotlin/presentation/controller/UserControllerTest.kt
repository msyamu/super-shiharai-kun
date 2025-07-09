package presentation.controller

import com.example.application.usecase.LoginUseCase
import com.example.application.usecase.UserRegistrationUseCase
import com.example.domain.model.User
import com.example.infrastructure.service.JwtService
import com.example.presentation.controller.UserController
import com.example.presentation.dto.LoginRequest
import com.example.presentation.dto.UserRegistrationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class UserControllerTest {

    private lateinit var userRegistrationUseCase: UserRegistrationUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var jwtService: JwtService
    private lateinit var userController: UserController

    @BeforeEach
    fun setup() {
        userRegistrationUseCase = mockk()
        loginUseCase = mockk()
        jwtService = mockk()
        userController = UserController(userRegistrationUseCase, loginUseCase, jwtService)
    }

    @Test
    fun `register should return UserResponse when registration is successful`() = runTest {
        // Given
        val request = UserRegistrationRequest(
            companyName = "テスト会社",
            name = "テスト太郎",
            email = "test@example.com",
            password = "password123"
        )
        
        val registeredUser = User(
            id = 1,
            companyName = "テスト会社",
            name = "テスト太郎",
            email = "test@example.com",
            password = "hashedPassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { userRegistrationUseCase.execute(request) } returns registeredUser

        // When
        val result = userController.signup(request)

        // Then
        assertEquals(registeredUser.id, result.id)
        assertEquals(registeredUser.companyName, result.companyName)
        assertEquals(registeredUser.name, result.name)
        assertEquals(registeredUser.email, result.email)
        
        coVerify { userRegistrationUseCase.execute(request) }
    }

    @Test
    fun `register should propagate exception when usecase throws exception`() = runTest {
        // Given
        val request = UserRegistrationRequest(
            companyName = "テスト会社",
            name = "テスト太郎",
            email = "existing@example.com",
            password = "password123"
        )

        coEvery { userRegistrationUseCase.execute(request) } throws IllegalArgumentException("Email already exists")

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            userController.signup(request)
        }
        assertEquals("Email already exists", exception.message)
        
        coVerify { userRegistrationUseCase.execute(request) }
    }

    @Test
    fun `login should return LoginResponse when credentials are valid`() = runTest {
        // Given
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )
        
        val user = User(
            id = 1,
            companyName = "テスト会社",
            name = "テスト太郎",
            email = "test@example.com",
            password = "hashedPassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val jwtToken = "jwt.token.here"

        coEvery { loginUseCase.execute(request) } returns user
        every { jwtService.generateToken(user) } returns jwtToken

        // When
        val result = userController.login(request)

        // Then
        assertEquals(jwtToken, result.token)
        assertEquals(user.id, result.user.id)
        assertEquals(user.companyName, result.user.companyName)
        assertEquals(user.name, result.user.name)
        assertEquals(user.email, result.user.email)
        
        coVerify { loginUseCase.execute(request) }
        coVerify { jwtService.generateToken(user) }
    }

    @Test
    fun `login should propagate exception when credentials are invalid`() = runTest {
        // Given
        val request = LoginRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        coEvery { loginUseCase.execute(request) } throws IllegalArgumentException("Invalid email or password")

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            userController.login(request)
        }
        assertEquals("Invalid email or password", exception.message)
        
        coVerify { loginUseCase.execute(request) }
        // JWTサービスは呼ばれないはず
        coVerify(exactly = 0) { jwtService.generateToken(any()) }
    }

    @Test
    fun `register should not include password in response`() = runTest {
        // Given
        val request = UserRegistrationRequest(
            companyName = "セキュア会社",
            name = "セキュア太郎",
            email = "secure@example.com",
            password = "securePassword123"
        )
        
        val registeredUser = User(
            id = 1,
            companyName = "セキュア会社",
            name = "セキュア太郎",
            email = "secure@example.com",
            password = "hashedSecurePassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { userRegistrationUseCase.execute(request) } returns registeredUser

        // When
        val result = userController.signup(request)

        // Then
        assertEquals(registeredUser.id, result.id)
        assertEquals(registeredUser.companyName, result.companyName)
        assertEquals(registeredUser.name, result.name)
        assertEquals(registeredUser.email, result.email)
        
        // UserResponseにはpasswordフィールドが存在しないことを確認
        val responseString = result.toString()
        assertFalse(responseString.contains("password", ignoreCase = true))
    }

    @Test
    fun `login should not include password in user response`() = runTest {
        // Given
        val request = LoginRequest(
            email = "secure@example.com",
            password = "securePassword123"
        )
        
        val user = User(
            id = 1,
            companyName = "セキュア会社",
            name = "セキュア太郎",
            email = "secure@example.com",
            password = "hashedSecurePassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val jwtToken = "secure.jwt.token"

        coEvery { loginUseCase.execute(request) } returns user
        every { jwtService.generateToken(user) } returns jwtToken

        // When
        val result = userController.login(request)

        // Then
        assertEquals(jwtToken, result.token)
        assertEquals(user.id, result.user.id)
        assertEquals(user.companyName, result.user.companyName)
        assertEquals(user.name, result.user.name)
        assertEquals(user.email, result.user.email)
        
        // UserResponseにはpasswordフィールドが存在しないことを確認
        val userResponseString = result.user.toString()
        assertFalse(userResponseString.contains("password", ignoreCase = true))
    }
}