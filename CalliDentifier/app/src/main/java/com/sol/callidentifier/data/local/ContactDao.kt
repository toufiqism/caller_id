package com.sol.callidentifier.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sol.callidentifier.data.model.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :searchQuery || '%' OR phoneNumber LIKE '%' || :searchQuery || '%'")
    fun searchContacts(searchQuery: String): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(contacts: List<Contact>)

    @Query("UPDATE contacts SET isBlocked = :blocked WHERE id = :contactId")
    fun updateBlockStatus(contactId: Long, blocked: Boolean)

    @Query("SELECT * FROM contacts WHERE isBlocked = 1")
    fun getBlockedContacts(): LiveData<List<Contact>>
} 