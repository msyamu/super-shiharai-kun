package util

import com.example.infrastructure.database.InvoiceTable
import com.example.infrastructure.database.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

object TestDatabaseUtil {
    
    fun createTestDatabase(name: String = "test"): Database {
        return Database.connect(
            url = "jdbc:h2:mem:${name}_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
            driver = "org.h2.Driver",
            user = "test",
            password = ""
        )
    }
    
    fun setupTables(database: Database) {
        transaction(database) {
            SchemaUtils.create(UserTable, InvoiceTable)
        }
    }
    
    fun cleanupTables(database: Database) {
        transaction(database) {
            SchemaUtils.drop(UserTable, InvoiceTable)
        }
    }
    
    fun clearTables(database: Database) {
        transaction(database) {
            InvoiceTable.deleteAll()
            UserTable.deleteAll()
        }
    }
    
    fun createTestUser(
        database: Database,
        companyName: String = "Test Company",
        name: String = "Test User",
        email: String = "test@example.com",
        password: String = "password123"
    ): Int {
        return transaction(database) {
            val insertStatement = UserTable.insert {
                it[UserTable.companyName] = companyName
                it[UserTable.name] = name
                it[UserTable.email] = email
                it[UserTable.password] = password
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            insertStatement[UserTable.id].value
        }
    }

    fun createTestInvoice(
        database: Database,
        userId: Int,
        paymentAmount: String = "10000.00",
        issueDate: String = "2025-01-01",
        paymentDueDate: String = "2025-12-31"
    ): Int {
        return transaction(database) {
            val amount = BigDecimal(paymentAmount)
            val fee = amount.multiply(BigDecimal("0.04"))
            val taxAmount = fee.multiply(BigDecimal("0.10"))
            val totalAmount = amount.add(fee).add(taxAmount)
            
            val insertStatement = InvoiceTable.insert {
                it[InvoiceTable.userId] = userId
                it[InvoiceTable.issueDate] = LocalDate.parse(issueDate)
                it[InvoiceTable.paymentAmount] = amount
                it[InvoiceTable.fee] = fee
                it[InvoiceTable.feeRate] = BigDecimal("0.04")
                it[InvoiceTable.taxAmount] = taxAmount
                it[InvoiceTable.taxRate] = BigDecimal("0.10")
                it[InvoiceTable.totalAmount] = totalAmount
                it[InvoiceTable.paymentDueDate] = LocalDate.parse(paymentDueDate)
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            insertStatement[InvoiceTable.id].value
        }
    }
}