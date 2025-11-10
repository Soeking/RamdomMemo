package net.soeki.randommemo.db

import androidx.room.*

@Dao
interface NoteDataDao {
    @Query("SELECT id, text, description FROM noteData")
    suspend fun getAllForList(): List<NoteOnList>

    @Query("SELECT * FROM noteData WHERE id = :targetId")
    suspend fun getById(targetId: Long): NoteData

    @Query("SELECT * FROM noteData")
    suspend fun getAllData():List<NoteData>

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insert(record: NoteData)

    @Update
    suspend fun update(record: NoteData)

    @Delete
    suspend fun delete(record: NoteData)
}