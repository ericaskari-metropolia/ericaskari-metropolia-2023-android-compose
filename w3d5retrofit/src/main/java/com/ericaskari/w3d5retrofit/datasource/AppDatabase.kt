package com.ericaskari.w3d5retrofit.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ericaskari.w3d5retrofit.entities.Actor
import com.ericaskari.w3d5retrofit.entities.ActorDao
import com.ericaskari.w3d5retrofit.entities.Movie
import com.ericaskari.w3d5retrofit.entities.MovieDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Mohammad Askari
 */
@Database(entities = [Actor::class, Movie::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun actorDao(): ActorDao

    companion object {
        private var DB_NAME: String = "w3d5retrofit"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()

                INSTANCE = instance

                // return instance
                instance
            }
        }
    }

    /**
     * Database initializer
     */
    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        /**
         * Override the onCreate method to populate the database.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
//                    database.populateDatabase()
                }
            }
        }
    }

    /**
     * Database initializer
     */
    @Suppress("RedundantSuspendModifier")
    suspend fun populateDatabase() {
        println("one time populating the database")
    }

}
