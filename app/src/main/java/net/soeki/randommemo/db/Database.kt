package net.soeki.randommemo.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteData::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDataDao(): NoteDataDao
}