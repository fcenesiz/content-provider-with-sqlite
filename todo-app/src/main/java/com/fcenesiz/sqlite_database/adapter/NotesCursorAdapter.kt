package com.fcenesiz.sqlite_database.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.fcenesiz.sqlite_database.data.ToDoContract
import com.fcenesiz.sqlite_database.databinding.ListviewItemBinding

class NotesCursorAdapter(
    context: Context?,
    cursor: Cursor?,
    autoRequery : Boolean
) : CursorAdapter(context, cursor, autoRequery) {

    companion object{
        public val TAG: String = NotesCursorAdapter::class.simpleName.toString()
    }

    lateinit var binding: ListviewItemBinding

    /**
     * Makes a new view to hold the data pointed to by cursor.
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is already
     * moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return the newly created view.
     */
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        binding = ListviewItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return binding.root
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     * @param view Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is already
     * moved to the correct position.
     */
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        cursor?.let {
            val noteColumnIndex = it.getColumnIndex(ToDoContract.Note.COLUMN_CONTENT)
            binding.textView.text= it.getString(noteColumnIndex)
        }
    }
}