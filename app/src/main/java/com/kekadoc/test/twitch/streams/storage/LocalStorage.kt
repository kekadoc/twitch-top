package com.kekadoc.test.twitch.streams.storage

import android.content.Context
import android.util.Log
import androidx.room.*
import com.kekadoc.test.twitch.streams.model.TwitchTopResponseElement
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "database-games")
data class DatabaseUnit(@PrimaryKey(autoGenerate = false) val position: Int, @ColumnInfo(name = "data") val data: String)

@Database(entities = arrayOf(DatabaseUnit::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
}
@Dao
interface GamesDao {

    @Query("SELECT * FROM `database-games`")
    suspend fun getAll(): List<DatabaseUnit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<DatabaseUnit>)

    @Delete
    suspend fun delete(user: DatabaseUnit)

    @Query("DELETE FROM `database-games`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `database-games` WHERE position IN (:position)")
    suspend fun getAll(position: List<Int>): List<DatabaseUnit>

    @Query("SELECT COUNT(*) FROM `database-games`")
    fun getCount(): Int

}

class LocalStorage(context: Context) {

    companion object {
        private const val TAG: String = "LocalStorage-TAG"
        @Volatile private var instance: LocalStorage? = null
        fun getInstance(context: Context): LocalStorage {
            return instance ?: synchronized(this) {
                instance ?: LocalStorage(context).also { instance = it }
            }
        }
    }

    val db = Room.databaseBuilder(context, AppDatabase::class.java, "database-games").build()
    val userDao = db.gamesDao()

    suspend fun saveAll(element: Map<Int, TwitchTopResponseElement>) {
        val list = arrayListOf<DatabaseUnit>()
        element.forEach {
            list.add(DatabaseUnit(it.key, Json.encodeToString(it.value)))
        }
        userDao.insertAll(list)
    }
    suspend fun deleteAll() {
        userDao.deleteAll()
    }
    suspend fun getAll(): Map<Int, TwitchTopResponseElement> {
        val list = userDao.getAll()
        val result = mutableMapOf<Int, TwitchTopResponseElement>()
        list.forEach {
            result[it.position] = Json.decodeFromString(it.data)
        }
        return result
    }
    suspend fun getAll(positions: List<Int>): Map<Int, TwitchTopResponseElement> {
        val list = userDao.getAll(positions)
        val result = mutableMapOf<Int, TwitchTopResponseElement>()
        list.forEach {
            result[it.position] = Json.decodeFromString(it.data)
        }
        return result
    }

}