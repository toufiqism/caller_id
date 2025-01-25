package com.sol.callidentifier.data.service

import android.content.Context
import android.util.Log
import com.sol.callidentifier.data.local.ContactDatabase
import com.sol.callidentifier.data.model.Contact

class CallerIdentificationService(private val context: Context) {
    private val contactDao = ContactDatabase.getDatabase(context).contactDao()

    suspend fun identifyCaller(phoneNumber: String): CallerIdentificationResult {
        // Remove any non-digit characters from the phone number
        val cleanedNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        Log.d("CallerID", "Identifying caller for number: $cleanedNumber")

        // First, check if the number is in contacts
        val contact = findContactByNumber(cleanedNumber)

        return when {
            contact != null -> {
                Log.d("CallerID", "Contact found: ${contact.name}")
                if (contact.isBlocked) {
                    CallerIdentificationResult.Blocked(contact)
                } else {
                    CallerIdentificationResult.Identified(contact)
                }
            }
            isSpamNumber(cleanedNumber) -> {
                Log.d("CallerID", "Spam number detected")
                CallerIdentificationResult.PotentialSpam
            }
            else -> {
                Log.d("CallerID", "Unknown number")
                CallerIdentificationResult.Unknown
            }
        }
    }

    private suspend fun findContactByNumber(phoneNumber: String): Contact? {
        val contacts = contactDao.getAllContacts().value ?: emptyList()
        Log.d("CallerID", "Total contacts: ${contacts.size}")
        
        return contacts.find { 
            it.phoneNumber.replace(Regex("[^0-9]"), "") == phoneNumber 
        }
    }

    private fun isSpamNumber(phoneNumber: String): Boolean {
        val spamNumbers = listOf(
            "18002223333", // Example spam number
            "19998887777"  // Another example spam number
        )
        return spamNumbers.contains(phoneNumber)
    }
}

// Sealed class to represent different caller identification scenarios
sealed class CallerIdentificationResult {
    data class Identified(val contact: Contact) : CallerIdentificationResult()
    data class Blocked(val contact: Contact) : CallerIdentificationResult()
    object PotentialSpam : CallerIdentificationResult()
    object Unknown : CallerIdentificationResult()
} 