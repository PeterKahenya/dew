package dew.app.mobile.data.repository

import dew.app.mobile.data.model.Chat
import dew.app.mobile.data.source.DewApi

interface ChatRepository {
    suspend fun chat(chat: Chat): Chat
}

class ChatRepositoryImpl(
    private val api: DewApi, private val authRepository: AuthRepository
) : ChatRepository {
    override suspend fun chat(chat: Chat): Chat {
        val auth = authRepository.auth()
        if (auth == null) {
            throw Exception("Not authenticated")
        } else {
            return api.chat("Bearer ${auth.accessToken}", auth.userId, chat)
        }
    }
}