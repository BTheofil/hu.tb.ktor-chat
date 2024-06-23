package hu.tb.routing

import hu.tb.group.GroupController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.chatInfo() {
    val groupController by inject<GroupController>()
    get("/info/{groupId}") {
        val id = call.parameters["groupId"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val isExist = groupController.isGroupExist(id)

        call.respond(isExist)
    }

    get("/info/{groupId}/history") {
        val id = call.parameters["groupId"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val historyList = groupController.getHistory(id)

        call.respond(historyList)
    }

    get("/info/create/{groupId}"){
        val id = call.parameters["groupId"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        groupController.createGroup(id)

        call.respond("Created new group with $id")
    }
}