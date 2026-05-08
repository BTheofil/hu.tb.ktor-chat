package hu.tb.datasource.data.model

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

object GroupJunctionTable : Table() {
    val user = reference("user_id", UserTable)
    val group = reference("group_id", GroupTable)
    override val primaryKey = PrimaryKey(user, name = "PK_Group_Junction_Table")
}

object GroupTable : LongIdTable("groups") {
    val name = varchar("name", 100)
}

class GroupEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupEntity>(GroupTable)

    var name by GroupTable.name
    var users by UserEntity via GroupJunctionTable
}