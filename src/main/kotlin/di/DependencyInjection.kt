package com.example.di

import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.application.usecase.InvoiceListUseCase
import com.example.application.usecase.LoginUseCase
import com.example.application.usecase.UserRegistrationUseCase
import com.example.domain.repository.InvoiceRepository
import com.example.domain.repository.UserRepository
import com.example.infrastructure.repository.InvoiceRepositoryImpl
import com.example.infrastructure.repository.UserRepositoryImpl
import com.example.infrastructure.service.JwtService
import com.example.presentation.controller.InvoiceController
import com.example.presentation.controller.UserController

object DependencyInjection {
    fun provideDependencies(): AppDependencies {
        val userRepository: UserRepository = UserRepositoryImpl()
        val invoiceRepository: InvoiceRepository = InvoiceRepositoryImpl()

        val userRegistrationUseCase = UserRegistrationUseCase(userRepository)
        val loginUseCase = LoginUseCase(userRepository)
        val invoiceRegistrationUseCase = InvoiceRegistrationUseCase(invoiceRepository)
        val invoiceListUseCase = InvoiceListUseCase(invoiceRepository)

        val jwtService = JwtService()
        val userController = UserController(userRegistrationUseCase, loginUseCase, jwtService)
        val invoiceController = InvoiceController(invoiceRegistrationUseCase, invoiceListUseCase)

        return AppDependencies(
            userController = userController,
            invoiceController = invoiceController,
            jwtService = jwtService
        )
    }
}

data class AppDependencies(
    val userController: UserController,
    val invoiceController: InvoiceController,
    val jwtService: JwtService
)
