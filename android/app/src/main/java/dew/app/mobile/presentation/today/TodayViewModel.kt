package dew.app.mobile.presentation.today

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dew.app.mobile.data.repository.TasksRepository
import dew.app.mobile.data.source.DbTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import retrofit2.HttpException
import java.io.IOException
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TodayState(
    val isLoading: Boolean = false, val tasks: List<DbTask> = emptyList(), val error: String? = null
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TodayState())
    val state: StateFlow<TodayState> = _state.asStateFlow()

    init {
        getTasks()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTasks() {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                val midnightDateTime = LocalDateTime(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date, LocalTime(0, 0, 0,0))
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                val formattedMidnight = formatter.format(midnightDateTime.toJavaLocalDateTime().atOffset(ZoneOffset.UTC))
                val filters = mapOf("created_at__gte" to formattedMidnight)
                val tasks: List<DbTask> = tasksRepository.filterTasks(filters)

                _state.update {
                    it.copy(tasks = tasks, isLoading = false, error = null)
                }
            } catch (e: HttpException) {
                val error = "Tasks HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Tasks IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            } catch (e: SecurityException) {
                val error = "Tasks SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            }catch (e: Exception) {
                val error = "Tasks Exception: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                tasksRepository.deleteTask(taskId)
                _state.update {
                    it.copy(
                        tasks = it.tasks.filter { it.id != taskId },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: HttpException) {
                val error = "Task HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Task IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            } catch (e: SecurityException) {
                val error = "Task SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(tasks = emptyList(), isLoading = false, error = error)
                }
            }
        }
    }


}