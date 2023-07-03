package com.example.memoverse

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM note_table WHERE date = :date")
    fun getNoteByDate(date: String): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}