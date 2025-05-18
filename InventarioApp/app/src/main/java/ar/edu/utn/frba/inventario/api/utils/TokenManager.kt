package ar.edu.utn.frba.inventario.api.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "inventar.io_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    fun saveAccessToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, token)
            apply() // Asynchronous save
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearAccessToken() {
        with(sharedPreferences.edit()) {
            remove(KEY_ACCESS_TOKEN)
            apply()
        }
    }

    fun saveRefreshToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_REFRESH_TOKEN, token)
            apply()
        }
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearRefreshToken() {
        with(sharedPreferences.edit()) {
            remove(KEY_REFRESH_TOKEN)
            apply()
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun clearAllTokens() {
        with(sharedPreferences.edit()) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            apply()
        }
    }

    fun hasAccessToken(): Boolean {
        return getAccessToken() != null
    }

    fun hasRefreshToken(): Boolean {
        return getRefreshToken() != null
    }
}