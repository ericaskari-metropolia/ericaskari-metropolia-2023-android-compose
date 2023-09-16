package com.ericaskari.w3d5beacon.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ericaskari.w3d5beacon.bluetoothsearch.AppBluetoothSearch
import com.ericaskari.w3d5beacon.bluetoothsearch.AppBluetoothSearchDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Mohammad Askari
 */
@Database(entities = [AppBluetoothSearch::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appBluetoothSearchDao(): AppBluetoothSearchDao
//    abstract fun actorDao(): ActorDao

    companion object {
        private var DB_NAME: String = "w3d5beacon"

        @Volatile
        private var Instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback())
                    .build()
                    .also { Instance = it }
            }
        }


        /**
         * Database initializer
         */
        private class AppDatabaseCallback() : Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        Instance!!.populateDatabase()
                    }
                }
            }
        }
    }

    /**
     * Database initializer
     */
    suspend fun populateDatabase() {
        println("one time populating the database")
    }

}
