package hu.tb.datasource.data.repository

import hu.tb.datasource.data.model.*
import hu.tb.repository.ChatRepository
import hu.tb.repository.domain.send.Group
import hu.tb.repository.domain.send.Message
import hu.tb.repository.domain.send.User
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

typealias UserId = Long
typealias MessageId = Long

private const val MESSAGE_PAGE_LIMIT = 10

class ChatRepositoryImpl : ChatRepository {

    init {
        transaction {
            SchemaUtils.create(
                UserTable,
                MessageTable,
                GroupTable,
                GroupJunctionTable
            )
        }
    }

    override fun createNewUser(
        username: String,
        userPassword: String
    ): UserId = transactionLogger {
        SchemaUtils.listTables()
        UserEntity.new {
            name = username
            password = userPassword
        }
    }.id.value

    override fun getUserById(userId: Long): User? = transactionLogger {
        UserEntity.findById(userId)
    }?.toDomain()

    override fun getUserByNameAndPw(searchedName: String, searchedPw: String): List<User> = transactionLogger {
        UserEntity.find { UserTable.name eq searchedName and (UserTable.password eq searchedPw) }
    }.map { it.toDomain() }

    override fun createNewGroup(
        currentUser: User,
        otherUser: User
    ): Group? = transactionLogger {
        val currentUserEntity = UserEntity.findById(currentUser.id)
        val otherUserEntity = UserEntity.findById(otherUser.id)

        if (currentUserEntity != null && otherUserEntity != null) {
            return@transactionLogger GroupEntity.new {
                name = currentUser.name + " " + otherUser.name
                users = SizedCollection(listOf(currentUserEntity, otherUserEntity))
            }
        } else null
    }?.toDomain()

    override fun getGroupById(groupId: Long): Group? = transactionLogger {
        GroupEntity.findById(groupId)
    }?.toDomain()

    override fun createMessage(message: Message): MessageId? = transactionLogger {
        val senderEntity = UserEntity.findById(message.senderId)
        val groupEntity = GroupEntity.findById(message.groupId)

        if (senderEntity != null && groupEntity != null) {
            MessageEntity.new {
                content = message.content
                timeStamp = message.timestamp
                sender = senderEntity
                group = groupEntity
            }
        } else null
    }?.id?.value

    override fun getMessageHistory(
        groupId: Long,
        offset: Long
    ): List<Message> = transactionLogger {
        MessageEntity.find { MessageTable.group eq groupId }
            .orderBy(MessageTable.timeStamp to SortOrder.DESC)
            .limit(MESSAGE_PAGE_LIMIT)
            .offset(offset)
            .toList()
    }.map { it.toDomain() }

    override fun deleteMessage(messageId: Long) = transaction {
        MessageEntity.findById(1)?.delete()
    }

    override fun leftGroup(
        userId: Long,
        groupId: Long
    ) = transactionLogger {
        val userEntity = UserEntity.findById(userId)
        val groupEntity = GroupEntity.findById(groupId)
        if (userEntity != null && groupEntity != null) {
            groupEntity.users = SizedCollection(groupEntity.users.toList() - userEntity)
        }
        if (groupEntity?.users?.count() == 0L) {
            MessageEntity.find { MessageTable.group eq groupId }.toList().forEach {
                it.delete()
            }
            groupEntity.delete()
        }
    }

    override fun deleteUser(userId: Long) = transactionLogger {
        UserEntity.findById(userId)?.delete()
    }

    private fun GroupEntity.toDomain() =
        Group(
            id = this.id.value,
            name = this.name,
            users = this.users.map { it.toDomain() }
        )

    private fun UserEntity.toDomain() =
        User(
            id = this.id.value,
            name = this.name,
            password = this.password,
            groupIds = if (this.groups == null) emptyList() else this.groups.map { it.id.value }
        )

    private fun MessageEntity.toDomain() =
        Message(
            content = this.content,
            timestamp = this.timeStamp,
            senderId = this.sender.id.value,
            groupId = this.group.id.value
        )
}