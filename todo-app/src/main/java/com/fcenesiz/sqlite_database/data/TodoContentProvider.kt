package com.fcenesiz.sqlite_database.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

class TodoContentProvider : ContentProvider() {

    companion object {
        public val TAG: String = TodoContentProvider::class.simpleName.toString()
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(
                ToDoContract.CONTENT_AUTHORITY,
                ToDoContract.PATH_TABLE_NOTES,
                ToDoContract.Note.CONTENT_URI_CODE
            )
            addURI(
                ToDoContract.CONTENT_AUTHORITY,
                ToDoContract.PATH_TABLE_CATEGORIES,
                ToDoContract.Category.CONTENT_URI_CODE
            )
        }
    }

    lateinit var db: SQLiteDatabase
    lateinit var helper: DatabaseHelper

    override fun onCreate(): Boolean {
        context?.let {
            helper = DatabaseHelper(it)
            db = helper.writableDatabase
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        orderBy: String?
    ): Cursor? {
        val cursor: Cursor?
        when (uriMatcher.match(uri)) {
            ToDoContract.Note.CONTENT_URI_CODE -> {
                val queryBuilder = SQLiteQueryBuilder()
                queryBuilder.tables = DatabaseHelper.INNER_JOIN_NOTE_TO_CATEGORIES_ON_CATEGORY_ID
                cursor = queryBuilder.query(
                    db,
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
                )
                cursor.setNotificationUri(context?.contentResolver, uri)
                return cursor
            }
            ToDoContract.Category.CONTENT_URI_CODE -> {
                cursor = db.query(
                    ToDoContract.Category.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
                )
                // sends an notification
                cursor.setNotificationUri(context?.contentResolver, uri)
                return cursor
            }
            else -> throw java.lang.IllegalArgumentException("QUERY: UNKNOWN URI: $uri")
        }
    }

    override fun getType(p0: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            ToDoContract.Note.CONTENT_URI_CODE ->
                addEntry(uri, values, ToDoContract.Note.TABLE_NAME)
            ToDoContract.Category.CONTENT_URI_CODE ->
                addEntry(uri, values, ToDoContract.Category.TABLE_NAME)
            else -> throw java.lang.IllegalArgumentException("INSERT, UNKNOWN URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        args: Array<out String>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            ToDoContract.Note.CONTENT_URI_CODE ->
                updateEntry(ToDoContract.Note.TABLE_NAME, uri, values, selection, args)
            ToDoContract.Category.CONTENT_URI_CODE ->
                updateEntry(ToDoContract.Category.TABLE_NAME, uri, values, selection, args)
            else -> throw IllegalArgumentException("UPDATE, UNKNOWN URI:$uri")
        }
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return when(uriMatcher.match(p0)){
            ToDoContract.Note.CONTENT_URI_CODE ->
                deleteEntry(ToDoContract.Note.TABLE_NAME, p0, p1, p2)
            ToDoContract.Category.CONTENT_URI_CODE ->
                deleteEntry(ToDoContract.Category.TABLE_NAME, p0, p1, p2)
            else -> throw IllegalArgumentException("DELETE, UNKNOWN URI:$p0")
        }
    }

    // to avoid code duplication
    private fun addEntry(uri: Uri, values: ContentValues?, tableName: String): Uri? {
        val id = db.insert(tableName, null, values)
        if (id < 0) {
            Log.i(TAG, "INSERT ERROR:$uri")
            return null
        }
        // receives notifications
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    // to avoid code duplication
    private fun updateEntry(
        tableName: String,
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        args: Array<out String>?
    ): Int {
        val id = db.update(tableName, values, selection, args)
        return if (id >= 0) {
            // receives notifications
            context?.contentResolver?.notifyChange(uri, null)
            id
        } else {
            Log.i(TAG, "updateEntry: Error, uri:$uri")
            -1
        }
    }

    private fun deleteEntry( tableName: String,
                             uri: Uri,
                             selection: String?,
                             args: Array<out String>?): Int{
        val id = db.delete(tableName, selection, args)
        return if (id >= 0) {
            // receives notifications
            context?.contentResolver?.notifyChange(uri, null)
            id
        } else{
            Log.i(TAG, "deleteEntry: Error, uri:$uri")
            -1
        }
    }
}