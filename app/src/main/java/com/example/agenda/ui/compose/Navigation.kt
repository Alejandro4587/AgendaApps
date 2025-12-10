package com.example.agenda.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agenda.model.data.Contact
import com.example.agenda.model.repository.ContactFileRepository
import com.example.agenda.ui.AddContactScreen
import com.example.agenda.ui.EditContactScreen
import com.example.agenda.ui.viewmodel.AgendaViewModelFactory
import com.example.agenda.ui.viewmodel.ContactFileViewModel


@Composable
fun MainScreen() {
    val context = LocalContext.current


    val viewModel: ContactFileViewModel = viewModel(
        factory = AgendaViewModelFactory(context)
    )


    val navController = rememberNavController()


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home", // Nombre de ruta simplificado
            modifier = Modifier.padding(innerPadding) // Aplicamos el padding aquí
        ) {


            composable("home") {

                HomeScreen(
                    viewModel = viewModel,
                    onAddClick = { navController.navigate("add") },
                    onEditClick = { id -> navController.navigate("edit/$id") }
                )
            }


            composable("add") {
                AddContactScreen(
                    onSave = { name, phone ->
                        viewModel.addContact(name, phone)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "edit/{contactId}",
                arguments = listOf(navArgument("contactId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("contactId") ?: 0

                val contact = viewModel.getContactById(id)

                if (contact != null) {
                    EditContactScreen(
                        contact = contact,
                        onSave = { name, phone ->
                            viewModel.editContact(id, name, phone)
                            navController.popBackStack()
                        },
                        onDelete = {
                            viewModel.deleteContact(id)
                            navController.popBackStack()
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}