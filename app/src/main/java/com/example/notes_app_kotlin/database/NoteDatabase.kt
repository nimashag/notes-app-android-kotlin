package com.example.notes_app_kotlin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notes_app_kotlin.model.Note

// Define the database using RoomDatabase annotation
@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    // Abstract method to get the DAO (Data Access Object)
    abstract fun getNoteDao(): NoteDao

    companion object {
        // Volatile variable to ensure that changes made by one thread are visible to other threads
        @Volatile
        private var instance: NoteDatabase? = null
        // Lock object to ensure that only one thread can access the database creation process at a time
        private val LOCK = Any()

        // Operator function to create an instance of the database
        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context).also {
                    instance = it
                }
            }

        // Function to create the database instance
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "note_db"
            ).build()
    }
}
