package com.fcenesiz.content_resolver

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import com.fcenesiz.content_provider.databinding.ActivityMainBinding

class MainActivity : RuntimePermissionActivity() {

    companion object private val PERMISSIONS_REQUEST_CODE = 5
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askPermissions()
    }

    fun askPermissions(){
        val askedPermissions : Array<String?> = arrayOf(
            Manifest.permission.READ_CONTACTS
        )
        super.askPermission(askedPermissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun permissionGranted(requestCode: Int) {
        if (requestCode == PERMISSIONS_REQUEST_CODE){
            binding.buttonShowAllMembers.setOnClickListener {
                val members = showAllMembers()
                val listViewAdapter =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, members)
                binding.listViewAllMembers.adapter = listViewAdapter
            }
        }
    }

    fun showAllMembers(): List<String> {

        val allMembers = mutableListOf<String>()

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection: String? = null
        val selectionArgs: Array<String>? = null
        val sortOrder: String? = null

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            selectionArgs,
            sortOrder
        )

        cursor?.let {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val name =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                            .takeIf { index ->
                                index >= 0
                            }?.let { index -> it.getString(index) }
                    val number = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        .takeIf { index ->
                            index >= 0
                        }?.let { index -> it.getString(index) }
                    allMembers.add("$name\t: $number")
                }
                return allMembers
            }
        }

        return emptyList<String>()
    }

}