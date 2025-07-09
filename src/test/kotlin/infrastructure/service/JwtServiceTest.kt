package infrastructure.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.model.User
import com.example.infrastructure.service.JwtService
import config.TestAppConfig
import util.TestConfigUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.*

class JwtServiceTest {

    private lateinit var jwtService: JwtService
    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        TestConfigUtil.setupTestEnvironment()
        jwtService = JwtService()
        testUser = User(
            id = 1,
            companyName = "テスト株式会社",
            name = "テスト太郎",
            email = "test@example.com",
            password = "hashedPassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
    
    @AfterEach
    fun cleanup() {
        TestConfigUtil.clearTestEnvironment()
    }

    @Test
    fun `generateToken should create valid JWT token`() {
        // When
        val token = jwtService.generateToken(testUser)

        // Then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        
        // JWTトークンの形式を確認（3つの部分がドットで区切られている）
        val parts = token.split(".")
        assertEquals(3, parts.size)
    }

    @Test
    fun `generateToken should include user information in claims`() {
        // When
        val token = jwtService.generateToken(testUser)

        // Then
        val decodedJWT = JWT.decode(token)
        assertEquals(testUser.id.toString(), decodedJWT.subject)
        assertEquals(testUser.email, decodedJWT.getClaim("email").asString())
        assertEquals(testUser.companyName, decodedJWT.getClaim("companyName").asString())
    }

    @Test
    fun `generateToken should set expiration time`() {
        // Given
        val beforeGeneration = System.currentTimeMillis()

        // When
        val token = jwtService.generateToken(testUser)

        // Then
        val afterGeneration = System.currentTimeMillis()
        val decodedJWT = JWT.decode(token)
        val expiresAt = decodedJWT.expiresAt.time
        
        // 有効期限が現在時刻より後に設定されていることを確認
        assertTrue(expiresAt > beforeGeneration)
        assertTrue(expiresAt > afterGeneration)
    }

    @Test
    fun `verifyToken should verify valid token successfully`() {
        // Given
        val token = jwtService.generateToken(testUser)

        // When
        val decodedJWT = jwtService.verifyToken(token)

        // Then
        assertNotNull(decodedJWT)
        assertEquals(testUser.id.toString(), decodedJWT!!.subject)
        assertEquals(testUser.email, decodedJWT.getClaim("email").asString())
        assertEquals(testUser.companyName, decodedJWT.getClaim("companyName").asString())
    }

    @Test
    fun `verifyToken should return null for invalid token`() {
        // Given
        val invalidToken = "invalid.jwt.token"

        // When
        val decodedJWT = jwtService.verifyToken(invalidToken)

        // Then
        assertNull(decodedJWT)
    }

    @Test
    fun `verifyToken should return null for malformed token`() {
        // Given
        val malformedToken = "not-a-jwt-token"

        // When
        val decodedJWT = jwtService.verifyToken(malformedToken)

        // Then
        assertNull(decodedJWT)
    }

    @Test
    fun `verifyToken should return null for empty token`() {
        // Given
        val emptyToken = ""

        // When
        val decodedJWT = jwtService.verifyToken(emptyToken)

        // Then
        assertNull(decodedJWT)
    }

    @Test
    fun `verifyToken should return null for token signed with wrong secret`() {
        // Given - トークンを異なるシークレットで署名
        val wrongAlgorithm = Algorithm.HMAC256("wrong-secret")
        val tokenWithWrongSecret = JWT.create()
            .withIssuer(TestAppConfig.Jwt.issuer)
            .withSubject(testUser.id.toString())
            .withClaim("email", testUser.email)
            .withClaim("companyName", testUser.companyName)
            .withExpiresAt(Date(System.currentTimeMillis() + TestAppConfig.Jwt.expiresInMillis))
            .sign(wrongAlgorithm)

        // When
        val decodedJWT = jwtService.verifyToken(tokenWithWrongSecret)

        // Then
        assertNull(decodedJWT)
    }

    @Test
    fun `getVerifier should return valid JWTVerifier`() {
        // When
        val verifier = jwtService.getVerifier()

        // Then
        assertNotNull(verifier)
        
        // 有効なトークンで検証してみる
        val token = jwtService.generateToken(testUser)
        val decodedJWT = verifier.verify(token)
        assertNotNull(decodedJWT)
        assertEquals(testUser.id.toString(), decodedJWT.subject)
    }

    @Test
    fun `generateToken should create different tokens for different users`() {
        // Given
        val user1 = testUser
        val user2 = User(
            id = 2,
            companyName = "別の会社",
            name = "別のユーザー",
            email = "another@example.com",
            password = "anotherPassword",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // When
        val token1 = jwtService.generateToken(user1)
        val token2 = jwtService.generateToken(user2)

        // Then
        assertNotEquals(token1, token2)
        
        val decoded1 = jwtService.verifyToken(token1)
        val decoded2 = jwtService.verifyToken(token2)
        
        assertNotNull(decoded1)
        assertNotNull(decoded2)
        assertNotEquals(decoded1!!.subject, decoded2!!.subject)
        assertNotEquals(decoded1.getClaim("email").asString(), decoded2.getClaim("email").asString())
    }

    @Test
    fun `generateToken should create different tokens for same user at different times`() {
        // Given
        val user = testUser

        // When
        val token1 = jwtService.generateToken(user)
        Thread.sleep(1000) // 1秒待機してタイムスタンプを確実に変更
        val token2 = jwtService.generateToken(user)

        // Then
        assertNotEquals(token1, token2) // 生成時刻が異なるため、トークンも異なる
        
        val decoded1 = jwtService.verifyToken(token1)
        val decoded2 = jwtService.verifyToken(token2)
        
        assertNotNull(decoded1)
        assertNotNull(decoded2)
        assertEquals(decoded1!!.subject, decoded2!!.subject) // 同じユーザー
        assertEquals(decoded1.getClaim("email").asString(), decoded2.getClaim("email").asString()) // 同じユーザー
    }
}