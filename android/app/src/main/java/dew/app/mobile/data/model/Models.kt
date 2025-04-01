package dew.app.mobile.data.model

import com.google.gson.annotations.SerializedName
import dew.app.mobile.data.source.DbTask

data class User(
    val id: String,
    val name: String,
    val email: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class UserSignup(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String
)

data class UserLogin(
    val email: String,
    val password: String
)

data class UserLoginResponse(
    val message: String,
    @SerializedName("access_token")
    val accessToken: String
)

data class Task(
    val id: String,
    val title: String,
    val description: String,
    @SerializedName("is_completed")
    val isCompleted: Boolean,
    @SerializedName("completed_at")
    val completedAt: String?,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)

fun Task.toDbTask(): DbTask {
    return DbTask(
        id = this.id,
        title = this.title,
        description = this.description,
        isComplete = this.isCompleted,
        completedAt = this.completedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt ?: ""
    )
}

data class TaskCreate(
    val title: String,
    val description: String,
    @SerializedName("is_completed")
    val isCompleted: Boolean?,
    @SerializedName("completed_at")
    val completedAt: String?
)

data class TaskUpdate(
    val title: String?,
    val description: String?,
    @SerializedName("is_completed")
    val isCompleted: Boolean?,
    @SerializedName("completed_at")
    val completedAt: String?
)

fun TaskCreate.toTaskUpdate(): TaskUpdate {
    return TaskUpdate(
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted,
        completedAt = this.completedAt
    )
}

data class Chat(
    val role: String,
    val content: String
)

data class Auth(
    val userId: String,
    @SerializedName("access_token")
    val accessToken: String,
    val email: String,
    val name: String
)

data class TaskFilters(
    val createdAt: String?,
    val completedAt: String?,
    val status: String?,
    val q: String,
    val sortBy: String = "created_at,asc"
)

fun TaskFilters.toQueryMap(): Map<String, String> {
    val queryMap = mutableMapOf<String, String>()
    if (this.createdAt != null) {
        queryMap["created_at__gte"] = this.createdAt
    }
    if (this.completedAt != null) {
        queryMap["completed_at__gte"] = this.completedAt
    }
    if (this.status != "all") {
        queryMap["is_complete"] = if (this.status == "complete") "true" else "false"
    }
    if (this.q.isNotEmpty()) {
        queryMap["q"] = this.q
    }

    return queryMap

}