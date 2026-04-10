import hu.tb.domain.receive.GroupCreateReceive
import hu.tb.domain.receive.GroupLeaveReceive
import hu.tb.module
import hu.tb.domain.receive.UserReceive
import hu.tb.domain.send.User
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
                "build.isDeveloperMode" to "true"
            )
        }
        application.module()

        val response = client.get("/ping")
        assertEquals("pong", response.bodyAsText())
    }

    @Test
    fun `test user create-get-delete`() {
        testApplication {
            environment {
                config = MapApplicationConfig(
                    "build.isDeveloperMode" to "true"
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
                setBody(UserReceive(name = "John-Tester", password = "abc-123"))
            }
            client.post("/createUser") {
                contentType(ContentType.Application.Json)
                setBody(UserReceive("Michel-Tester", password = "ice-cream"))
            }

            val searchedJohn = client.post("/searchUserByNameAndPw") {
                contentType(ContentType.Application.Json)
                setBody(UserReceive(name = "John-Tester", password = "abc-123"))
            }
            assertEquals("John-Tester", searchedJohn.body<User>().name)
            assertEquals("abc-123", searchedJohn.body<User>().password)

            val emptyUser = client.delete("/deleteUser")
            assertEquals(HttpStatusCode.NotFound, emptyUser.status)
            assertEquals("No userId provided", emptyUser.bodyAsText())

            val johnDelete = client.delete("/deleteUser") {
                parameter("userId", searchedJohn.body<User>().id)
            }
            assertEquals(
                "User with ${searchedJohn.body<User>().id} id deleted",
                johnDelete.bodyAsText()
            )

            val searchedMichel = client.post("/searchUserByNameAndPw") {
                contentType(ContentType.Application.Json)
                setBody(UserReceive("Michel-Tester", password = "ice-cream"))
            }
            val michelDelete = client.delete("/deleteUser") {
                parameter("userId", searchedMichel.body<User>().id)
            }
            assertEquals(HttpStatusCode.OK, michelDelete.status)
        }
    }

    @Test
    fun `test group create-delete`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "build.isDeveloperMode" to "true"
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
            setBody(UserReceive(name = "Bob-member", password = "the_builder"))
        }
        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserReceive(name = "Lidl", password = "paper01"))
        }

        val bobResponse = client.get("/searchUsersName") { parameter("searchName", "Bob-member") }
        val lidlResponse = client.get("/searchUsersName") { parameter("searchName", "Lidl") }

        val bobUser = bobResponse.body<List<User>>().first()
        val lidlUser = lidlResponse.body<List<User>>().first()

        client.post("/createGroup") {
            contentType(ContentType.Application.Json)
            setBody(GroupCreateReceive(currentUserId = bobUser.id, otherUserId = lidlUser.id))
        }

        val bobWithGroupResponse = client.get("/searchUsersName") { parameter("searchName", "Bob-member") }
        val lidlUpdatedResponse = client.get("/searchUsersName") { parameter("searchName", "Lidl") }

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

        val bobLeftGroupResponse = client.get("/searchUsersName") { parameter("searchName", "Bob-member") }
        assertEquals(
            bobLeftGroupResponse.body<List<User>>().first().groupIds,
            emptyList()
        )

        val lidlStillInGroupResponse = client.get("/searchUsersName") { parameter("searchName", "Lidl") }
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

        val lidlLeftGroupResponse = client.get("/searchUsersName") { parameter("searchName", "Lidl") }

        assertEquals(
            lidlLeftGroupResponse.body<List<User>>().first().groupIds,
            emptyList()
        )

        client.delete("/deleteUser") { parameter("userId", bobLeftGroupResponse.body<List<User>>().first().id) }
        client.delete("/deleteUser") { parameter("userId", lidlLeftGroupResponse.body<List<User>>().first().id) }
    }
}