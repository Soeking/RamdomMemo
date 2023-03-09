package net.soeki.randommemo

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.*
import net.soeki.randommemo.db.NoteData
import net.soeki.randommemo.db.NoteDatabase
import net.soeki.randommemo.db.NoteOnList

@OptIn(ExperimentalCoroutinesApi::class)
class AccessDatabase(applicationContext: Context) {
    private val database = Room.databaseBuilder(
        applicationContext,
        NoteDatabase::class.java,
        "noteData"
    ).build()

    private val dao = database.noteDataDao()

    fun getList(): List<NoteOnList> {
        return CoroutineScope(Dispatchers.IO).async {
            dao.getAllForList()
        }.getCompleted()
    }

    fun getNote(targetId: Long): NoteData {
        return CoroutineScope(Dispatchers.IO).async {
            dao.getById(targetId)
        }.getCompleted()
    }

    fun insertNote(note: NoteData): Boolean {
        return CoroutineScope(Dispatchers.IO).launch {
            dao.insert(note)
        }.isCompleted
    }

    fun updateNote(note: NoteData): Boolean {
        return CoroutineScope(Dispatchers.IO).launch {
            dao.update(note)
        }.isCompleted
    }

    fun deleteNote(id: Long): Boolean {
        return CoroutineScope(Dispatchers.IO).launch {
            dao.getById(id).let { dao.delete(it) }
        }.isCompleted
    }
}