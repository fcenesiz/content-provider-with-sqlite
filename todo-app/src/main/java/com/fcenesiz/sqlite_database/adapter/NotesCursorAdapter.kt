package com.fcenesiz.sqlite_database.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.fcenesiz.sqlite_database.data.ToDoContract
import com.fcenesiz.sqlite_database.databinding.ListviewNoteItemBinding

class NotesCursorAdapter(
    context: Context?,
    cursor: Cursor?,
    autoRequery: Boolean
) : CursorAdapter(context, cursor, autoRequery) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return ListviewNoteItemBinding
            .inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
    }


    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        view?.let {
            val binding = ListviewNoteItemBinding.bind(view)
            cursor?.let {
                val noteColumnIndex = it.getColumnIndex(ToDoContract.Note.COLUMN_CONTENT)
                binding.textView.text = it.getString(noteColumnIndex)
            }
        }
    }
}