package com.example.agenda.ui.viewmodel




import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agenda.model.repository.ContactFileRepository

class AgendaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val repository = ContactFileRepository(context)

        // 2. Devolvemos el ViewModel directamente
        return ContactFileViewModel(repository) as T
    }
}