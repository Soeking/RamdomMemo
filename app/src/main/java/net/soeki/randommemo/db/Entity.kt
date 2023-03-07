package net.soeki.randommemo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(indices = [Index(value = ["id"], unique = true)])
data class NoteData(
    @PrimaryKey val id:Long,
    @ColumnInfo val text:String,
    @ColumnInfo val description:String?,
    @ColumnInfo val updateDate:Date
)