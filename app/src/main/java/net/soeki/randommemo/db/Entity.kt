package net.soeki.randommemo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(indices = [Index(value = ["id"], unique = true)])
data class NoteData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo var text: String = "",
    @ColumnInfo var description: String = "",
    @ColumnInfo var updateDate: String = ""
)

data class NoteOnList(
    @ColumnInfo val id: Long,
    @ColumnInfo val text: String
)