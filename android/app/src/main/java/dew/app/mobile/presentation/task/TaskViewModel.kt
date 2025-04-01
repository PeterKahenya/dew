package dew.app.mobile.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dew.app.mobile.data.model.TaskCreate
import dew.app.mobile.data.model.TaskFilters
import dew.app.mobile.data.model.TaskUpdate
import dew.app.mobile.data.model.toQueryMap
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

data class TaskState(
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val task: DbTask? = null,
    val error: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                val task: DbTask = tasksRepository.getTask(taskId)
                _state.update {
                    it.copy(task = task, isLoading = false, error = null)
                }
            } catch (e: HttpException) {
                val error = "Task Create HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(task = null, isLoading = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Task Create IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(task = null, isLoading = false, error = error)
                }
            } catch (e: SecurityException) {
                val error =
                    "Task Create SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(task = null, isLoading = false, error = error)
                }
            }
        }
    }

    fun createTask(taskCreate: TaskCreate) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isCreated = false)
                }
                println("TaskCreate: $taskCreate")
                val task: DbTask = tasksRepository.createTask(taskCreate)
                println("TaskResponse: $task")
                _state.update {
                    it.copy(task = task, isCreated = true, error = null)
                }
            } catch (e: HttpException) {
                val error = "Task Create HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(task = null, isCreated = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Task Create IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(task = null, isCreated = false, error = error)
                }
            } catch (e: SecurityException) {
                val error =
                    "Task Create SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(task = null, isLoading = false, error = error)
                }
            }
        }
    }

    fun updateTask(taskId: String, taskUpdate: TaskUpdate) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isUpdated = false)
                }
                val task: DbTask = tasksRepository.updateTask(taskId, taskUpdate)
                _state.update { it ->
                    it.copy(
                        task = task, isUpdated = true, error = null
                    )
                }
            } catch (e: HttpException) {
                val error = "Task Update HttpException: ${e.response()} ${e.localizedMessage}"
                println(error)
                _state.update {
                    it.copy(task = null, isUpdated = false, error = error)
                }
            } catch (e: IOException) {
                val error =
                    "Task Update IOException: ${e.localizedMessage ?: "Couldn't reach server. Check your internet connection"}"
                println(error)
                _state.update {
                    it.copy(task = null, isUpdated = false, error = error)
                }
            } catch (e: SecurityException) {
                val error =
                    "Task Update SecurityException: ${e.localizedMessage ?: "Unexpected error"}"
                println(error)
                _state.update {
                    it.copy(task = null, isUpdated = false, error = error)
                }
            }
        }
    }
}
