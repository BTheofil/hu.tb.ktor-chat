package hu.tb.datastore

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserDatastoreRepository(
    private val context: Context
) {

    fun counterFlow(): Flow<UserData> = context.userDataStore.data
}