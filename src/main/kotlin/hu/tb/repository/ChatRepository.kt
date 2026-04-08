package hu.tb.repository

import hu.tb.datasource.data.repository.MessageId
import hu.tb.datasource.data.repository.UserId
import hu.tb.repository.domain.send.Group
import hu.tb.repository.domain.send.Message
import hu.tb.repository.domain.send.User

interface ChatRepository {
    fun createNewUser(username: String, userPassword: String): UserId
    fun getUserById(userId: Long): User?
    fun getUserByNameAndPw(searchedName: String, searchedPw: String): List<User>
    fun createNewGroup(currentUser: User, otherUser: User): Group?
    fun getGroupById(groupId: Long): Group?
    fun createMessage(message: Message): MessageId?
    fun getMessageHistory(groupId: Long, offset: Long = 1): List<Message>
    fun deleteMessage(messageId: Long): Unit?
    fun leftGroup(userId: Long, groupId: Long)
    fun deleteUser(userId: Long): Unit?
}