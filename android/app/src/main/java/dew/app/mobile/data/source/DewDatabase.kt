package dew.app.mobile.data.source

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity
data class DbTask(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "is_completed")
    val isComplete: Boolean = false,
    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)

@Dao
interface TasksDao{
    @Query("SELECT * FROM dbtask")
    suspend fun getAll(): List<DbTask>

    @Query("SELECT * FROM dbtask WHERE id IN (:ids)")
    suspend fun getAllByIds(ids: List<String>): List<DbTask>

    @Query("SELECT * FROM dbtask WHERE id = :id")
    suspend fun getById(id: String): DbTask?

    @Insert
    suspend fun insert(task: DbTask)

    @Update
    suspend fun update(task: DbTask)

    @Query("DELETE FROM dbtask WHERE id = :id")
    suspend fun delete(id: String)
}

@Database(entities = [DbTask::class], version = 1)
abstract class DewDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}