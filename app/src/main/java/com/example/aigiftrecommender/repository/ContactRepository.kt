package com.example.aigiftrecommender.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.ContactsContract
import com.example.aigiftrecommender.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface ContactRepository {
    suspend fun getAllContacts(): List<Contact>
    suspend fun getContactDetails(contactId: String): Contact?
}

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ContactRepository {

    @SuppressLint("Range") // Suppressing for brevity, ensure proper handling in production
    override suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contactsList = mutableListOf<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                // Initially, we load just ID and name. Birthday and notes can be loaded on demand.
                contactsList.add(Contact(id, name, null, null))
            }
        }
        return@withContext contactsList
    }

    @SuppressLint("Range")
    override suspend fun getContactDetails(contactId: String): Contact? = withContext(Dispatchers.IO) {
        var contact: Contact? = null
        var name: String? = null
        var birthday: String? = null
        var notes: String? = null

        // Get Name
        val nameCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
            ContactsContract.Contacts._ID + " = ?",
            arrayOf(contactId),
            null
        )
        nameCursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            }
        }

        if (name == null) return@withContext null // Contact not found or no name

        // Get Birthday
        val birthdayCursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE),
            ContactsContract.Data.CONTACT_ID + " = ? AND " +
                    ContactsContract.Data.MIMETYPE + " = ? AND " +
                    ContactsContract.CommonDataKinds.Event.TYPE + " = ?",
            arrayOf(contactId, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString()),
            null
        )
        birthdayCursor?.use {
            if (it.moveToFirst()) {
                birthday = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE))
            }
        }

        // Get Notes
        val noteCursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Note.NOTE),
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
            arrayOf(contactId, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE),
            null
        )
        noteCursor?.use {
            if (it.moveToFirst()) {
                notes = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE))
            }
        }
        
        contact = Contact(contactId, name!!, birthday, notes)
        return@withContext contact
    }
}

