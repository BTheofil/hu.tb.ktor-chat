package hu.tb.group

import hu.tb.datasource.MessageRepository
import hu.tb.datasource.sendModel.Message
import hu.tb.model.Member
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import java.util.concurrent.ConcurrentHashMap

class GroupController(
    private val messageRepository: MessageRepository
) {
    private val onlineMembers = ConcurrentHashMap<String, Member>()

    fun join(
        name: String,
        socketSession: WebSocketSession
    ) {
        onlineMembers[name] = Member(
            name,
            socketSession
        )
    }

    suspend fun isGroupExist(groupId: String): Boolean = messageRepository.isGroupExist(groupId)

    suspend fun sendMessage(
        sender: String,
        message: String,
        groupId: String
    ) {
        onlineMembers.values.forEach { member ->
            //create msg
            val messageObject = Message(
                id = ObjectId().toString(),
                sender = sender,
                message = message,
                timeStamp = System.currentTimeMillis()
            )

            //same msg to db
            messageRepository.insertMessage(messageObject, groupId)

            //send msg to others
            val parseMessage = Json.encodeToString(messageObject)
            member.socket.send(Frame.Text(parseMessage))
        }
    }

    suspend fun getHistory(groupId: String): List<Message> = messageRepository.getHistory(groupId)

    suspend fun leave(
        leaver: String
    ) {
        onlineMembers[leaver]?.socket?.close()
        if (onlineMembers.containsKey(leaver)) {
            onlineMembers.remove(leaver)
        }
    }
}