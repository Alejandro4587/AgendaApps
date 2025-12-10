package com.example.agenda.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda.model.data.Contact
import com.example.agenda.model.repository.ContactFileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactFileViewModel(private val repository: ContactFileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Contact>>(emptyList())
    val uiState = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = repository.getAllContacts()
        }
    }

    fun addContact(name: String, phone: String) {
        viewModelScope.launch {

            val currentList = _uiState.value

            var maxId = 0
            for (contact in currentList) {
                if (contact.id > maxId) {
                    maxId = contact.id
                }
            }
            val newId = if (currentList.isEmpty()) 1 else maxId + 1


            val newContact = Contact(newId, name, phone)


            repository.addContact(newContact)

            loadContacts()
        }
    }

    fun editContact(id: Int, name: String, phone: String) {
        viewModelScope.launch {
            repository.updateContact(Contact(id, name, phone))
            loadContacts()
        }
    }

    fun deleteContact(id: Int) {
        viewModelScope.launch {
            repository.deleteContact(id)
            loadContacts()
        }
    }

    fun getContactById(id: Int): Contact? {
        return _uiState.value.find { it.id == id }
    }
}