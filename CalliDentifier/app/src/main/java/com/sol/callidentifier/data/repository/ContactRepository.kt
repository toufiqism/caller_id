package com.sol.callidentifier.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sol.callidentifier.data.local.ContactDatabase
import com.sol.callidentifier.data.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepository(context: Context) {
    private val contactDao = ContactDatabase.getDatabase(context).contactDao()

    fun getAllContacts(): LiveData<List<Contact>> = contactDao.getAllContacts()

    fun searchContacts(query: String): LiveData<List<Contact>> = contactDao.searchContacts(query)

    suspend fun insertContacts(contacts: List<Contact>) = withContext(Dispatchers.IO) {
        contactDao.insertContacts(contacts)
    }

    suspend fun toggleBlockStatus(contact: Contact) = withContext(Dispatchers.IO) {
        contactDao.updateBlockStatus(contact.id, !contact.isBlocked)
    }

    fun getBlockedContacts(): LiveData<List<Contact>> = contactDao.getBlockedContacts()
} 