package hu.tb.datasource.data.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object UserTable : UuidTable("user_table") {
    val name = varchar("name", 128)
    val password = varchar("password", 128)
}

class UserEntity @OptIn(ExperimentalUuidApi::class) constructor(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<UserEntity>(UserTable)

    @OptIn(ExperimentalUuidApi::class)
    var name by UserTable.name
    @OptIn(ExperimentalUuidApi::class)
    var password by UserTable.password
}