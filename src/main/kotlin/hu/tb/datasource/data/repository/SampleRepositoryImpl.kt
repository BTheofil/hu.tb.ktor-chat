package hu.tb.datasource.data.repository

import hu.tb.datasource.data.model.*
import hu.tb.repository.SampleRepository
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SampleRepositoryImpl() : SampleRepository {

    init {
        transaction {
            SchemaUtils.create(
                UserTable,
                MessageTable,
                GroupTable,
                GroupJunctionTable
            )
        }
    }

    override fun insertUser() {
        transaction {
            addLogger(StdOutSqlLogger)

            /*val elsoUser = UserEntity.new {
                name = "alma"
                password = "abc123"
            }

            val masodikUser = UserEntity.new {
                name = "barack"
                password = "qwe456"
            }

            val users = UserEntity.all().toList()

            val newGroup = GroupEntity.new {
                name = "elsoG"
            }
            newGroup.users = SizedCollection(users)*/

            UserEntity[1].groups.forEach { println(it.name) }
        }
    }
}