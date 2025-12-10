package com.example.agenda.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agenda.model.data.Contact
import com.example.agenda.ui.viewmodel.AgendaViewModelFactory
import com.example.agenda.ui.viewmodel.ContactFileViewModel



@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: ContactFileViewModel = viewModel(
        factory = AgendaViewModelFactory(context)
    )
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->

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



@Composable
fun HomeScreen(
    viewModel: ContactFileViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val contacts by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Header("Agenda de Contactos")


        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (contacts.isEmpty()) {

                Text(
                    text = "No hay contactos",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(contacts) { contact ->
                        ContactItem(contact, onClick = { onEditClick(contact.id) })
                    }
                }
            }


            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Esto lo pone a la derecha
                    .padding(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    }
}

@Composable
fun AddContactScreen(onSave: (String, String) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Header("Nuevo Contacto")
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onCancel) { Text("Cancelar") }
                Button(onClick = { if (name.isNotBlank()) onSave(name, phone) }) { Text("Guardar") }
            }
        }
    }
}

@Composable
fun EditContactScreen(
    contact: Contact,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(contact.name) }
    var phone by remember { mutableStateOf(contact.phoneNumber) }

    Column(modifier = Modifier.fillMaxSize()) {
        Header("Editar Contacto")
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onCancel) { Text("Cancelar") }
                Button(onClick = { onSave(name, phone) }) { Text("Guardar") }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar Contacto")
            }
        }
    }
}

@Composable
fun Header(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ContactItem(contact: Contact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = contact.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = contact.phoneNumber, fontSize = 14.sp)
        }
    }
}