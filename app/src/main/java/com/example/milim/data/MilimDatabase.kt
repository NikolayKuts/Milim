package com.example.milim.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.milim.pojo.Deck
import com.example.milim.pojo.Word

@Database(entities = [Deck::class, Word::class], version = 2, exportSchema = false)
abstract class MilimDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME = "milim.db"
        private var database: MilimDatabase? = null
        private val LOCK = Any()


        fun getInstance(context: Context): MilimDatabase {
            synchronized(LOCK) {
                database?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    MilimDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                database = instance
                return instance
            }
        }
    }
    abstract fun decksDao(): DecksDao
    abstract fun wordsDao(): WordsDao
}