package com.example.memoverse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "summary") val summary: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name="image") val image : ByteArray?
)

