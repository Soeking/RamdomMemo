package net.soeki.randommemo.db

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date

class AccessDatabase(applicationContext: Context) {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
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
            note.updateDate = dateFormat.format(Date())
            dao.insert(note)
        }.isCompleted
    }

    fun updateNote(note: NoteData): Boolean = runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            note.updateDate = dateFormat.format(Date())
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