package hu.tb.datasource.sample

import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SampleRepositoryImpl() : SampleRepository {

    init {
        transaction {
            //SchemaUtils.create(Users)
        }
    }

    override suspend fun countUsers(): Int =
        transaction {
            //return@transaction User.count().toInt()
            0
        }

    override fun addUser(): Int =
        transaction {
            /*val newUser = User.new {
                name = "test"
                password = "123"
            }

            return@transaction newUser.id.value*/

            0
        }
}