package hu.tb.datasource.data.repository

import hu.tb.datasource.data.model.*
import hu.tb.domain.send.DecodeGroup
import hu.tb.domain.send.Group
import hu.tb.domain.send.Message
import hu.tb.domain.send.User
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

typealias UserId = Long
typealias MessageId = Long

private const val MESSAGE_PAGE_LIMIT = 10

class ChatRepository {

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

    fun createNewUser(
        username: String,
        userPassword: String
    ): UserId = transactionLogger {
        UserEntity.new {
            name = username
            password = userPassword
        }
    }.id.value

    fun getUserById(userId: Long): User? = transactionLogger {
        UserEntity.findById(userId)
    }?.toDomain()

    fun getUserByNameAndPw(searchedName: String, searchedPw: String): User? = transactionLogger {
        UserEntity.all()
            .find { it.name == searchedName && it.password == searchedPw }
            ?.toDomain()
    }

    fun getUserByName(searchedName: String): List<User> = transactionLogger {
        UserEntity.find { UserTable.name eq searchedName }.map { it.toDomain() }
    }

    fun createNewGroup(
        currentUserId: Long,
        otherUserId: Long
    ): Group? = transactionLogger {
        val currentUserEntity = UserEntity.findById(currentUserId)
        val otherUserEntity = UserEntity.findById(otherUserId)

        if (currentUserEntity != null && otherUserEntity != null) {
            val newGroup = GroupEntity.new { name = "$currentUserId-$otherUserId" }
            newGroup.users = SizedCollection(listOf(currentUserEntity, otherUserEntity))
            newGroup.toDomain()
        } else null
    }

    fun decodeGroupIds(groupIds: List<Long>, userId: Long): List<DecodeGroup> = transactionLogger {
        val decodedGroups = mutableListOf<DecodeGroup>()
        groupIds.forEach { id ->
            val group = GroupEntity.findById(id)
            val otherUsers = group?.users?.filter { it.id.value != userId }
            if (otherUsers != null) {
                val decodeGroup = if (otherUsers.size > 1) {
                    DecodeGroup.Complex(groupId = group.id.value, memberNames = otherUsers.map { it.name })
                } else {
                    DecodeGroup.Simple(groupId = group.id.value, otherUserName = otherUsers.first().name)
                }
                decodedGroups.add(decodeGroup)
            }
        }
        decodedGroups.toList()
    }

    fun createMessage(message: Message): MessageId? = transactionLogger {
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

    fun getMessageHistory(
        groupId: Long,
        offset: Long
    ): List<Message> = transactionLogger {
        MessageEntity.find { MessageTable.group eq groupId }
            .orderBy(MessageTable.timeStamp to SortOrder.ASC)
            .limit(MESSAGE_PAGE_LIMIT)
            .offset(offset)
            .toList()
            .map { it.toDomain() }
    }

    fun deleteMessage(messageId: Long) = transaction {
        MessageEntity.findById(messageId)?.delete()
    }

    fun leaveGroup(
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

    fun deleteUser(userId: Long) = transactionLogger {
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
            id = this.id.value,
            content = this.content,
            timestamp = this.timeStamp,
            senderId = this.sender.id.value,
            groupId = this.group.id.value
        )
}