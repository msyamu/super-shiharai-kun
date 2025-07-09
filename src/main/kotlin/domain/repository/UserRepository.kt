package com.example.domain.repository

import com.example.domain.model.NewUser
import com.example.domain.model.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun save(newUser: NewUser): User
}
