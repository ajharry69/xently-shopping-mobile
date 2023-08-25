package ke.co.xently.shopping.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ke.co.xently.shopping.features.recommendations.datasources.local.RecommendationDao
import ke.co.xently.shopping.features.recommendations.models.RecommendationResponse
import ke.co.xently.shopping.features.users.User
import ke.co.xently.shopping.features.users.UserDao

@Database(
    entities = [
        User::class,
        RecommendationResponse.LocalCache::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class Database : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val recommendationDao: RecommendationDao
}