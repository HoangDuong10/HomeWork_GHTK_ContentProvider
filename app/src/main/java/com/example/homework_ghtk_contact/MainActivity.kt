package com.example.homework_ghtk_contact

import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.WRITE_CONTACTS
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework_ghtk_contrac.R
import com.example.homework_ghtk_contrac.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var adapter : PhoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPhone.layoutManager = linearLayoutManager
        adapter = PhoneAdapter()
        adapter.addData(readContacts())
        binding.rvPhone.adapter = adapter
        checkPermissions()
        binding.btnUpdatePhoneNumber.setOnClickListener{
            updateRecyclerView()
            updatePhoneNumbers()
        }


    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(
                    READ_CONTACTS,
                    WRITE_CONTACTS
                ),
                99)
        } else {
            Log.d("Permissions", "Contacts permissions already granted.")
        }
    }


    @SuppressLint("Range")
    fun readContacts(): List<String> {
        val contacts = mutableListOf<String>()
        val contentResolver = this.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            null,
            null,
            null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contacts.add(phoneNumber)
            }
        }
        return contacts
    }

    private fun convertPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("0167") && phoneNumber.startsWith("84167")) {
            "037${phoneNumber.substring(4)}"
        } else {
            phoneNumber
        }
    }

    private fun updateRecyclerView() {
        adapter.clearData()
        val contacts = readContacts()
        val convertedContacts = contacts.map { convertPhoneNumber(it) }
        adapter.addData(convertedContacts)

    }

    @SuppressLint("Range")
    private fun updatePhoneNumbers() {
        val contentResolver = contentResolver
        val uri = ContactsContract.Data.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Data._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val selection = "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            "0167%"
        )

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndex(ContactsContract.Data._ID))
                val oldNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                // Kiểm tra và cập nhật số điện thoại

                if (oldNumber.startsWith("0167")||oldNumber.startsWith("84167")) {
                    val newPhoneNumber = "037" + oldNumber.substring(4)
                    val values = ContentValues().apply {
                        put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
                    }
                    val updateUri = ContentUris.withAppendedId(uri, id)
                    try {
                        val rowsUpdated = contentResolver.update(updateUri, values, null, null)
                        if (rowsUpdated > 0) {
                            Log.d("UpdateContacts", "Successfully updated $rowsUpdated rows")
                        } else {
                            Log.d("UpdateContacts", "No rows updated")
                        }
                    } catch (e: Exception) {
                        Log.e("UpdateContacts", "Error updating contacts", e)
                    }
                }

            }
        }
    }



}