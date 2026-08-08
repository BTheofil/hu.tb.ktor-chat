import com.auth0.jwt.JWT
import hu.tb.domain.receive.*
import hu.tb.domain.send.DecodeGroup
import hu.tb.domain.send.Group
import hu.tb.domain.send.Message
import hu.tb.domain.send.User
import hu.tb.domain.send.UserCreated
import hu.tb.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.seconds

class RoutingTest {

    @Test
    fun `ping server test`() = testApplication {
        setupEnvironment()

        val response = client.get("/ping")
        assertEquals("pong", response.bodyAsText())
    }

    @Test
    fun `test user create-get-delete`() = testApplication {
        setupEnvironment()
        client = createClient {
            install(ContentNegotiation) { json() }
        }

        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateReceive(name = "John-Tester", password = "abc-123"))
        }
        client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateReceive("Michel-Tester", password = "ice-cream"))
        }

        val searchedJohn = client.post("/searchUserByNameAndPw") {
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

        val searchedMichel = client.post("/searchUserByNameAndPw") {
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
        setupEnvironment()
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

        val bobResponse = client.post("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Bob-member"))
        }
        val lidlResponse = client.post("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Lidl"))
        }

        val bobUser = bobResponse.body<List<User>>().first()
        val lidlUser = lidlResponse.body<List<User>>().first()

        client.post("/createGroup") {
            contentType(ContentType.Application.Json)
            setBody(GroupCreateReceive(currentUserId = bobUser.id, otherUserId = lidlUser.id))
        }

        val bobWithGroupResponse = client.post("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Bob-member"))
        }
        val lidlUpdatedResponse = client.post("/searchUserByName") {
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

        val bobLeftGroupResponse = client.post("/searchUserByName") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByName(name = "Bob-member"))
        }
        assertEquals(
            bobLeftGroupResponse.body<List<User>>().first().groupIds,
            emptyList()
        )

        val lidlStillInGroupResponse = client.post("/searchUserByName") {
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
                    targetGroupId = lidlStillInGroupResponse.body<List<User>>()
                        .first().groupIds.first()
                )
            )
        }

        val lidlLeftGroupResponse = client.post("/searchUserByName") {
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

    @Test
    fun `test group decode`() = testApplication {
        setupEnvironment()
        client = createClient {
            install(ContentNegotiation) { json() }
        }

        val kodeeResponse = client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Kodee", password = "iLoveKotlin"))
        }
        val jetbrainsResponse = client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Jetbrains", password = "iLove"))
        }

        val kodeeUser = kodeeResponse.body<UserCreated>()
        val jetbrainsUser = jetbrainsResponse.body<UserCreated>()

        val groupResponse = client.post("/createGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupCreateReceive(
                    currentUserId = kodeeUser.userId,
                    otherUserId = jetbrainsUser.userId
                )
            )
        }
        val kodeeDetailResponse = client.post("/searchUserByNameAndPw") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Kodee", password = "iLoveKotlin"))
        }
        val kodeeDetailUser = kodeeDetailResponse.body<User>()

        val decodeResponse = client.post("/decodeGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupDecodeReceive(
                    userId = kodeeDetailUser.id,
                    groupIds = kodeeDetailUser.groupIds
                )
            )
        }
        val decodedData = decodeResponse.body<List<DecodeGroup>>()
        assertEquals(
            "Jetbrains",
            (decodedData.first() as DecodeGroup.Simple).otherUserName
        )

        //cleanup
        client.delete("/leaveGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupLeaveReceive(
                    leaveUserId = kodeeDetailUser.id,
                    targetGroupId = groupResponse.body<Group>().id,
                )
            )
        }
        client.delete("/leaveGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupLeaveReceive(
                    leaveUserId = jetbrainsUser.userId,
                    targetGroupId = groupResponse.body<Group>().id,
                )
            )
        }
        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(
                UserDeleteReceive(
                    userId = kodeeDetailUser.id
                )
            )
        }
        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(
                UserDeleteReceive(
                    userId = jetbrainsUser.userId
                )
            )
        }
    }

    @Test
    fun `test message`() = testApplication {
        setupEnvironment()
        client = createClient {
            install(ContentNegotiation) { json() }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }

        val aliceData = client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Alice", password = "apple"))
        }
        val evelinData = client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserSearchReceive.ByTarget(name = "Evelin", password = "lemon"))
        }

        val aliceUser = aliceData.body<UserCreated>()
        val evelinUser = evelinData.body<UserCreated>()

        val groupData = client.post("/createGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupCreateReceive(
                    currentUserId = aliceUser.userId,
                    otherUserId = evelinUser.userId
                )
            )
        }
        val targetGroupId = groupData.body<Group>().id

        runTest {
            val evelinReady = CompletableDeferred<Unit>()
            val evelin = launch {
                client.webSocket(
                    urlString = "/groupConnect",
                    request = {
                        header(HttpHeaders.Authorization, "Bearer ${evelinUser.token}")
                        parameter("targetGroupId", targetGroupId)
                    }
                ) {
                    evelinReady.complete(Unit)
                    assertEquals("Hello", receiveDeserialized<Message>().content)
                    send(Frame.Text("Hi Alice, it's Evelin here"))
                    receiveDeserialized<Message>()
                    close()
                }
            }

            val alice = launch {
                evelinReady.await()

                client.webSocket(
                    urlString = "/groupConnect",
                    request = {
                        header(HttpHeaders.Authorization, "Bearer ${aliceUser.token}")
                        parameter("targetGroupId", targetGroupId)
                    }
                ) {
                    send(Frame.Text("Hello"))
                    receiveDeserialized<Message>()
                    assertEquals(
                        "Hi Alice, it's Evelin here",
                        receiveDeserialized<Message>().content
                    )
                   close()
                }
            }

            alice.join()
            evelin.join()
        }

        val messageHistory = client.post("/groupHistory") {
            contentType(ContentType.Application.Json)
            setBody(MessageHistoryReceive(groupId = targetGroupId))
        }

        assertEquals(2, messageHistory.body<List<Message>>().size)
        // History is newest first, so Alice's opening message is the last entry.
        messageHistory.body<List<Message>>().last().also {
            assertEquals("Hello", it.content)
            assertEquals(aliceUser.userId, it.senderId)
        }

        val deleteData = client.delete("/deleteMessage") {
            contentType(ContentType.Application.Json)
            setBody(
                MessageDeleteReceive(
                    messageId = messageHistory.body<List<Message>>().last().id!!
                )
            )
        }
        assertEquals(HttpStatusCode.OK, deleteData.status)

        val shorterMessageHistory = client.post("/groupHistory") {
            contentType(ContentType.Application.Json)
            setBody(MessageHistoryReceive(groupId = targetGroupId))
        }
        assertEquals(1, shorterMessageHistory.body<List<Message>>().size)

        client.delete("/deleteMessage") {
            contentType(ContentType.Application.Json)
            setBody(
                MessageDeleteReceive(
                    messageId = shorterMessageHistory.body<List<Message>>().first().id!!
                )
            )
        }
        client.delete("/leaveGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupLeaveReceive(
                    leaveUserId = aliceUser.userId,
                    targetGroupId = targetGroupId
                )
            )
        }
        client.delete("/leaveGroup") {
            contentType(ContentType.Application.Json)
            setBody(
                GroupLeaveReceive(
                    leaveUserId = evelinUser.userId,
                    targetGroupId = targetGroupId
                )
            )
        }
        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = aliceUser.userId))
        }
        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = evelinUser.userId))
        }
    }

    @Test
    fun `test token`() = testApplication {
        setupEnvironment()
        client = createClient {
            install(ContentNegotiation) { json() }
        }

        val createData = client.post("/createUser") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateReceive(name = "Peter", password = "Freedom"))
        }

        assertEquals(Long::class, createData.body<UserCreated>().userId::class)
        assertEquals(
            LocalDateTime.now().plusDays(5).toLocalDate(),
            JWT.decode(createData.body<UserCreated>().token).expiresAt
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        )

        delay(1.seconds)

        val newToken = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateReceive(name = "Peter", password = "Freedom"))
        }

        assertNotEquals(
            JWT.decode(createData.body<UserCreated>().token).expiresAt,
            JWT.decode(newToken.body<String>()).expiresAt
        )

        client.delete("/deleteUser") {
            contentType(ContentType.Application.Json)
            setBody(UserDeleteReceive(userId = createData.body<UserCreated>().userId))
        }
    }

    private fun ApplicationTestBuilder.setupEnvironment() {
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
    }
}