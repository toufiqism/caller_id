package com.sol.callidentifier.utils

import android.content.Context
import android.provider.ContactsContract
import com.sol.callidentifier.data.model.Contact

class ContactFetcher(private val context: Context) {
    fun fetchContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex)?.replace(Regex("[\\s-()]"), "") ?: ""

                if (number.isNotBlank()) {
                    contacts.add(
                        Contact(
                            id = id,
                            name = name,
                            phoneNumber = number
                        )
                    )
                }
            }
        }

        return contacts
    }
} 