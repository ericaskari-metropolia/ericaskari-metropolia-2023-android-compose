package com.ericaskari.w3d5retrofit.datasource

//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import androidx.sqlite.db.SupportSQLiteDatabase
//import com.example.exercise_5.datasource.Converters
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
///**
// * @author Mohammad Askari
// */
//@Database(entities = [Member::class, MemberInfo::class, MemberGrade::class, Party::class, MemberComment::class], version = 9)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun memberDao(): MemberDao
//    abstract fun memberInfoDao(): MemberInfoDao
//    abstract fun memberGradeDao(): MemberGradeDao
//    abstract fun memberCommentDao(): MemberCommentDao
//    abstract fun partyDao(): PartyDao
//
//    companion object {
//        private var DB_NAME: String = "exercise-5-db"
//
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
//                    .fallbackToDestructiveMigration()
//                    .addCallback(AppDatabaseCallback(scope))
//                    .build()
//
//                INSTANCE = instance
//
//                // return instance
//                instance
//            }
//        }
//    }
//
//    /**
//     * Database initializer
//     */
//    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
//        /**
//         * Override the onCreate method to populate the database.
//         */
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//
//            INSTANCE?.let { database ->
//                scope.launch(Dispatchers.IO) {
//                    database.populateDatabase(database.memberDao())
//                }
//            }
//        }
//    }
//
//    /**
//     * Database initializer
//     */
//    @Suppress("RedundantSuspendModifier")
//    suspend fun populateDatabase(memberDao: MemberDao) {
//        println("one time populating the database")
//    }
//
//}
