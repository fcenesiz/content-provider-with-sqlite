package com.fcenesiz.content_provider

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.fcenesiz.content_provider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.buttonAdd.setOnClickListener { addPerson() }
        binding.buttonFind.setOnClickListener { showPerson() }
        binding.buttonRemove.setOnClickListener { removePerson() }
        binding.buttonShowAllPersons.setOnClickListener { showAllPersons() }

        showAllPersons()
    }

    fun addPerson() {
        val name = binding.etNameToAdd.text.toString()
        val values = ContentValues().apply {
            put("name", name)
        }

        val uri = contentResolver.insert(PersonProvider.CONTENT_URI, values)
        // Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()
        showAllPersons()
    }

    fun showPerson() {
        val selection = "id = ?"
        val selectionArgs = arrayOf(binding.etIdToShow.text.toString())
        val list = show(selection, selectionArgs)
        initListView(list)
    }

    fun removePerson() {
        val removeId = binding.etNameToRemove.text.toString()
        contentResolver.delete(PersonProvider.CONTENT_URI, "id = ?", arrayOf(removeId))
        showAllPersons()
    }

    fun showAllPersons() {
        val list = show(null, null)
        initListView(list)
    }

    private fun show(selection: String?, selectionArgs : Array<String>?): List<String> {

        val list = mutableListOf<String>()
        val projection = arrayOf("id", "name")
        val cursor = contentResolver.query(
            PersonProvider.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null,
        )?.let {

            while (it.moveToNext()) {
                val indexId = it.getColumnIndex("id")
                val indexName = it.getColumnIndex("name")
                val id = if (indexId >= 0) it.getString(indexId) else ""
                val name = if (indexName >= 0) it.getString(indexName) else ""
                list.add("$id: $name\n")
            }
            it.close()
        }

        return list
    }

    private fun initListView(list : List<String>){
        val listViewAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        binding.listViewPersons.adapter = listViewAdapter
    }
}