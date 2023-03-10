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
        "noteDatabase"
    ).build()

    private val dao = database.noteDataDao()

    fun getList(): List<NoteOnList> = runBlocking {
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            dao.getAllForList()
        }
    }

    fun getNote(targetId: Long): NoteData = runBlocking {
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            dao.getById(targetId)
        }
    }

    fun insertNote(note: NoteData): Boolean = runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(note)
        }.isCompleted
    }

    fun updateNote(note: NoteData): Boolean = runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            dao.update(note)
        }.isCompleted
    }

    fun deleteNote(id: Long): Boolean = runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                dao.getById(id)
            }.let { dao.delete(it) }
        }.isCompleted
    }
}