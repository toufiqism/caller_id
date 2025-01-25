package com.sol.callidentifier.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.sol.callidentifier.R
import com.sol.callidentifier.data.model.Contact
import com.sol.callidentifier.ui.viewmodel.ContactViewModel

class ContactDetailActivity : AppCompatActivity() {
    private lateinit var contactViewModel: ContactViewModel

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        contactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]

        val contact = intent.getParcelableExtra<Contact>(EXTRA_CONTACT)
        contact?.let { displayContactDetails(it) }
    }

    private fun displayContactDetails(contact: Contact) {
        val nameTextView: TextView = findViewById(R.id.contactDetailNameTextView)
        val numberTextView: TextView = findViewById(R.id.contactDetailNumberTextView)
        val blockButton: Button = findViewById(R.id.contactBlockButton)

        nameTextView.text = contact.name
        numberTextView.text = contact.phoneNumber

        updateBlockButtonText(contact, blockButton)

        blockButton.setOnClickListener {
            contactViewModel.toggleBlockStatus(contact)
            // Refresh the button text after blocking/unblocking
            contactViewModel.allContacts.observe(this) { contacts ->
                val updatedContact = contacts.find { it.id == contact.id }
                updatedContact?.let { updateBlockButtonText(it, blockButton) }
            }
        }
    }

    private fun updateBlockButtonText(contact: Contact, blockButton: Button) {
        blockButton.text = if (contact.isBlocked) "Unblock" else "Block"
    }
} 