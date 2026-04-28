import hu.tb.domain.receive.GroupCreateReceive
import hu.tb.domain.receive.GroupLeaveReceive
import hu.tb.domain.receive.UserDeleteReceive
import hu.tb.domain.receive.UserSearchReceive
import hu.tb.domain.send.User
import hu.tb.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {

    @Test
    fun `ping server test`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "build.isDeveloperMode" to "true",
                "jwt.realm" to "message app",
                "jwt.audience" to "user messenger app",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.secret" to "secretTest"
            )
        }

        application.module()

        val response = client.get("/ping")
        assertEquals("pong", response.bodyAsText())
    }

    @Test
    fun `test user create-get-delete`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "build.isDeveloperMode" to "true",
                "jwt.realm" to "message app",
                "jwt.audience" to "user messenger app",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.secret" to "secretTest"
            )
        }
        application.module()
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "John-Tester", password = "abc-123"))
        }
        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget("Michel-Tester", password = "ice-cream"))
        }

        val searchedJohn = client.get("/searchUserByNameAndPw") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "John-Tester", password = "abc-123"))
        }
        assertEquals("John-Tester", searchedJohn.body<User>().name)
        assertEquals("abc-123", searchedJohn.body<User>().password)

        val emptyUser = client.delete("/deleteUser")
        assertEquals(HttpStatusCode.UnsupportedMediaType, emptyUser.status)

        val johnDelete = client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = searchedJohn.body<User>().id))
        }
        assertEquals(
            "User with ${searchedJohn.body<User>().id} id deleted",
            johnDelete.bodyAsText()
        )

        val searchedMichel = client.get("/searchUserByNameAndPw") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget("Michel-Tester", password = "ice-cream"))
        }
        val michelDelete = client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = searchedMichel.body<User>().id))
        }
        assertEquals(HttpStatusCode.OK, michelDelete.status)
    }

    @Test
    fun `test group create-delete`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "build.isDeveloperMode" to "true",
                "jwt.realm" to "message app",
                "jwt.audience" to "user messenger app",
                "jwt.issuer" to "http://0.0.0.0:8080/",
                "jwt.secret" to "secretTest"
            )
        }
        application.module()
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Bob-member", password = "the_builder"))
        }
        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Lidl", password = "paper01"))
        }

        val bobResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Bob-member"))
        }
        val lidlResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Lidl"))
        }

        val bobUser = bobResponse.body<List<User>>().first()
        val lidlUser = lidlResponse.body<List<User>>().first()

        client.post("/createGroup") {
            contentType(ContentType.Application.Json)
            setBody(GroupCreateReceive(currentUserId = bobUser.id, otherUserId = lidlUser.id))
        }

        val bobWithGroupResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Bob-member"))
        }
        val lidlUpdatedResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Lidl"))
        }

        assertEquals(
            bobWithGroupResponse.body<List<User>>().first().groupIds.first(),
            lidlUpdatedResponse.body<List<User>>().first().groupIds.first()
        )

        client.delete("/leaveGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupLeaveReceive(
                    leaveUserId = bobWithGroupResponse.body<List<User>>().first().id,
                    targetGroupId = bobWithGroupResponse.body<List<User>>().first().groupIds.first()
                )
            )
        }

        val bobLeftGroupResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Bob-member"))
        }
        assertEquals(
            bobLeftGroupResponse.body<List<User>>().first().groupIds,
            emptyList()
        )

        val lidlStillInGroupResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Lidl"))
        }
        assertEquals(
            lidlStillInGroupResponse.body<List<User>>().first().groupIds.size,
            1
        )

        client.delete("/leaveGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupLeaveReceive(
                    leaveUserId = lidlStillInGroupResponse.body<List<User>>().first().id,
                    targetGroupId = lidlStillInGroupResponse.body<List<User>>().first().groupIds.first()
                )
            )
        }

        val lidlLeftGroupResponse = client.get("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Lidl"))
        }

        assertEquals(
            lidlLeftGroupResponse.body<List<User>>().first().groupIds,
            emptyList()
        )

        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = bobLeftGroupResponse.body<List<User>>().first().id))
        }
        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = lidlLeftGroupResponse.body<List<User>>().first().id))
        }
    }
}