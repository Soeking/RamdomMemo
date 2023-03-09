package net.soeki.randommemo.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDataDao {
    @Query("SELECT id, text FROM noteData")
    suspend fun getAllForList(): List<NoteOnList>

    @Query("SELECT * FROM noteData WHERE id = :targetId")
    suspend fun getById(targetId: Long): NoteData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: NoteData)

    @Update
    suspend fun update(record: NoteData)

    @Delete
    suspend fun delete(record: NoteData)
}