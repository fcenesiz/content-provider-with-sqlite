package com.fcenesiz.sqlite_database.data

import android.net.Uri
import android.provider.BaseColumns

class ToDoContract {

    public companion object{
        val CONTENT_AUTHORITY = "com.fcenesiz.sqlite_database.todo_content_provider"
        val PATH_TABLE_NOTES = "notes"
        val PATH_TABLE_CATEGORIES = "categories"
        val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
    }

    object Note : BaseColumns {
        val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TABLE_NOTES)
        val CONTENT_URI_CODE = 1

        const val TABLE_NAME: String = "Notes"
        const val _ID: String = BaseColumns._ID
        const val COLUMN_CONTENT: String = "Note"
        const val COLUMN_CREATION_DATE: String = "Creation_Date"
        const val COLUMN_END_DATE: String = "End_Date"
        const val COLUMN_DONE: String = "Done"
        const val COLUMN_CATEGORY_ID: String = "Category_Id"
    }

    object Category : BaseColumns {
        val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TABLE_CATEGORIES)
        val CONTENT_URI_CODE = 2

        const val TABLE_NAME: String = "Categories"
        const val _ID: String = BaseColumns._ID
        const val COLUMN_CATEGORY: String = "Category"
    }

}