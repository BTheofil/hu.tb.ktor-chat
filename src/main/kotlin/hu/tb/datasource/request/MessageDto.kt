package hu.tb.datasource.request

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class MessageDto(
    @BsonId
    val id: ObjectId,
    val sender: String,
    val message: String,
    val timeStamp: Long
)
