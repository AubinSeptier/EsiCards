package com.aubinseptier.esicards

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.tokenStore by preferencesDataStore(name = "token")
class TokenStorage(private val context: Context) {
    private var tokenKey = stringPreferencesKey("token")

    suspend fun write(token: String){
        context.tokenStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun read(): String {
        val token = context.tokenStore.data.firstOrNull()?.get(tokenKey)
        return token ?: ""
    }
}