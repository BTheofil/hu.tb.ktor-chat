package hu.tb.datasource.sample

import hu.tb.datasource.sample.response.UserResponse
import org.bson.BsonValue

interface SampleRepository {
    suspend fun getUsers(): List<UserResponse>
    suspend fun insertExample(text: String): BsonValue?
}
