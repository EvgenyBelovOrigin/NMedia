package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow<Token?>(Token(0, null))


    init {
        val id = prefs.getLong(ID_KEY, 0)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id == 0L || token == null) {
            prefs.edit { clear() }
        } else {
            _authState.value = Token(id, token)
        }
        sendPushToken()
    }

    val authState: StateFlow<Token?> = _authState.asStateFlow()


    @Synchronized
    fun setAuth(token: Token) {
        with(prefs.edit()) {
            putLong(ID_KEY, token.id)
            putString(TOKEN_KEY, token.token)
            apply()
        }
        _authState.value = token
        sendPushToken()
    }

    @Synchronized
    fun clear() {
        with(prefs.edit()) {
            clear()
            commit()
        }
        _authState.value = Token(0, null)
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                Api.service.save(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val ID_KEY = "ID_KEY"

        @Volatile
        private var INSTANCE: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            INSTANCE ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initApp(context: Context): AppAuth = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildAuth(context).also { INSTANCE = it }
        }

        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
    }

}
