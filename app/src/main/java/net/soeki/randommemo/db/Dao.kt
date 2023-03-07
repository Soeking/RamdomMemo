package net.soeki.randommemo.db

import androidx.room.*

@Dao
interface NoteDataDao {
    @Query("SELECT * FROM noteData")
    fun getAll(): List<NoteData>

    @Query("SELECT text FROM noteData")
    fun getAllTexts(): List<String>

    @Query("SELECT * FROM noteData WHERE id = :targetId")
    fun getById(targetId:Long): NoteData

    @Insert
    fun insert(record: NoteData)

    @Update
    fun update(record: NoteData)

    @Delete
    fun delete(record: NoteData)
}