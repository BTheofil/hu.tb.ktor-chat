package hu.tb.datasource

import com.mongodb.MongoException
import org.bson.BsonValue
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import hu.tb.datasource.sendModel.Message

private const val MESSAGE_COLLECTION = "message_collection"

class MessageRepositoryImpl(
    private val mongoDb: MongoDatabase
): MessageRepository {

    override suspend fun insertMessage(message: Message): BsonValue? {
        try {
            val result = mongoDb.getCollection<Message>(MESSAGE_COLLECTION).insertOne(message)
            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: $e")
            return null
        }
    }
}