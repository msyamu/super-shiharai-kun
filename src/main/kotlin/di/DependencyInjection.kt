package com.example.di

import com.example.application.usecase.LoginUseCase
import com.example.application.usecase.UserRegistrationUseCase
import com.example.domain.repository.UserRepository
import com.example.infrastructure.repository.UserRepositoryImpl
import com.example.infrastructure.service.JwtService
import com.example.presentation.controller.UserController

object DependencyInjection {
    fun provideDependencies(): AppDependencies {
        val userRepository: UserRepository = UserRepositoryImpl()
        val userRegistrationUseCase = UserRegistrationUseCase(userRepository)
        val loginUseCase = LoginUseCase(userRepository)
        val jwtService = JwtService()
        val userController = UserController(userRegistrationUseCase, loginUseCase, jwtService)
        
        return AppDependencies(
            userController = userController
        )
    }
}

data class AppDependencies(
    val userController: UserController
)