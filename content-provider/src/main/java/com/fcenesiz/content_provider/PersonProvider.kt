package com.fcenesiz.content_provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class PersonProvider : ContentProvider() {

    companion object {

        // about content provider
        val CONTENT_AUTHORITY = "com.fcenesiz.content_provider.PersonProvider"
        val PATH_PERSON_TABLE = "person"
        val URI_CODE_PERSON_TABLE = 1
        val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
        public val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PERSON_TABLE)
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(
                CONTENT_AUTHORITY,
                PATH_PERSON_TABLE,
                URI_CODE_PERSON_TABLE // like unique id
            )
        }

        // about content provider

        // about database and tables
        val DATABASE_NAME = "person.db"
        val DATABASE_VERSION = 1
        val TABLE_NAME_PERSON = "person"

        // sql constants
        val CREATE_PERSON_TABLE =
            "CREATE TABLE $TABLE_NAME_PERSON (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL" +
                    ")"
        val DROP_PERSON_TABLE =
            "DROP TABLE IF EXISTS $TABLE_NAME_PERSON"
        // about database and tables
    }

    lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
        context?.let {
            val databaseHelper = DatabaseHelper(it)
            db = databaseHelper.writableDatabase
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        orderBy: String?
    ): Cursor? {
        when(uriMatcher.match(uri)){
            URI_CODE_PERSON_TABLE -> {
                val cursor = db.query(
                    TABLE_NAME_PERSON,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
                )
                return cursor
            }
            else -> throw java.lang.IllegalArgumentException("Unknown uri: $uri")
        }
    }

    override fun getType(p0: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            URI_CODE_PERSON_TABLE -> {
                val rowId = db.insert(TABLE_NAME_PERSON, null, values)
                if (rowId > 0) {
                    return ContentUris.withAppendedId(CONTENT_URI, rowId)
                }
            }
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        when(uriMatcher.match(uri)){
            URI_CODE_PERSON_TABLE ->{
                val removedRowCount = db.delete(
                    TABLE_NAME_PERSON,
                    selection,
                    selectionArgs
                )
                return removedRowCount
            }
            else -> throw java.lang.IllegalArgumentException("Unknown uri: $uri")
        }
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    inner class DatabaseHelper(
        context: Context
    ) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_PERSON_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db?.execSQL(DROP_PERSON_TABLE)
            onCreate(db)
        }

    }

}