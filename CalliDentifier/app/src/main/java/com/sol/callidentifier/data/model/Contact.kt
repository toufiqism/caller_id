package com.sol.callidentifier.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: Long,
    val name: String,
    val phoneNumber: String,
    val isBlocked: Boolean = false
) : Parcelable 