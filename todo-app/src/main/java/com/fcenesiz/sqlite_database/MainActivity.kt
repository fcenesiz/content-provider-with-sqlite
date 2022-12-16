package com.fcenesiz.sqlite_database

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.fcenesiz.sqlite_database.adapter.CategoryCursorAdapter
import com.fcenesiz.sqlite_database.adapter.NotesCursorAdapter
import com.fcenesiz.sqlite_database.data.MyQueryHandler
import com.fcenesiz.sqlite_database.data.ToDoContract.*
import com.fcenesiz.sqlite_database.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        public val TAG: String = MainActivity::class.simpleName.toString()
    }

    lateinit var binding: ActivityMainBinding
    lateinit var listViewNotesAdapter: NotesCursorAdapter
    lateinit var categoryCursorAdapter: CategoryCursorAdapter
    lateinit var cursorNote: Cursor
    var cursorCategory: Cursor? = null
    var selectedCategoryId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }
        binding.layoutContentMain.listViewNotes.setOnItemClickListener { adapterView, view, i, l ->
            onListViewNotesItemClick(
                binding.layoutContentMain.listViewNotes.getItemAtPosition(i) as Cursor
            )
        }
    }

    fun initialize() {

        // loader initialization and triggering onCreateLoader
        LoaderManager.getInstance(this).apply {
            initLoader(
                101, // loader id
                null,
                this@MainActivity
            )
            initLoader(
                100, // loader id
                null,
                this@MainActivity
            )
        }


        listViewNotesAdapter = NotesCursorAdapter(
            this,
            null,
            false
        )
        binding.layoutContentMain.listViewNotes.adapter = listViewNotesAdapter

        //init spinner
        categoryCursorAdapter = CategoryCursorAdapter(this, cursorCategory, false)
        binding.layoutContentMain.spinnerCategory.apply {
            adapter = categoryCursorAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                    selectedCategoryId = id.toString()
                    LoaderManager.getInstance(this@MainActivity)
                        .restartLoader(
                            100, // loader id
                        null,
                            this@MainActivity
                        )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }
    }

    fun onListViewNotesItemClick(cursor: Cursor) {
        startActivity(Intent(this, NoteActivity::class.java).apply {

            val idIndex = cursor.getColumnIndex(Note._ID)
            val contentIndex = cursor.getColumnIndex(Note.COLUMN_CONTENT)
            val creationDateIndex = cursor.getColumnIndex(Note.COLUMN_CREATION_DATE)
            val endDateIndex = cursor.getColumnIndex(Note.COLUMN_END_DATE)
            val doneIndex = cursor.getColumnIndex(Note.COLUMN_DONE)
            val categoryIdIndex = cursor.getColumnIndex(Note.COLUMN_CATEGORY_ID)
            val categoryColumnIndex = cursor.getColumnIndex(Category.COLUMN_CATEGORY)

            putExtra(Note._ID, cursor.getString(idIndex))
            putExtra(Note.COLUMN_CONTENT, cursor.getString(contentIndex))
            putExtra(Note.COLUMN_CREATION_DATE, cursor.getString(creationDateIndex))
            putExtra(Note.COLUMN_END_DATE, cursor.getString(endDateIndex))
            putExtra(Note.COLUMN_DONE, cursor.getString(doneIndex))
            putExtra(Note.COLUMN_CATEGORY_ID, cursor.getString(categoryIdIndex))
            putExtra(Category.COLUMN_CATEGORY, cursor.getString(categoryColumnIndex))

        })
    }

    private fun providerAddNote() {
        val contentValues = ContentValues().apply {
            put(Note.COLUMN_CONTENT, "Hug Someone")
            put(Note.COLUMN_CREATION_DATE, "15-12-2022")
            put(Note.COLUMN_END_DATE, "")
            put(Note.COLUMN_DONE, 0)
            put(Note.COLUMN_CATEGORY_ID, 87)
        }
        val uri = contentResolver.insert(Note.CONTENT_URI, contentValues)
        Log.i(TAG, "addNote: $uri")
    }

    private fun providerAddCategory() {
        val contentValues = ContentValues().apply {
            put(Category.COLUMN_CATEGORY, "Deneme kategori")
        }
        val uri = contentResolver.insert(Category.CONTENT_URI, contentValues)
        Log.i(TAG, "addCategory: $uri")
    }

    private fun providerShowCategory(): Cursor? {
        val projection = arrayOf(
            Category.TABLE_NAME + "." + Category._ID,
            Category.TABLE_NAME + "." + Category.COLUMN_CATEGORY
        )
        return contentResolver.query(
            Category.CONTENT_URI,
            projection,
            null, null, null, null
        )
        /*
        var allCategories = ""
        contentResolver.query(
            Category.CONTENT_URI,
            projection,
            null, null, null, null
        )?.let {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val category = it.getString(1)
                allCategories += "id: $id\tcategory: $category\n"
            }
            it.close()
            Log.i(TAG, "showCategory: \n$allCategories")
        }

         */
    }

    private fun providerShowNote(): Cursor? {
        val projection = arrayOf(
            Note.TABLE_NAME + "." + Note._ID,
            Note.TABLE_NAME + "." + Note.COLUMN_CONTENT,
            Note.TABLE_NAME + "." + Note.COLUMN_CATEGORY_ID,
            Category.TABLE_NAME + "." + Category.COLUMN_CATEGORY
        )
        return contentResolver.query(
            Note.CONTENT_URI,
            projection,
            null, null, null, null
        )
        /*
        var allNotes = ""
        contentResolver.query(
            Note.CONTENT_URI,
            projection,
            null, null, null, null
        )?.let {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val content = it.getString(1)
                val categoryId = it.getString(2)
                val categoryName = it.getString(3)
                allNotes += "id: $id\tnote: $content\t\n\tcategory_id: $categoryId\tcategoryName: $categoryName\n"
            }
            it.close()
            Log.i(TAG, "showNotes: \n$allNotes")
        }
         */
    }

    private fun providerUpdateNote() {
        val id = contentResolver.update(
            Note.CONTENT_URI,
            ContentValues().apply {
                put(
                    Note.COLUMN_CONTENT,
                    "Updated new content"
                )
            },
            "${Note._ID} = ?",
            arrayOf("104")
        )
        Toast.makeText(this, "Entry update: $id", Toast.LENGTH_SHORT).show()
    }

    private fun providerDeleteNote(noteId: Int) {
        // if noteId == -1 -> delete all notes
        val deleteAll = noteId == -1
        val selection = if (deleteAll) null else "${Note._ID} = ?"
        val args = if (deleteAll) null else arrayOf("$noteId")
        val id = contentResolver.delete(
            Note.CONTENT_URI,
            selection,
            args
        )
        Toast.makeText(this, "Entry delete: $id", Toast.LENGTH_SHORT).show()
    }

    private fun providerDeleteCategory(categoryId: Int) {
        // if categoryId == -1 -> delete all notes
        val deleteAll = categoryId == -1
        val selection = if (deleteAll) null else "${Category._ID} = ?"
        val args = if (deleteAll) null else arrayOf("$categoryId")
        //val id = contentResolver.delete(
        //    Category.CONTENT_URI,
        //    selection,
        //    args
        //)
        //Toast.makeText(this, "Entry delete: $id", Toast.LENGTH_SHORT).show()

        // async delete
        MyQueryHandler(contentResolver).apply {
            startDelete(
                0,
                null,
                Category.CONTENT_URI,
                selection,
                args
            )
        }

    }

    private fun testCreateCategories() {
        for (i in 0 until 10) {
            val contentValues = ContentValues().apply {
                put(Category.COLUMN_CATEGORY, "Deneme kategori #$i")
            }
            val uri = contentResolver.insert(Category.CONTENT_URI, contentValues)
            Log.i(TAG, "addCategory: $uri")
        }
    }

    private fun testCreateNotes() {
        for (i in 1 until 100) {
            val contentValues = ContentValues().apply {
                put(Note.COLUMN_CONTENT, "Hug Someone #$i")
                put(Note.COLUMN_CREATION_DATE, "15-12-2022")
                put(Note.COLUMN_END_DATE, "17-12-2022")
                put(Note.COLUMN_DONE, if (i % 2 == 0) "1" else "0")
                put(Note.COLUMN_CATEGORY_ID, (i) % 10 + 1)
            }
            //val uri = contentResolver.insert(Note.CONTENT_URI, contentValues)
            //Log.i(TAG, "addNote: $uri")

            // async query
            val queryHandler = MyQueryHandler(contentResolver)
            queryHandler.startInsert(
                0,
                null,
                Note.CONTENT_URI,
                contentValues
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_categories -> {
                startActivity(Intent(this, CategoryActivity::class.java))
                return true
            }
            R.id.action_delete_all_categories -> {
                providerDeleteCategory(-1)
                return true
            }
            R.id.action_delete_all_notes -> {
                providerDeleteNote(-1)
                return true
            }
            R.id.action_test_categories -> {
                testCreateCategories()
                return true
            }
            R.id.action_test_notes -> {
                testCreateNotes()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            100 -> {
                val projection = arrayOf(
                    Note.TABLE_NAME + "." + Note._ID,
                    Note.COLUMN_CONTENT,
                    Note.COLUMN_CREATION_DATE,
                    Note.COLUMN_END_DATE,
                    Note.COLUMN_DONE,
                    Note.COLUMN_CATEGORY_ID,
                    Category.TABLE_NAME + "." + Category.COLUMN_CATEGORY
                )
                val selection = "${Note.COLUMN_CATEGORY_ID} = ?"
                val selectionArgs = arrayOf(selectedCategoryId)
                val orderBy = "${Note.TABLE_NAME}.${Note._ID} DESC"
                return CursorLoader(
                    this,
                    Note.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    orderBy
                )
            }
            101 -> {
                val projection = arrayOf(
                    Category.TABLE_NAME + "." + Category._ID,
                    Category.COLUMN_CATEGORY
                )
                val orderBy = "${Category._ID} ASC"
                return CursorLoader(
                    this,
                    Category.CONTENT_URI,
                    projection,
                    null, null, orderBy
                )
            }
        }
        return CursorLoader(this)
    }


    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data?.let {
            when (loader.id) {
                100 -> {
                    cursorNote = data
                    listViewNotesAdapter.swapCursor(data)
                }
                101 -> {
                    cursorCategory = data
                    categoryCursorAdapter.swapCursor(data)
                }
                else -> {}
            }
        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {
        listViewNotesAdapter.swapCursor(null)
        categoryCursorAdapter.swapCursor(null)
    }

}

/*  SQLite operations without Content Provider

    private fun createMockNotes() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.writableDatabase

        // method 1 not recommended
        val insertQuery = "INSERT INTO ${Note.TABLE_NAME}(" +
                "${ToDoContract.Note.COLUMN_CONTENT}," +
                "${ToDoContract.Note.COLUMN_CREATION_DATE}," +
                "${ToDoContract.Note.COLUMN_END_DATE}," +
                "${ToDoContract.Note.COLUMN_DONE}," +
                "${ToDoContract.Note.COLUMN_CATEGORY_ID})" +
                " VALUES (\"Go to Gym\", \"13-12-2022\", \"\", 0, 1)"

        db?.execSQL(insertQuery)

        // method 2 recommended
        val newNote = ContentValues()
        newNote.put(ToDoContract.Note.COLUMN_CONTENT, "Clear Home")
        newNote.put(ToDoContract.Note.COLUMN_CREATION_DATE, "12-12-2022")
        newNote.put(ToDoContract.Note.COLUMN_END_DATE, "")
        newNote.put(ToDoContract.Note.COLUMN_DONE, 0)
        newNote.put(ToDoContract.Note.COLUMN_CATEGORY_ID, 1)

        val id = db.insert(ToDoContract.Note.TABLE_NAME, null, newNote)

    }

    private fun readNotes() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        val projection = arrayOf<String>(
            ToDoContract.Note.COLUMN_CONTENT,
            ToDoContract.Note.COLUMN_CREATION_DATE,
            ToDoContract.Note.COLUMN_END_DATE,
            ToDoContract.Note.COLUMN_DONE,
            ToDoContract.Note.COLUMN_CATEGORY_ID
        )
        val selection = ToDoContract.Note.COLUMN_CATEGORY_ID + " = ?"
        val selectionArgs = arrayOf("1")

        val cursor = db.query(
            ToDoContract.Note.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null, null, null
        )
        val count = cursor.count
        Toast.makeText(this, "count : $count", Toast.LENGTH_SHORT).show()

        while (cursor.moveToNext()) {
            var note = ""
            for (i in 0 until cursor.columnCount)
                note += cursor.getString(i) + ",\t"
            Log.i(TAG, note)
        }

        cursor.close()
        db.close()
    }

    private fun updateNote() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        val updatedValues = ContentValues().apply {
            put(ToDoContract.Note.COLUMN_CONTENT, "new updated value")
        }
        val args = arrayOf("13")

        val affectedRowCount = db.update(
            ToDoContract.Note.TABLE_NAME,
            updatedValues,
            ToDoContract.Note._ID + " = ?",
            args
        )

        Log.i(TAG, "updateNote: affected row count: $affectedRowCount")

    }

    private fun deleteNote() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        for (i in 0 until 20) {
            val args = arrayOf(i.toString()) // id
            db.delete(
                ToDoContract.Note.TABLE_NAME,
                ToDoContract.Note._ID + " = ?",
                args
            )
        }

    }

 */