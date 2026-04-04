package hu.tb.datasource.data.repository

import hu.tb.datasource.data.model.*
import hu.tb.repository.SampleRepository
import hu.tb.repository.domain.send.User
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SampleRepositoryImpl : SampleRepository {

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

    @OptIn(ExperimentalTime::class)
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

            //UserEntity.all().forEach { println(it.name) }

            //UserEntity[2].groups.forEach { println(it.name) }

            /*MessageEntity.new {
                content = "first message"
                timeStamp = Clock.System.now().epochSeconds
                sender = UserEntity[1]
                group = GroupEntity[1]
            }*/
        }
    }

    fun createNewUser(
        username: String,
        userPassword: String
    ): Long =
        transaction {
            UserEntity.new {
                name = username
                password = userPassword
            }
        }.id.value

    override fun getUserById(userId: Long): User? {
        val userEntity = transaction {
            UserEntity.findById(userId)
        }
        userEntity?.let {
            return User(
                name = userEntity.name,
                password = userEntity.password
            )
        }
        return null
    }

}