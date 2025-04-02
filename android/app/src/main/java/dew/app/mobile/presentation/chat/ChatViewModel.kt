package dew.app.mobile.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dew.app.mobile.data.model.Chat
import dew.app.mobile.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ChatState(
    val isLoading: Boolean = false,
    val messages: List<Chat> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel(){
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()
    init {
        _state.update {
            it.copy(messages = it.messages
                    + Chat(role = "assistant", content = "Hello, how can I help you today?")
                    + Chat(role = "user", content = "Please summarize my pending \n tasks.")
            )
        }
    }

    fun chat(chat: Chat){
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true, messages = it.messages + chat)
                }
                val message: Chat = chatRepository.chat(chat)
                println("ChatResponse: $message")
                _state.update {
                    it.copy(messages = it.messages + message, isLoading = false, error = null)
                }
            } catch (e: HttpException){
                val error = "Chat HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(messages = emptyList(), isLoading = false, error = error)
                }
            } catch (e: IOException){
                val error = "Chat IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(messages = emptyList(), isLoading = false, error = error)
                }
            } catch (e: SecurityException){
                val error = "Chat SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(messages = emptyList(), isLoading = false, error = error)
                }
            }
        }
    }
}