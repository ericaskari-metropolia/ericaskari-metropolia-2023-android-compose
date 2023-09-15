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
@Database(entities = [Actor::class, Movie::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun actorDao(): ActorDao

    companion object {
        private var DB_NAME: String = "w3d5retrofit"

        @Volatile
        private var Instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
//            return Instance ?: synchronized(this) {
//                val instance = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
//                    .fallbackToDestructiveMigration()
//                    .addCallback(AppDatabaseCallback())
//                    .build()
//                    .also { Instance = it }
//            }
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
                        Instance!!.populateDatabase(database.movieDao(), database.actorDao())
                    }
                }
            }
        }
    }

    /**
     * Database initializer
     */
    suspend fun populateDatabase(movieDao: MovieDao, actorDao: ActorDao) {
        val movies = listOf(
            Movie("1", "Inception", "2010", "Christopher Nolan"),
            Movie("2", "The Shawshank Redemption", "1994", "Frank Darabont"),
            Movie("3", "Pulp Fiction", "1994", "Quentin Tarantino"),
            Movie("4", "The Godfather", "1972", "Francis Ford Coppola"),
            Movie("5", "Forrest Gump", "1994", "Robert Zemeckis"),
            Movie("6", "The Dark Knight", "2008", "Christopher Nolan"),
            Movie("7", "Fight Club", "1999", "David Fincher"),
            Movie("8", "The Matrix", "1999", "The Wachowskis"),
            Movie("9", "Gladiator", "2000", "Ridley Scott"),
            Movie("10", "The Lord of the Rings: The Fellowship of the Ring", "2001", "Peter Jackson")
        )

        val actors = listOf(
            Actor("1", "Leonardo DiCaprio", "1", "Cobb"),
            Actor("2", "Joseph Gordon-Levitt", "1", "Arthur"),
            Actor("3", "Ellen Page", "1", "Ariadne"),
            Actor("4", "Tom Hardy", "1", "Eames"),
            Actor("5", "Tim Robbins", "2", "Andy Dufresne"),
            Actor("6", "Morgan Freeman", "2", "Ellis Boyd 'Red' Redding"),
            Actor("7", "John Travolta", "3", "Vincent Vega"),
            Actor("8", "Samuel L. Jackson", "3", "Jules Winnfield"),
            Actor("9", "Uma Thurman", "3", "Mia Wallace"),
            Actor("10", "Bruce Willis", "3", "Butch Coolidge"),
            Actor("11", "Marlon Brando", "4", "Don Vito Corleone"),
            Actor("12", "Al Pacino", "4", "Michael Corleone"),
            Actor("13", "James Caan", "4", "Sonny Corleone"),
            Actor("14", "Robert Duvall", "4", "Tom Hagen"),
            Actor("15", "Tom Hanks", "5", "Forrest Gump"),
            Actor("16", "Robin Wright", "5", "Jenny Curran"),
            Actor("17", "Gary Sinise", "5", "Lieutenant Dan Taylor"),
            Actor("18", "Christian Bale", "6", "Bruce Wayne / Batman"),
            Actor("19", "Heath Ledger", "6", "Joker"),
            Actor("20", "Aaron Eckhart", "6", "Harvey Dent / Two-Face"),
            Actor("21", "Edward Norton", "7", "The Narrator"),
            Actor("22", "Brad Pitt", "7", "Tyler Durden"),
            Actor("23", "Helena Bonham Carter", "7", "Marla Singer"),
            Actor("24", "Keanu Reeves", "8", "Neo"),
            Actor("25", "Laurence Fishburne", "8", "Morpheus"),
            Actor("26", "Carrie-Anne Moss", "8", "Trinity"),
            Actor("27", "Russell Crowe", "9", "Maximus"),
            Actor("28", "Joaquin Phoenix", "9", "Commodus"),
            Actor("29", "Connie Nielsen", "9", "Lucilla"),
            Actor("30", "Elijah Wood", "10", "Frodo Baggins"),
            Actor("31", "Ian McKellen", "10", "Gandalf"),
            Actor("32", "Viggo Mortensen", "10", "Aragorn")
        )

        actorDao.insertItems(*actors.toTypedArray())
        movieDao.insertItems(*movies.toTypedArray())

        println("one time populating the database")
    }

}
