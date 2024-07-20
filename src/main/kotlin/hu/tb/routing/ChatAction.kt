package hu.tb.routing

import hu.tb.group.GroupController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.chatAction() {
    val groupController by inject<GroupController>()

    get("/action/create/{groupId}"){
        val id = call.parameters["groupId"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        groupController.createGroup(id)

        call.respond("Created new group with $id")
    }

    get("action/members") {
        groupController.getMembers()
    }
}