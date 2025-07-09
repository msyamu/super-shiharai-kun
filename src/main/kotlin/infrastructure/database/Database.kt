package com.example.infrastructure.database

import com.example.infrastructure.config.AppConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"

    fun init() {
        Database.connect(
            url = AppConfig.Database.jdbcUrl,
            driver = DRIVER_CLASS_NAME,
            user = AppConfig.Database.user,
            password = AppConfig.Database.password
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserTable, InvoiceTable)
        }
    }
}
