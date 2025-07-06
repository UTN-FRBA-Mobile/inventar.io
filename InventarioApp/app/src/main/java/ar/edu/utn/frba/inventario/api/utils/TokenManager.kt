package ar.edu.utn.frba.inventario.api.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Singleton

@Singleton
class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "inventar.io_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    fun getAccessToken(): String? = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(REFRESH_TOKEN_KEY, null)

    fun saveSession(accessToken: String, refreshToken: String) {
        sharedPreferences.edit {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    fun clearSession() {
        sharedPreferences.edit {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            apply()
        }
    }

    fun hasSession(): Boolean = hasAccessToken() && hasRefreshToken()

    fun hasAccessToken(): Boolean = getAccessToken() != null

    fun hasRefreshToken(): Boolean = getRefreshToken() != null
}
