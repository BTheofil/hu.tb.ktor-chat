package hu.tb.datasource

import com.mongodb.MongoException
import org.bson.BsonValue
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import hu.tb.datasource.request.MessageDto
import hu.tb.datasource.sendModel.Message
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class MessageRepositoryImpl(
    private val mongoDb: MongoDatabase
) : MessageRepository {

    override suspend fun createGroup(groupId: String) {
        mongoDb.createCollection(groupId)
    }

    override suspend fun isGroupExist(
        groupId: String
    ): Boolean {
        val rawList = mongoDb.listCollectionNames().toList()
        return rawList.contains(groupId)
    }

    override suspend fun insertMessage(
        message: Message,
        groupId: String,
    ): BsonValue? {
        try {
            val result = mongoDb.getCollection<Message>(groupId).insertOne(message)
            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: $e")
            return null
        }
    }

    override suspend fun getHistory(groupId: String): List<Message> =
        mongoDb.getCollection<MessageDto>(groupId).find().map {
            Message(
                id = it.id.toString(),
                sender = it.sender,
                message = it.message,
                timeStamp = it.timeStamp
            )
        }.toList()

}