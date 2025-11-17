package hu.tb.datasource.sample

interface SampleRepository {
    suspend fun countUsers(): Int
    fun addUser(): Int
}
