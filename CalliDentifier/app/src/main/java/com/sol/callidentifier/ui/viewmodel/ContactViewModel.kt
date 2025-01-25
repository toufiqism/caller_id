package com.sol.callidentifier.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sol.callidentifier.data.model.Contact
import com.sol.callidentifier.data.repository.ContactRepository
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ContactRepository = ContactRepository(application)

    val allContacts: LiveData<List<Contact>> = repository.getAllContacts()
    val blockedContacts: LiveData<List<Contact>> = repository.getBlockedContacts()

    fun searchContacts(query: String): LiveData<List<Contact>> = repository.searchContacts(query)

    fun toggleBlockStatus(contact: Contact) {
        viewModelScope.launch {
            repository.toggleBlockStatus(contact)
        }
    }

    fun loadContacts(contacts: List<Contact>) {
        viewModelScope.launch {
            repository.insertContacts(contacts)
        }
    }
} 