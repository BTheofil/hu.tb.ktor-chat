package hu.tb.datasource.sample.mongo

import hu.tb.datasource.sample.response.UserResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class UserMongo(
    @BsonId val id: ObjectId = ObjectId(),
    val name: String,
    val email: String,
    val password: String
) {
    fun toResponse() = UserResponse(
        id = id.toString(),
        name = name,
        email = email,
        password = password
    )
}
