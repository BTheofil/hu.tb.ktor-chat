package hu.tb.datasource.sample

import com.mongodb.kotlin.client.coroutine.MongoClient
import hu.tb.datasource.sample.mongo.TextMongo
import hu.tb.datasource.sample.mongo.UserMongo
import hu.tb.datasource.sample.response.UserResponse
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue

class SampleRepositoryImpl(
    private val mongoClient: MongoClient
) : SampleRepository {
    override suspend fun getUsers(): List<UserResponse> {
        return mongoClient.getDatabase(DB_NAME)
            .getCollection<UserMongo>(USER_COLLECTION)
            .find()
            .toList().map { it.toResponse() }
    }

    override suspend fun insertExample(text: String): BsonValue? {
        val insertedText = mongoClient.getDatabase(DB_NAME)
            .getCollection<TextMongo>(TEXT_COLLECTION)
            .insertOne(TextMongo(text))

        return insertedText.insertedId
    }
}

private const val DB_NAME = "sample_mflix"
private const val USER_COLLECTION = "users"
private const val TEXT_COLLECTION = "simple_texts"