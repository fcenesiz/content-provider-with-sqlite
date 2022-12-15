package com.fcenesiz.sqlite_database

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.fcenesiz.sqlite_database.adapter.NotesCursorAdapter
import com.fcenesiz.sqlite_database.data.ToDoContract
import com.fcenesiz.sqlite_database.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        public val TAG: String = MainActivity::class.simpleName.toString()
    }

    lateinit var binding: ActivityMainBinding
    lateinit var listViewNotesAdapter : NotesCursorAdapter
    lateinit var cursor : Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }
    }

    fun initialize() {

        // loader initialization and triggering onCreateLoader
        LoaderManager.getInstance(this).initLoader(
            100, // loader id
            null,
            this
        )

        listViewNotesAdapter = NotesCursorAdapter(
            this,
            null,
            false
        )
        binding.layoutContentMain.listViewNotes.adapter = listViewNotesAdapter

    }




    private fun providerAddNote() {
        val contentValues = ContentValues().apply {
            put(ToDoContract.Note.COLUMN_CONTENT, "Hug Someone")
            put(ToDoContract.Note.COLUMN_CREATION_DATE, "15-12-2022")
            put(ToDoContract.Note.COLUMN_END_DATE, "")
            put(ToDoContract.Note.COLUMN_DONE, 0)
            put(ToDoContract.Note.COLUMN_CATEGORY_ID, 87)
        }
        val uri = contentResolver.insert(ToDoContract.Note.CONTENT_URI, contentValues)
        Log.i(TAG, "addNote: $uri")
    }

    private fun providerAddCategory() {
        val contentValues = ContentValues().apply {
            put(ToDoContract.Category.COLUMN_CATEGORY, "Deneme kategori")
        }
        val uri = contentResolver.insert(ToDoContract.Category.CONTENT_URI, contentValues)
        Log.i(TAG, "addCategory: $uri")
    }

    private fun providerShowCategory() : Cursor?{
        val projection = arrayOf(
            ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category._ID,
            ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category.COLUMN_CATEGORY
        )
        return contentResolver.query(
            ToDoContract.Category.CONTENT_URI,
            projection,
            null, null, null, null
        )
        /*
        var allCategories = ""
        contentResolver.query(
            ToDoContract.Category.CONTENT_URI,
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

    private fun providerShowNote() : Cursor?{
        val projection = arrayOf(
            ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note._ID,
            ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note.COLUMN_CONTENT,
            ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note.COLUMN_CATEGORY_ID,
            ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category.COLUMN_CATEGORY
        )
        return contentResolver.query(
            ToDoContract.Note.CONTENT_URI,
            projection,
            null, null, null, null
        )
        /*
        var allNotes = ""
        contentResolver.query(
            ToDoContract.Note.CONTENT_URI,
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
            ToDoContract.Note.CONTENT_URI,
            ContentValues().apply {
                put(
                    ToDoContract.Note.COLUMN_CONTENT,
                    "Updated new content"
                )
            },
            "${ToDoContract.Note._ID} = ?",
            arrayOf("104")
        )
        Toast.makeText(this, "Entry update: $id", Toast.LENGTH_SHORT).show()
    }

    private fun providerDeleteNote(noteId: Int) {
        // if noteId == -1 -> delete all notes
        val deleteAll = noteId == -1
        val selection = if (deleteAll) null else "${ToDoContract.Note._ID} = ?"
        val args = if (deleteAll) null else arrayOf("$noteId")
        val id = contentResolver.delete(
            ToDoContract.Note.CONTENT_URI,
            selection,
            args
        )
        Toast.makeText(this, "Entry delete: $id", Toast.LENGTH_SHORT).show()
    }

    private fun providerDeleteCategory(categoryId: Int) {
        // if categoryId == -1 -> delete all notes
        val deleteAll = categoryId == -1
        val selection = if (deleteAll) null else "${ToDoContract.Category._ID} = ?"
        val args = if (deleteAll) null else arrayOf("$categoryId")
        val id = contentResolver.delete(
            ToDoContract.Category.CONTENT_URI,
            selection,
            args
        )
        Toast.makeText(this, "Entry delete: $id", Toast.LENGTH_SHORT).show()
    }

    private fun testCreateCategories() {
        for (i in 0 until 10) {
            val contentValues = ContentValues().apply {
                put(ToDoContract.Category.COLUMN_CATEGORY, "Deneme kategori #$i")
            }
            val uri = contentResolver.insert(ToDoContract.Category.CONTENT_URI, contentValues)
            Log.i(TAG, "addCategory: $uri")
        }
    }

    private fun testCreateNotes() {
        for (i in 1 until 100) {
            val contentValues = ContentValues().apply {
                put(ToDoContract.Note.COLUMN_CONTENT, "Hug Someone #$i")
                put(ToDoContract.Note.COLUMN_CREATION_DATE, "15-12-2022")
                put(ToDoContract.Note.COLUMN_END_DATE, "")
                put(ToDoContract.Note.COLUMN_DONE, 0)
                put(ToDoContract.Note.COLUMN_CATEGORY_ID, (i) % 10 + 1)
            }
            val uri = contentResolver.insert(ToDoContract.Note.CONTENT_URI, contentValues)
            Log.i(TAG, "addNote: $uri")
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

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     *
     * This will always be called from the process's main thread.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (id == 100){
            val projection = arrayOf(
                ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note._ID,
                ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note.COLUMN_CONTENT,
                ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note.COLUMN_CATEGORY_ID,
                ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category.COLUMN_CATEGORY
            )
            return CursorLoader(
                this,
                ToDoContract.Note.CONTENT_URI,
                projection,
                null, null, null
            )
        }
        return CursorLoader(this)
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is *not* allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See [ FragmentManager.openTransaction()][androidx.fragment.app.FragmentManager.beginTransaction] for further discussion on this.
     *
     *
     * This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     *
     *  *
     *
     *The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a [android.database.Cursor]
     * and you place it in a [android.widget.CursorAdapter], use
     * the [android.widget.CursorAdapter.CursorAdapter] constructor *without* passing
     * in either [android.widget.CursorAdapter.FLAG_AUTO_REQUERY]
     * or [android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER]
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     *  *  The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a [android.database.Cursor] from a [android.content.CursorLoader],
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * [android.widget.CursorAdapter], you should use the
     * [android.widget.CursorAdapter.swapCursor]
     * method so that the old Cursor is not closed.
     *
     *
     *
     * This will always be called from the process's main thread.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        cursor = data!!
        listViewNotesAdapter.swapCursor(data)
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     *
     * This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor>) {
        listViewNotesAdapter.swapCursor(null)
    }

}

/*  SQLite operations without Content Provider

    private fun createMockNotes() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.writableDatabase

        // method 1 not recommended
        val insertQuery = "INSERT INTO ${ToDoContract.Note.TABLE_NAME}(" +
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