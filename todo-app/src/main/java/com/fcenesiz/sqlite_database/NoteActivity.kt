package com.fcenesiz.sqlite_database

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import com.fcenesiz.sqlite_database.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {

    lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        intent.let {
            val noteContent = intent.getStringExtra("note_content")
            noteContent.let {
                binding.editTextTextPersonName.setText(noteContent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}