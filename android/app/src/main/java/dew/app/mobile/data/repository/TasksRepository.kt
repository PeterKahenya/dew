package dew.app.mobile.data.repository

import dew.app.mobile.data.model.TaskCreate
import dew.app.mobile.data.model.TaskUpdate
import dew.app.mobile.data.model.toDbTask
import dew.app.mobile.data.source.DbTask
import dew.app.mobile.data.source.DewApi
import dew.app.mobile.data.source.TasksDao
import javax.inject.Inject

interface TasksRepository {
    suspend fun createTask(taskCreate: TaskCreate): DbTask
    suspend fun updateTask(taskId: String, taskUpdate: TaskUpdate): DbTask
    suspend fun deleteTask(taskId: String)
    suspend fun filterTasks(): List<DbTask>
}

class TasksRepositoryImpl @Inject constructor(
    private val api: DewApi,
    private val tasksDao: TasksDao,
    private val authRepository: AuthRepository
) : TasksRepository {
    override suspend fun createTask(taskCreate: TaskCreate): DbTask {
        val auth = authRepository.auth()
        if (auth == null) {
            throw Exception("Not authenticated")
        } else {
            val apiTask = api.createTask(auth.userId, taskCreate)
            tasksDao.insert(apiTask.toDbTask())
            return tasksDao.getById(apiTask.id) ?: throw Exception("Task not found")
        }
    }

    override suspend fun updateTask(taskId: String, taskUpdate: TaskUpdate): DbTask {
        val auth = authRepository.auth()
        if (auth == null) {
            throw Exception("Not authenticated")
        } else {
            val apiTask = api.updateTask(auth.userId, taskId, taskUpdate)
            var dbTask = tasksDao.getById(taskId)
            if (dbTask == null) {
                tasksDao.insert(apiTask.toDbTask())
                dbTask = tasksDao.getById(apiTask.id)
            } else {
                tasksDao.update(apiTask.toDbTask())
                dbTask = tasksDao.getById(apiTask.id)
            }
            return dbTask ?: throw Exception("Task not found")
        }
    }

    override suspend fun deleteTask(taskId: String) {
        val auth = authRepository.auth()
        if (auth == null) {
            throw Exception("Not authenticated")
        } else {
            api.deleteTask(auth.userId, taskId)
            if (tasksDao.getById(taskId) != null) {
                tasksDao.delete(taskId)
            }
        }
    }

    override suspend fun filterTasks(): List<DbTask> {
        val auth = authRepository.auth()
        if (auth == null) {
            throw Exception("Not authenticated")
        } else {
            val apiTasks = api.filterTasks()
            for (task in apiTasks) {
                if (tasksDao.getById(task.id) == null) {
                    tasksDao.insert(task.toDbTask())
                } else {
                    tasksDao.update(task.toDbTask())
                }
            }
            return tasksDao.getAll()
        }
    }

}