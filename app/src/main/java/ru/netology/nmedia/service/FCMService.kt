package ru.netology.nmedia.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()


    override fun onMessageReceived(message: RemoteMessage) {
        println(gson.toJson(message))
    }

    override fun onNewToken(token: String) {
        println("token $token")
    }
}