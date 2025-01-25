package com.sol.callidentifier

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.sol.callidentifier.data.service.CallerIdentificationService
import com.sol.callidentifier.data.service.CallerNotificationService
import com.sol.callidentifier.ui.adapter.ContactAdapter
import com.sol.callidentifier.ui.viewmodel.ContactViewModel
import com.sol.callidentifier.utils.ContactFetcher
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var callerIdentificationService: CallerIdentificationService
    private lateinit var callerNotificationService: CallerNotificationService
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var testCallerIdButton: ExtendedFloatingActionButton

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.READ_CONTACTS] == true -> {
                loadContacts()
            }
            permissions[Manifest.permission.POST_NOTIFICATIONS] == true -> {
                // Notification permission granted
            }
            else -> {
                showPermissionRationaleDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        contactRecyclerView = findViewById(R.id.contactRecyclerView)
        searchBar = findViewById(R.id.searchBar)
        searchView = findViewById(R.id.searchView)
        topAppBar = findViewById(R.id.topAppBar)
        testCallerIdButton = findViewById(R.id.testCallerIdButton)

        // Setup toolbar
        setSupportActionBar(topAppBar)

        // Setup RecyclerView
        contactAdapter = ContactAdapter { contact ->
            contactViewModel.toggleBlockStatus(contact)
        }

        contactRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
        }

        // Setup ViewModel
        contactViewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        contactViewModel.allContacts.observe(this) { contacts ->
            contactAdapter.submitList(contacts)
        }

        // Request both permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_CONTACTS))
        }

        // Setup SearchView
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            searchBar.setText(searchView.text)
            false
        }

        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                // Reset to show all contacts when search is closed
                contactViewModel.allContacts.value?.let { 
                    contactAdapter.submitList(it) 
                }
            }
        }

        searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = searchView.text.toString()
            searchContacts(query)
            false
        }

        // Setup Caller ID Test Button
        callerIdentificationService = CallerIdentificationService(this)
        callerNotificationService = CallerNotificationService(this)

        testCallerIdButton.setOnClickListener {
            testCallerIdentification()
        }
    }

    private fun loadContacts() {
        val contactFetcher = ContactFetcher(this)
        val contacts = contactFetcher.fetchContacts()
        contactViewModel.loadContacts(contacts)
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Contacts Permission Needed")
            .setMessage("This app needs access to your contacts to display and manage them.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun searchContacts(query: String) {
        contactViewModel.searchContacts(query).observe(this) { contacts ->
            contactAdapter.submitList(contacts)
        }
    }

    private fun testCallerIdentification() {
        lifecycleScope.launch {
            try {
                // Test with different phone numbers
                val testNumbers = listOf(
                    "18002223333",  // Spam number
                    "19998887777",  // Another spam number
                    "1234567890"    // A random number
                )

                testNumbers.forEach { number ->
                    try {
                        val result = callerIdentificationService.identifyCaller(number)
                        callerNotificationService.showCallerNotification(result)
                        
                        // Add logging to verify each call
                        Log.d("CallerID", "Number: $number, Result: $result")
                    } catch (e: Exception) {
                        Log.e("CallerID", "Error processing number $number", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("CallerID", "Error in testCallerIdentification", e)
                // Optionally show a toast to user
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity, 
                        "Failed to simulate calls: ${e.localizedMessage}", 
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}