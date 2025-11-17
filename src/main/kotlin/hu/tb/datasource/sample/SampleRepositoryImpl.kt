package hu.tb.datasource.sample

import hu.tb.datasource.data.User
import hu.tb.datasource.data.Users
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SampleRepositoryImpl() : SampleRepository {

    init {
        Database.connect(
            url = "jdbc:sqlite:data.db",
            driver = "org.sqlite.JDBC"
        )
        transaction {
            SchemaUtils.create(Users)
        }
    }

    override suspend fun countUsers(): Int =
        transaction {
            return@transaction User.count().toInt()
        }

    override fun addUser(): Int =
        transaction {
            val newUser = User.new {
                name = "test"
                password = "123"
            }

            return@transaction newUser.id.value
        }
}