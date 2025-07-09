package infrastructure.repository

import com.example.domain.model.NewUser
import com.example.domain.model.User
import com.example.infrastructure.repository.UserRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import util.TestDatabaseUtil
import java.time.LocalDateTime

class UserRepositoryImplTest {

    private lateinit var database: Database
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeEach
    fun setup() {
        database = TestDatabaseUtil.createTestDatabase("user_repo")
        TestDatabaseUtil.setupTables(database)
        userRepository = UserRepositoryImpl()
    }

    @AfterEach
    fun cleanup() {
        TestDatabaseUtil.cleanupTables(database)
    }

    @Test
    fun `findByEmail should return user when email exists`() = runTest {
        // Given
        val testUser = createTestUser()
        val savedUser = userRepository.save(testUser)

        // When
        val foundUser = userRepository.findByEmail(testUser.email)

        // Then
        assertNotNull(foundUser)
        assertEquals(savedUser.id, foundUser!!.id)
        assertEquals(testUser.email, foundUser.email)
        assertEquals(testUser.companyName, foundUser.companyName)
        assertEquals(testUser.name, foundUser.name)
    }

    @Test
    fun `findByEmail should return null when email does not exist`() = runTest {
        // When
        val foundUser = userRepository.findByEmail("nonexistent@example.com")

        // Then
        assertNull(foundUser)
    }

    @Test
    fun `save should create new user with generated ID`() = runTest {
        // Given
        val newUser = createTestUser()

        // When
        val savedUser = userRepository.save(newUser)

        // Then
        assertNotNull(savedUser.id)
        assertTrue(savedUser.id > 0)
        assertEquals(newUser.companyName, savedUser.companyName)
        assertEquals(newUser.name, savedUser.name)
        assertEquals(newUser.email, savedUser.email)
        assertEquals(newUser.password, savedUser.password)
        assertNotNull(savedUser.createdAt)
        assertNotNull(savedUser.updatedAt)
    }

    @Test
    fun `save should set timestamps correctly`() = runTest {
        // Given
        val newUser = createTestUser()
        val beforeSave = LocalDateTime.now().minusSeconds(1)

        // When
        val savedUser = userRepository.save(newUser)

        // Then
        val afterSave = LocalDateTime.now().plusSeconds(1)
        assertTrue(savedUser.createdAt.isAfter(beforeSave))
        assertTrue(savedUser.createdAt.isBefore(afterSave))
        assertTrue(savedUser.updatedAt.isAfter(beforeSave))
        assertTrue(savedUser.updatedAt.isBefore(afterSave))
        assertEquals(savedUser.createdAt, savedUser.updatedAt)
    }

    @Test
    fun `save should handle multiple users with different emails`() = runTest {
        // Given
        val user1 = NewUser(
            companyName = "会社1",
            name = "ユーザー1",
            email = "user1@example.com",
            password = "hashedPassword1"
        )
        val user2 = NewUser(
            companyName = "会社2",
            name = "ユーザー2",
            email = "user2@example.com",
            password = "hashedPassword2"
        )

        // When
        val savedUser1 = userRepository.save(user1)
        val savedUser2 = userRepository.save(user2)

        // Then
        assertNotEquals(savedUser1.id, savedUser2.id)
        assertEquals(user1.email, savedUser1.email)
        assertEquals(user2.email, savedUser2.email)

        // Both should be findable
        val foundUser1 = userRepository.findByEmail(user1.email)
        val foundUser2 = userRepository.findByEmail(user2.email)
        assertNotNull(foundUser1)
        assertNotNull(foundUser2)
        assertEquals(savedUser1.id, foundUser1!!.id)
        assertEquals(savedUser2.id, foundUser2!!.id)
    }

    @Test
    fun `findByEmail should be case sensitive`() = runTest {
        // Given
        val newUser = createTestUser(email = "Test@Example.com")
        userRepository.save(newUser)

        // When
        val foundUpperCase = userRepository.findByEmail("TEST@EXAMPLE.COM")
        val foundLowerCase = userRepository.findByEmail("test@example.com")
        val foundExact = userRepository.findByEmail("Test@Example.com")

        // Then
        assertNull(foundUpperCase)
        assertNull(foundLowerCase)
        assertNotNull(foundExact)
    }

    @Test
    fun `save should preserve all user data correctly`() = runTest {
        // Given
        val newUser = NewUser(
            companyName = "特殊文字テスト株式会社・〜！@#$%^&*()",
            name = "長い名前のテストユーザー田中太郎",
            email = "complex.email+test@sub.domain.example.com",
            password = "veryLongHashedPasswordWithSpecialChars!@#$%^&*()123456789"
        )

        // When
        val savedUser = userRepository.save(newUser)
        val retrievedUser = userRepository.findByEmail(newUser.email)

        // Then
        assertNotNull(retrievedUser)
        assertEquals(newUser.companyName, retrievedUser!!.companyName)
        assertEquals(newUser.name, retrievedUser.name)
        assertEquals(newUser.email, retrievedUser.email)
        assertEquals(newUser.password, retrievedUser.password)
    }

    private fun createTestUser(
        companyName: String = "テスト株式会社",
        name: String = "テスト太郎",
        email: String = "test@example.com",
        password: String = "hashedPassword123"
    ): NewUser {
        return NewUser(
            companyName = companyName,
            name = name,
            email = email,
            password = password
        )
    }
}