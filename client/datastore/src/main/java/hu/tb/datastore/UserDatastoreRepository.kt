package hu.tb.datastore

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserDatastoreRepository(
    private val context: Context
) {

    fun userdataFlow(): Flow<UserData> = context.userDataStore.data

    suspend fun updateUserData(
        name: String? = null,
        password: String? = null,
        token: String? = null
    ) {
        context.userDataStore.updateData { userData ->
            userData.copy(
                name = name ?: userData.name,
                password = password ?: userData.password,
                token = token ?: userData.token
            )
        }
    }
}