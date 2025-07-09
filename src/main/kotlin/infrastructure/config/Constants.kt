package com.example.infrastructure.config

import java.math.BigDecimal

object Constants {
    // Fee and Tax Rates
    val FEE_RATE = BigDecimal("0.04")
    val TAX_RATE = BigDecimal("0.10")

    // Error Messages
    const val EMAIL_ALREADY_EXISTS_ERROR = "Email already exists"
    const val INVALID_CREDENTIALS_ERROR = "Invalid email or password"
    const val USER_ID_NOT_FOUND_ERROR = "User ID not found in JWT token"
    
    // Environment Constants
    object Environment {
        const val LOCAL = "local"
        const val PRODUCTION = "production"
        const val DEVELOPMENT = "development"
    }
}
