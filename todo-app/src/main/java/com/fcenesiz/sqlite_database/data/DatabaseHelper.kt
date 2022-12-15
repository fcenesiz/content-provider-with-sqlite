package com.fcenesiz.sqlite_database.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fcenesiz.sqlite_database.data.ToDoContract.Category
import com.fcenesiz.sqlite_database.data.ToDoContract.Note
class DatabaseHelper(
    val context : Context,
    val name : String,
    val factory : SQLiteDatabase.CursorFactory?,
    val version : Int
    ) : SQLiteOpenHelper(context, name, factory, version) {

    companion object{
        const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 1

        // constants
        // create
        private const val TABLE_CATEGORIES_CREATE =
            "CREATE TABLE ${Category.TABLE_NAME} (" +
                    "${Category._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Category.COLUMN_CATEGORY} TEXT" +
                    ")"
        private const val TABLE_NOTES_CREATE =
            "CREATE TABLE ${Note.TABLE_NAME} (" +
                    "${Note._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Note.COLUMN_CONTENT} TEXT," +
                    "${Note.COLUMN_CREATION_DATE} TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "${Note.COLUMN_END_DATE} TEXT," +
                    "${Note.COLUMN_DONE} INTEGER," +
                    "${Note.COLUMN_CATEGORY_ID} INTEGER," +
                    "FOREIGN KEY(${Note.COLUMN_CATEGORY_ID}) REFERENCES ${Category.TABLE_NAME}(${Category._ID})" +
                    ")" // FOREIGN KEY => if the category is deleted, the note is also deleted!
        // drop
        private const val DROP_TABLE_CATEGORIES =
            "DROP TABLE IF EXISTS ${Category.TABLE_NAME}"
        private const val DROP_TABLE_NOTES =
            "DROP TABLE IF EXISTS ${Note.TABLE_NAME}"

        // join tables
        const val INNER_JOIN_NOTE_TO_CATEGORIES_ON_CATEGORY_ID =
            "${Note.TABLE_NAME} INNER JOIN ${Category.TABLE_NAME} " +
                    "ON " +
                    "${Note.TABLE_NAME}.${Note.COLUMN_CATEGORY_ID}" +
                    " = " +
                    "${Category.TABLE_NAME}.${Category._ID}"
    }

    constructor(context: Context) : this(context, DATABASE_NAME, null, DATABASE_VERSION)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLE_CATEGORIES_CREATE)
        db?.execSQL(TABLE_NOTES_CREATE)
    }

    // should enable to use foreign key
    override fun onConfigure(db: SQLiteDatabase?) {
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_CATEGORIES)
        db?.execSQL(DROP_TABLE_NOTES)
        // to don't lose user data need to handle here
        onCreate(db)
    }

}