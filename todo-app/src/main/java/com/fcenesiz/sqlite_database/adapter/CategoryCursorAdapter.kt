package com.fcenesiz.sqlite_database.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.fcenesiz.sqlite_database.R
import com.fcenesiz.sqlite_database.data.ToDoContract
import com.fcenesiz.sqlite_database.databinding.ListviewCategoryItemBinding

class CategoryCursorAdapter(context: Context?, c: Cursor?, autoRequery: Boolean) :
    CursorAdapter(context, c, autoRequery) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return ListviewCategoryItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        ).root
    }

    override fun bindView(v: View?, context: Context?, cursor: Cursor?) {
        v?.let {
            val binding = ListviewCategoryItemBinding.bind(v)
            cursor?.let {
                val categoryColumnIndex = it.getColumnIndex(ToDoContract.Category.COLUMN_CATEGORY)
                binding.textViewCategory.text = it.getString(categoryColumnIndex)
            }
        }
    }

}