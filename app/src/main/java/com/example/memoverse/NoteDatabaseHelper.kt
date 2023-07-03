package com.example.memoverse

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.sql.Blob

class NoteDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "NoteDatabase.db"

        private const val TABLE_NAME = "note_table"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_SUMMARY = "summary"
        private const val COLUMN_IMAGE="image"
        private lateinit var noteDao : NoteDao
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_SUMMARY TEXT," +
                "$COLUMN_IMAGE BYTEARRAY"+
                ")"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DATE, note.date)
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_SUMMARY, note.summary)
        values.put(COLUMN_IMAGE,note.image)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

//    @SuppressLint("Range")
//    fun getNoteByDate(date: String): Note? {
//        val db = readableDatabase
//        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?", arrayOf(date))
//        var note: Note? = null
//        val list : List<Note> = ArrayList()
//        if (cursor.moveToFirst()) {
//            note = Note(
//                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
//                date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
//                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
//                summary = cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)),
//                image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE))
//            )
//        }
//        cursor.close()
//        db.close()
//        return note
//    }
@SuppressLint("Range")
fun getNoteByDate(date: String): ArrayList<Note>? {
    val db = readableDatabase
    val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?", arrayOf(date))
    val list : ArrayList<Note> = ArrayList()
    if (cursor.moveToFirst()) {
        do{
           var note = Note(
               id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
               date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
               title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
               summary = cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)),
               image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE))
           )
            list.add(note)
        }while (cursor.moveToNext());
    }
    cursor.close()
    db.close()
    return list
}



    fun blobToByteArray(blob: Blob?): ByteArray? {
        if (blob == null) {
            return null
        }

        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesRead: Int
        val inputStream = blob.binaryStream

        try {
            bytesRead = inputStream.read(buffer)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream.close()
            inputStream.close()
        }

        return outputStream.toByteArray()
    }


    fun byteArrayFromString(byteArrayString: String): ByteArray? {
        return try {
            val decodedBytes = Base64.decode(byteArrayString, Base64.DEFAULT)
            decodedBytes
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }


    @SuppressLint("Range")
    fun getUserById(id: Int): Note? {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?", arrayOf(id.toString()))
        var note: Note? = null
        if (cursor.moveToFirst()) {
            note = Note(
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                summary = cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)),
                image = byteArrayFromString(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)))
            )
        }
        cursor.close()
        db.close()
        return note
    }

    @SuppressLint("Range")
    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val note = Note(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                    title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    summary = cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)),
                    image = byteArrayFromString(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)))
                )
                notes.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notes
    }
}