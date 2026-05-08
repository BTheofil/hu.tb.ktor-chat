package hu.tb.datasource.data.repository

import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun <T> transactionLogger(statement: () -> T): T = transaction {
    addLogger(StdOutSqlLogger)
    statement()
}