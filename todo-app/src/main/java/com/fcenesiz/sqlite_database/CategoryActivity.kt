package com.fcenesiz.sqlite_database

import android.content.ContentValues
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.fcenesiz.sqlite_database.adapter.CategoryCursorAdapter
import com.fcenesiz.sqlite_database.data.MyQueryHandler
import com.fcenesiz.sqlite_database.data.ToDoContract.*
import com.fcenesiz.sqlite_database.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object{
        public val TAG: String = CategoryActivity::class.simpleName.toString()
    }

    lateinit var binding: ActivityCategoryBinding
    lateinit var categoryCursorAdapter: CategoryCursorAdapter
    var cursor: Cursor? = null
    var selectedCategoryId = -1L
    lateinit var queryHandler : MyQueryHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        queryHandler = MyQueryHandler(contentResolver)

        LoaderManager.getInstance(this).initLoader(
            50,
            null,
            this
        )
        categoryCursorAdapter = CategoryCursorAdapter(this, cursor, false)

        binding.listViewCategories.adapter = categoryCursorAdapter
        Log.i(TAG, "onCreate: I am running!")

        binding.listViewCategories.setOnItemClickListener { adapterView, view, position, id ->
            selectedCategoryId = id

            val cursor = binding.listViewCategories.getItemAtPosition(position) as Cursor
            val columnIndex = cursor.getColumnIndex(Category.COLUMN_CATEGORY)
            val category = cursor.getString(columnIndex)

            binding.editTextCategoryName.setText(category)
        }

        binding.buttonNew.setOnClickListener { createCategory() }
        binding.buttonSave.setOnClickListener { saveOrUpdateCategory() }
        binding.buttonDelete.setOnClickListener { deleteCategory() }
    }

    private fun createCategory() {
        selectedCategoryId = -1L
        binding.editTextCategoryName.setText("")
        binding.editTextCategoryName.isSelected = true

    }

    private fun saveOrUpdateCategory() {
        val categoryName = binding.editTextCategoryName.text.toString()
        val contentValues = ContentValues().apply {
            put(Category.COLUMN_CATEGORY, categoryName)
        }


        if (selectedCategoryId == -1L) { // insert
            queryHandler.startInsert(
                1,
                null,
                Category.CONTENT_URI,
                contentValues
            )
        } else { // update
            val selection = "${Category._ID} = ?"
            val selectionArgs = arrayOf(selectedCategoryId.toString())
            queryHandler.startUpdate(
                2,
                null,
                Category.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs
            )
        }
        binding.editTextCategoryName.setText("")
        selectedCategoryId = -1L
    }

    private fun deleteCategory() {
        val selection = "${Category._ID} = ?"
        val selectionArgs = arrayOf(selectedCategoryId.toString())
        queryHandler.startDelete(
            3,
            null,
            Category.CONTENT_URI,
            selection,
            selectionArgs
        )
        binding.editTextCategoryName.setText("")
        selectedCategoryId = -1L
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (id == 50) {
            val projection =
                arrayOf(Category._ID, Category.COLUMN_CATEGORY)
            val orderBy = "${Category._ID} DESC"
            return CursorLoader(
                this,
                Category.CONTENT_URI, projection, null, null, orderBy
            )
        }
        return CursorLoader(this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data?.let {
            cursor = it
            categoryCursorAdapter.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        categoryCursorAdapter.swapCursor(null)
    }
}