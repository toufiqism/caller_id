package com.sol.callidentifier.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sol.callidentifier.R
import com.sol.callidentifier.data.model.Contact
import com.sol.callidentifier.ui.ContactDetailActivity

class ContactAdapter(
    private val onItemClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContactViewHolder(
        itemView: View,
        private val onItemClick: (Contact) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.contactNameTextView)
        private val numberTextView: TextView = itemView.findViewById(R.id.contactNumberTextView)

        fun bind(contact: Contact) {
            nameTextView.text = contact.name
            numberTextView.text = contact.phoneNumber

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ContactDetailActivity::class.java).apply {
                    putExtra(ContactDetailActivity.EXTRA_CONTACT, contact)
                }
                context.startActivity(intent)
            }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
} 