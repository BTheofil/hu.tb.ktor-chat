import hu.tb.module
import hu.tb.repository.domain.receive.UserReceive
import hu.tb.repository.domain.send.User
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

            val searchedUser = client.post("/searchUserByNameAndPw") {
                contentType(ContentType.Application.Json)
                setBody(UserReceive(name = "John-Tester", password = "abc-123"))
            }
            assertEquals("John-Tester", searchedUser.body<User>().name)
            assertEquals("abc-123", searchedUser.body<User>().password)

            val emptyUser = client.get("/deleteUser")
            assertEquals(HttpStatusCode.NotFound, emptyUser.status)
            assertEquals("No userId provided", emptyUser.bodyAsText())

            val johnDelete = client.get("/deleteUser") {
                parameter("userId", 1)
            }
            assertEquals("User with 1 id deleted", johnDelete.bodyAsText())
            val michelDelete = client.get("/deleteUser") {
                parameter("userId", 2)
            }
            assertEquals(HttpStatusCode.OK, michelDelete.status)
        }
    }
}