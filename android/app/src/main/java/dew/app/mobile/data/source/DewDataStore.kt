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
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val emailKey = stringPreferencesKey("email")
    private val nameKey = stringPreferencesKey("name")

    override suspend fun saveAuth(auth: Auth) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = auth.userId
            preferences[accessTokenKey] = auth.accessToken
            preferences[emailKey] = auth.email
            preferences[nameKey] = auth.name
        }
    }
    
    override suspend fun getAuth(): Auth? {
        try {
            val auth: Auth? = context.dataStore.data.map { preferences ->
                if (
                    preferences[userIdKey] == null
                ) {
                    null
                } else {
                    Auth(
                        preferences[userIdKey] ?: "",
                        preferences[accessTokenKey] ?: "",
                        preferences[emailKey] ?: "",
                        preferences[nameKey] ?: ""
                    )
                }
            }.firstOrNull()
            return auth
        } catch (e: Exception) {
            println("DewDatastoreImpl Dew getAuth Error: ${e.message}")
            return null
        }
    }
}