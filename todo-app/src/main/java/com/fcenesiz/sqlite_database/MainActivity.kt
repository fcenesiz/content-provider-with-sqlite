package com.fcenesiz.sqlite_database

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.fcenesiz.sqlite_database.data.DatabaseHelper
import com.fcenesiz.sqlite_database.data.ToDoContract
import com.fcenesiz.sqlite_database.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        public val TAG: String = MainActivity::class.simpleName.toString()
    }

    lateinit var binding: ActivityMainBinding

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
        val notes = resources.getStringArray(R.array.notes)
        val listViewAdapter =
            ArrayAdapter<String>(this, R.layout.listview_item, R.id.textView, notes)
        binding.layoutContentMain.listViewNotes.adapter = listViewAdapter
        binding.layoutContentMain.listViewNotes.setOnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra(
                "note_content",
                notes[position]
            )
            startActivity(intent)
        }
        initDatabase()
    }

    fun initDatabase() {
        providerShowCategory()
        providerShowNote()
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

    private fun providerShowCategory() {
        val projection = arrayOf(
            ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category._ID,
            ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category.COLUMN_CATEGORY
        )
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
    }

    private fun providerShowNote() {
        val projection = arrayOf(
            ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note._ID,
            ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note.COLUMN_CONTENT,
            ToDoContract.Note.TABLE_NAME + "." + ToDoContract.Note.COLUMN_CATEGORY_ID,
            ToDoContract.Category.TABLE_NAME + "." + ToDoContract.Category.COLUMN_CATEGORY
        )
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

    private fun providerDeleteNote(noteId: Int){
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

    private fun providerDeleteCategory(categoryId: Int){
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

    private fun testCreateCategories(){
        for (i in 0 until 10){
            val contentValues = ContentValues().apply {
                put(ToDoContract.Category.COLUMN_CATEGORY, "Deneme kategori #$i")
            }
            val uri = contentResolver.insert(ToDoContract.Category.CONTENT_URI, contentValues)
            Log.i(TAG, "addCategory: $uri")
        }
    }

    private fun testCreateNotes(){
        for (i in 0 until 10){
            val contentValues = ContentValues().apply {
                put(ToDoContract.Note.COLUMN_CONTENT, "Hug Someone #$i")
                put(ToDoContract.Note.COLUMN_CREATION_DATE, "15-12-2022")
                put(ToDoContract.Note.COLUMN_END_DATE, "")
                put(ToDoContract.Note.COLUMN_DONE, 0)
                put(ToDoContract.Note.COLUMN_CATEGORY_ID, i + 1)
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