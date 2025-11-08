package hu.tb.datasource.sample

import hu.tb.datasource.sample.model.UserSender

interface SampleRepository {
    suspend fun getUsers(): List<UserSender>
}
