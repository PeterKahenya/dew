package dew.app.mobile.data.source

import dew.app.mobile.data.model.Chat
import dew.app.mobile.data.model.Task
import dew.app.mobile.data.model.TaskCreate
import dew.app.mobile.data.model.TaskUpdate
import dew.app.mobile.data.model.User
import dew.app.mobile.data.model.UserLogin
import dew.app.mobile.data.model.UserLoginResponse
import dew.app.mobile.data.model.UserSignup
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DewApi{

    @POST("signup")
    suspend fun signup(@Body user: UserSignup): User

    @POST("login")
    suspend fun login(@Body user: UserLogin): UserLoginResponse

    @GET("me")
    suspend fun profile(@Header("Authorization") accessToken: String): User

    @POST("logout")
    suspend fun logout(@Header("Authorization") accessToken: String)

    @POST("{userId}/tasks")
    suspend fun createTask(@Header("Authorization") accessToken: String, @Path("userId") userId:String, @Body task: TaskCreate): Task

    @PUT("{userId}/tasks/{taskId}")
    suspend fun updateTask(@Header("Authorization") accessToken: String, @Path("userId") userId:String, @Path("taskId") taskId:String, @Body task: TaskUpdate): Task

    @DELETE("{userId}/tasks/{taskId}")
    suspend fun deleteTask(@Header("Authorization") accessToken: String, @Path("userId") userId:String, @Path("taskId") taskId:String,)

    @GET("{userId}/tasks")
    suspend fun filterTasks(@Header("Authorization") accessToken: String,): List<Task>

    @POST("{userId}/chat")
    suspend fun chat(@Header("Authorization") accessToken: String, @Path("userId") userId: String, @Body chat: Chat): Chat
}