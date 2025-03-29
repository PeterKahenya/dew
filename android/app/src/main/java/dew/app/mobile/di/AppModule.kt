package dew.app.mobile.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dew.app.mobile.Constants
import dew.app.mobile.data.repository.AuthRepository
import dew.app.mobile.data.repository.AuthRepositoryImpl
import dew.app.mobile.data.repository.ChatRepository
import dew.app.mobile.data.repository.ChatRepositoryImpl
import dew.app.mobile.data.repository.TasksRepository
import dew.app.mobile.data.repository.TasksRepositoryImpl
import dew.app.mobile.data.source.DewApi
import dew.app.mobile.data.source.DewDataStore
import dew.app.mobile.data.source.DewDatabase
import dew.app.mobile.data.source.DewDatastoreImpl
import dew.app.mobile.data.source.TasksDao
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideDewApi(): DewApi {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDewDataStore(@ApplicationContext context: Context): DewDataStore {
        return DewDatastoreImpl(context)
    }

    @Provides
    @Singleton
    fun providesDewDataBase(@ApplicationContext context: Context): DewDatabase {
        return Room.databaseBuilder(context, DewDatabase::class.java, "dew_db").build()
    }

    @Provides
    @Singleton
    fun provideTasksDao(db: DewDatabase): TasksDao = db.tasksDao()

    @Provides
    @Singleton
    fun provideAuthRepository(api: DewApi, ds: DewDataStore): AuthRepository {
        return AuthRepositoryImpl(api,ds)
    }

    @Provides
    @Singleton
    fun provideTasksRepository(
        api: DewApi,
        tasksDao: TasksDao,
        authRepository: AuthRepository
    ): TasksRepository {
        return TasksRepositoryImpl(api,tasksDao,authRepository)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        api: DewApi,
        authRepository: AuthRepository
    ): ChatRepository {
        return ChatRepositoryImpl(api,authRepository)
    }

}