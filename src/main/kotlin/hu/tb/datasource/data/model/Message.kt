package hu.tb.datasource.data.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

object MessageTable : LongIdTable("messages") {
    val content = text("content")
    val timeStamp = long("time_stamp")
    val sender = reference("user", UserTable)
    val group = reference("group", GroupTable)
}

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MessageEntity>(MessageTable)

    var content by MessageTable.content
    var timeStamp by MessageTable.timeStamp
    var sender by UserTable.id
    var group by GroupTable.id
}