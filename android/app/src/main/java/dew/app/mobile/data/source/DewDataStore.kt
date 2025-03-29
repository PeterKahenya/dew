package dew.app.mobile.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import dew.app.mobile.data.model.Auth
import androidx.datastore.preferences.core.stringPreferencesKey
import dew.app.mobile.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

interface DewDataStore{
    suspend fun saveAuth(auth: Auth)
    suspend fun getAuth(): Auth?
}

class DewDatastoreImpl(private val context: Context): DewDataStore{

    private val userIdKey = stringPreferencesKey("user_id")

    override suspend fun saveAuth(auth: Auth) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = auth.userId
        }
    }
    
    override suspend fun getAuth(): Auth? {
        try {
            val auth: Auth? = context.dataStore.data.map { preferences ->
                println("getAuth: $preferences")
                if (
                    preferences[userIdKey] == null
                ) {
                    null
                } else {
                    Auth(
                        preferences[userIdKey] ?: "",
                    )
                }
            }.firstOrNull()
            return auth
        } catch (e: Exception) {
            println("Dew getAuth Error: $e")
            return null
        }
    }
}