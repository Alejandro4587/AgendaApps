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
import com.example.agenda.model.repository.ContactFileRepository
import com.example.agenda.ui.viewmodel.ContactFileViewModel


@Composable
fun MainScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Navigation(innerPadding)
    }
}

@Composable
fun Navigation(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = ContactFileRepository(context)

    val viewModel: ContactFileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ContactFileViewModel(repository) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(innerPadding)
    ) {

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add") },
                onEditClick = { id -> navController.navigate("edit/$id") }
            )
        }

        composable("add") {
            AddContact(
                navController = navController,
                viewModel = viewModel,
                innerPadding = innerPadding
            )
        }

        composable(
            route = "edit/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("contactId") ?: 0
            EditContact(
                navController = navController,
                viewModel = viewModel,
                innerPadding = innerPadding,
                id = id
            )
        }
    }
}