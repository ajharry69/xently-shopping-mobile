package ke.co.xently.shopping.datasource.local.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.co.xently.shopping.BuildConfig
import ke.co.xently.shopping.datasource.local.Database
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "${context.packageName}.shopping.db"
        ).fallbackToDestructiveMigration().apply {

            if (BuildConfig.DEBUG) {
                setQueryCallback(
                    object : RoomDatabase.QueryCallback {
                        override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                            Timber.d("Query <${sqlQuery}>. Args: <${bindArgs.joinToString()}>")
                        }
                    },
                    Executors.newSingleThreadExecutor(),
                )
            }
        }.build()
    }
}