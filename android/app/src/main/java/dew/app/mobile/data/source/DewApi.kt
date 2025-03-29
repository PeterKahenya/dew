package dew.app.mobile.data.source

import dew.app.mobile.data.model.Chat
import dew.app.mobile.data.model.TaskCreate
import dew.app.mobile.data.model.User
import dew.app.mobile.data.model.UserLogin
import dew.app.mobile.data.model.UserLoginResponse
import dew.app.mobile.data.model.UserSignup
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DewApi{

    @POST("signup")
    suspend fun signup(@Body user: UserSignup): User

    @POST("login")
    suspend fun login(@Body user: UserLogin): UserLoginResponse

    @GET("me")
    suspend fun profile()

    @POST("logout")
    suspend fun logout()

    @POST("{userId}/tasks")
    suspend fun createTask(@Path("userId") userId:String, @Body task: TaskCreate): User

    @PUT("{userId}/tasks/{taskId}")
    suspend fun updateTask(@Path("userId") userId:String, @Path("taskId") taskId:String, @Body task: TaskCreate): User

    @DELETE("{userId}/tasks/{taskId}")
    suspend fun deleteTask()

    @GET("{userId}/tasks")
    suspend fun filterTasks()

    @POST("{userId}/chat")
    suspend fun chat(@Path("userId") userId: String, @Body chat: Chat): Chat
}