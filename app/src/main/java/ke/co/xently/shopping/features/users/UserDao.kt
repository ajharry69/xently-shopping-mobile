package ke.co.xently.shopping.features.users

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    fun get(): Flow<User?>

    @Query("DELETE FROM user") // We expect to have 1 record anyway
    fun deleteCurrentlySignedInUserOnSessionExpiration()
}