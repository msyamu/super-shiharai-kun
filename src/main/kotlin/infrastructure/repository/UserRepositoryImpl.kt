package com.example.infrastructure.repository

import com.example.domain.model.NewUser
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.infrastructure.database.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

class UserRepositoryImpl : UserRepository {
    override suspend fun findByEmail(email: String): User? = newSuspendedTransaction {
        UserTable.selectAll().where { UserTable.email eq email }
            .singleOrNull()
            ?.let { rowToUser(it) }
    }

    override suspend fun save(newUser: NewUser): User = newSuspendedTransaction {
        val now = LocalDateTime.now()
        val id = UserTable.insertAndGetId {
            it[companyName] = newUser.companyName
            it[name] = newUser.name
            it[email] = newUser.email
            it[password] = newUser.password
            it[createdAt] = now
            it[updatedAt] = now
        }
        
        User(
            id = id.value,
            companyName = newUser.companyName,
            name = newUser.name,
            email = newUser.email,
            password = newUser.password,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[UserTable.id].value,
            companyName = row[UserTable.companyName],
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password],
            createdAt = row[UserTable.createdAt],
            updatedAt = row[UserTable.updatedAt]
        )
    }
}