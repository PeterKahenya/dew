package dew.app.mobile.presentation.tasks

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
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class TasksState(
    val isLoading: Boolean = false, val tasks: List<DbTask> = emptyList(), val error: String? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    init {
        val filters = mutableMapOf("created_at__gte" to "2020-01-01T00:00:00.000Z")
        getTasks(filters)
    }

    fun getTasks(filters: Map<String, String>) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                val tasks: List<DbTask> = tasksRepository.filterTasks(filters)
                println("TasksResponse: ${tasks.size}")
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