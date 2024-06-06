package hu.tb.datasource

import hu.tb.datasource.sendModel.Message
import org.bson.BsonValue

interface MessageRepository {

    suspend fun insertMessage(message: Message): BsonValue?
}