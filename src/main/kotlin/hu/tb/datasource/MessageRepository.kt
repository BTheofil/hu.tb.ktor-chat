package hu.tb.datasource

import hu.tb.datasource.sendModel.Message
import org.bson.BsonValue
import java.lang.reflect.Member

interface MessageRepository {

    suspend fun createGroup(groupId: String)

    suspend fun isGroupExist(groupId: String): Boolean

    suspend fun insertMessage(message: Message, groupId: String): BsonValue?

    suspend fun getHistory(groupId: String): List<Message>

    suspend fun getMembers(): List<Member>
}