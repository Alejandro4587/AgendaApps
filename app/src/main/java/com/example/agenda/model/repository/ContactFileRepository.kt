package com.example.agenda.model.repository

import android.content.Context
import com.example.agenda.model.data.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File



class ContactFileRepository(private val context: Context) {

    private val fileName = "agenda.csv"

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    // LISTAR: Lee línea por línea con un bucle
    suspend fun getAllContacts(): List<Contact> {
        return withContext(Dispatchers.IO) {
            val file = getFile()

            // Si no existe el archivo, devolvemos lista vacía
            if (!file.exists()) {
                mutableListOf<Contact>()
            } else {
                val lista = mutableListOf<Contact>()
                try {
                    val lines = file.readLines()

                    for (line in lines) {
                        val parts = line.split(",")
                        if (parts.size >= 3) {
                            val id = parts[0].toInt()
                            val name = parts[1]
                            val phone = parts[2]

                            val contact = Contact(id, name, phone)
                            lista.add(contact)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                lista
            }
        }
    }

    // AÑADIR: Escribe al final del archivo
    suspend fun addContact(contact: Contact) {
        withContext(Dispatchers.IO) {
            val file = getFile()
            val linea = "${contact.id},${contact.name},${contact.phoneNumber}\n"
            file.appendText(linea)
        }
    }

    // EDITAR: Lee y modifica en memoria, borra archivo y escribe  de nuevo
    suspend fun updateContact(updatedContact: Contact) {
        withContext(Dispatchers.IO) {
            val listaActual = getAllContacts() //
            val listaNueva = mutableListOf<Contact>()

            // 2. Recorremos buscando el que hay que cambiar
            for (contact in listaActual) {
                if (contact.id == updatedContact.id) {
                    listaNueva.add(updatedContact)
                } else {
                    listaNueva.add(contact)
                }
            }

            // 3. Guardamos la lista nueva en el archivo
            guardarListaEnArchivo(listaNueva)
        }
    }

    // ELIMINAR: Lee y filtra el que no queremos, borra archivo y escribe el resto
    suspend fun deleteContact(id: Int) {
        withContext(Dispatchers.IO) {
            val listaActual = getAllContacts()
            val listaNueva = mutableListOf<Contact>()

            for (contact in listaActual) {
                if (contact.id != id) {
                    listaNueva.add(contact)
                }
            }

            guardarListaEnArchivo(listaNueva)
        }
    }

    private fun guardarListaEnArchivo(contacts: List<Contact>) {
        val file = getFile()
        file.writeText("")

        for (contact in contacts) {
            val linea = "${contact.id},${contact.name},${contact.phoneNumber}\n"
            file.appendText(linea)
        }
    }
}