package com.example.agenda.model.repository

import android.content.Context
import com.example.agenda.model.data.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


class ContactFileRepository(private val context: Context) {

    private val fileName = "agenda.csv"

    private fun getFile(): File = File(context.filesDir, fileName)


    suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val file = getFile()
        if (!file.exists()) return@withContext emptyList()

        val lista = mutableListOf<Contact>()
        try {
            file.readLines().forEach { line ->
                val parts = line.split(",")
                if (parts.size >= 3) {
                    try {
                        val id = parts[0].trim().toInt()
                        val name = parts[1].trim()
                        val phone = parts[2].trim()
                        lista.add(Contact(id, name, phone))
                    } catch (e: NumberFormatException) {
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext lista
    }


    suspend fun addContact(contact: Contact) = withContext(Dispatchers.IO) {
        val file = getFile()
        file.appendText("${contact.id},${contact.name},${contact.phoneNumber}\n")
    }

    suspend fun updateContact(updatedContact: Contact) = withContext(Dispatchers.IO) {
        val currentList = getAllContacts().toMutableList()
        var index = -1

        for (i in currentList.indices) {
            if (currentList[i].id == updatedContact.id) {
                index = i
                break
            }
        }

        if (index != -1) {
            currentList[index] = updatedContact
            rewriteFile(currentList)
        }
    }


    suspend fun deleteContact(id: Int) = withContext(Dispatchers.IO) {
        val currentList = getAllContacts()
        val newList = mutableListOf<Contact>()

        for (contact in currentList) {
            if (contact.id != id) {
                newList.add(contact)
            }
        }
        rewriteFile(newList)
    }
    private fun rewriteFile(contacts: List<Contact>) {
        val file = getFile()
        file.writeText("")
        for (contact in contacts) {
            file.appendText("${contact.id},${contact.name},${contact.phoneNumber}\n")
        }
    }
}