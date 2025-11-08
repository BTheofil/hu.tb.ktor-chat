package hu.tb.datasource.sample

import com.mongodb.kotlin.client.coroutine.MongoClient
import hu.tb.datasource.sample.dto.User
import hu.tb.datasource.sample.model.UserSender
import kotlinx.coroutines.flow.toList

class SampleRepositoryImpl(
    private val mongoClient: MongoClient
) : SampleRepository {
    override suspend fun getUsers(): List<UserSender> {
        return mongoClient.getDatabase("sample_mflix")
            .getCollection<User>("users")
            .find()
            .toList().map {
                UserSender(
                    id = it.id.toString(),
                    name = it.name,
                    email = it.email,
                    password = it.password
                )
            }
    }
}