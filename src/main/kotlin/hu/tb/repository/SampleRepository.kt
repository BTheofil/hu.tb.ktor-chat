package hu.tb.repository

import hu.tb.repository.domain.send.User

interface SampleRepository {
    fun insertUser()
    fun getUserById(userId: Long): User?
}