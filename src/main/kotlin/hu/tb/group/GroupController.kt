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

    private val members = ConcurrentHashMap<String, Member>()

    fun join(
        name: String,
        socketSession: WebSocketSession
    ) {
        members[name] = Member(
            name,
            socketSession
        )
    }

    suspend fun sendMessage(
        sender: String,
        message: String
    ) {
        members.values.forEach { member ->
            //create msg
            val messageObject = Message(
                id = ObjectId().toString(),
                sender = sender,
                message = message,
                timeStamp = System.currentTimeMillis()
            )

            //same msg to db
            messageRepository.insertMessage(messageObject)

            //send msg to others
            val parseMessage = Json.encodeToString(messageObject)
            member.socket.send(Frame.Text(parseMessage))
        }
    }

    suspend fun leave(
        leaver: String
    ) {
        members[leaver]?.socket?.close()
        if (members.containsKey(leaver)) {
            members.remove(leaver)
        }
    }
}