package dew.app.mobile.data.model

import com.google.gson.annotations.SerializedName

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
    val password: String,
)

data class UserLoginResponse(
    val message: String
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

data class Chat(
    val role: String,
    val content: String
)