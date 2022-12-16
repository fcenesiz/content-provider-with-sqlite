package com.fcenesiz.sqlite_database

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.fcenesiz.sqlite_database.data.MyQueryHandler
import com.fcenesiz.sqlite_database.data.ToDoContract.*
import com.fcenesiz.sqlite_database.databinding.ActivityNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    lateinit var binding: ActivityNoteBinding
    lateinit var categoryAdapter: SimpleCursorAdapter
    lateinit var categoryCursor: Cursor
    lateinit var calender: Calendar
    lateinit var queryHandler: MyQueryHandler
    var isNew: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        queryHandler = MyQueryHandler(contentResolver)
        initializeCursor()
        initializeCategorySpinnerAdapter()
        intent?.let {
            initializeViews(it)
        }

        binding.buttonCreationDate.setOnClickListener { showCalender(binding.textViewCreationDate) }
        binding.buttonEndDate.setOnClickListener { showCalender(binding.textViewEndDate) }
        binding.fabSaveNote.setOnClickListener { save() }
        binding.buttonDeleteNote.setOnClickListener { delete() }

    }

    private fun initializeCursor() {
        val projection = arrayOf(Category._ID, Category.COLUMN_CATEGORY)
        val orderBy = "${Category._ID} ASC"
        contentResolver.query(
            Category.CONTENT_URI,
            projection,
            null, null,
            orderBy
        )?.let {
            categoryCursor = it
        }
    }

    private fun initializeCategorySpinnerAdapter() {
        categoryAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryCursor,
            arrayOf(Category.COLUMN_CATEGORY),
            IntArray(1) { android.R.id.text1 },
            0
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerCategory.adapter = categoryAdapter
    }

    private fun initializeViews(intent: Intent) {
        val id = intent.getStringExtra(Note._ID)
        if (id == null) {
            onInsertNewNote()
            isNew = true
        } else {
            onUpdateNote(intent)
            isNew = false
        }
    }

    private fun onInsertNewNote() {
        binding.buttonDeleteNote.visibility = View.GONE
    }

    private fun onUpdateNote(intent: Intent) {
        val content = intent.getStringExtra(Note.COLUMN_CONTENT)
        val creationDate = intent.getStringExtra(Note.COLUMN_CREATION_DATE)
        val endDate = intent.getStringExtra(Note.COLUMN_END_DATE)
        val done = intent.getStringExtra(Note.COLUMN_DONE)
        val categoryId = intent.getStringExtra(Note.COLUMN_CATEGORY_ID)
        val columnCategory = intent.getStringExtra(Category.COLUMN_CATEGORY)

        binding.editTextNote.setText(content)
        binding.textViewCreationDate.text = creationDate
        binding.textViewEndDate.text = endDate
        binding.checkBoxDone.isChecked = done == "1"
        categoryId?.let { binding.spinnerCategory.setSelection(it.toInt() - 1) }
    }

    private fun save() {
        val values = ContentValues().apply {
            put(Note.COLUMN_CONTENT, binding.editTextNote.text.toString())
            put(Note.COLUMN_CREATION_DATE, binding.textViewCreationDate.text.toString())
            put(Note.COLUMN_END_DATE, binding.textViewEndDate.text.toString())
            put(Note.COLUMN_DONE, if (binding.checkBoxDone.isChecked) 1 else 0)
            put(Note.COLUMN_CATEGORY_ID, binding.spinnerCategory.selectedItemId)
        }
        if (isNew) { // insert
            queryHandler.startInsert(
                1,
                null,
                Note.CONTENT_URI,
                values,
            )
            Toast.makeText(this, "Note is created!", Toast.LENGTH_SHORT).show()
        } else { // update
            val selection = "${Note._ID} = ?"
            val selectionArgs = arrayOf(intent.getStringExtra(Note._ID))
            queryHandler.startUpdate(
                2,
                null,
                Note.CONTENT_URI,
                values,
                selection,
                selectionArgs
            )
            Toast.makeText(this, "Note is updated!", Toast.LENGTH_SHORT).show()
        }
        onBackPressed()
    }

    private fun delete(){
        val selection = "${Note._ID} = ?"
        val selectionArgs = arrayOf(intent.getStringExtra(Note._ID))
        queryHandler.startDelete(
            3,
            null,
            Note.CONTENT_URI,
            selection,
            selectionArgs
        )
        onBackPressed()
    }

    private fun showCalender(textView: TextView) {
        calender = Calendar.getInstance()
        val date = Date(
            calender.get(Calendar.DAY_OF_MONTH),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.YEAR)
        )
        val calenderDialog =
            DatePickerDialog(this, { view, year, month, day ->
                val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr"))

                textView.text = "$day/$month/$year"
            }, date.year, date.month, date.day).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    data class Date(
        val day: Int,
        val month: Int,
        val year: Int
    )
}