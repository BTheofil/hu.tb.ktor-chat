package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.GroupCreateReceive
import hu.tb.domain.receive.GroupLeaveReceive
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.groupApi() {

    val chatRepository by inject<ChatRepository>()

    post("/createGroup") {
        val usersInfo = call.receive<GroupCreateReceive>()
        val newGroup = chatRepository.createNewGroup(
            currentUserId = usersInfo.currentUserId,
            otherUserId = usersInfo.otherUserId
        )
        newGroup?.let {
            call.respond(message = it, status = HttpStatusCode.Created)
        } ?:
        call.respondText(text = "Can not create group :c", status = HttpStatusCode.NoContent)
    }

    delete("/leaveGroup") {
        val infos = call.receive<GroupLeaveReceive>()
        chatRepository.leaveGroup(
            userId = infos.leaveUserId,
            groupId = infos.targetGroupId
        )

        call.respondText(
            text = "User with ${infos.leaveUserId} left the ${infos.targetGroupId} group id",
            status = HttpStatusCode.OK
        )
    }
}