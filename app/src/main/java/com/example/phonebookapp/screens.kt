package com.example.phonebookapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Phonebook") },
                actions = {
                    IconButton(onClick = { navController.navigate("addContact") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact")
                    }
                }
            )
        }
    ) { padding ->
        val contacts by viewModel.contacts.observeAsState(initial = emptyList())
        LazyColumn(contentPadding = padding) {
            items(contacts) { contact ->
                ContactItem(contact = contact, viewModel = viewModel, navController = navController)
                Divider()
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: ContactModel,
    viewModel: ContactViewModel,
    navController: NavController
) {
    val painter = rememberImagePainter(data = "https://api.multiavatar.com/${contact.avatarKey}.png")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = "Contact Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { navController.navigate("contactDetail/${contact.id}") }
        )
        Spacer(modifier = Modifier.weight(0.03f))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate("contactDetail/${contact.id}") }
        ) {
            Text(text = "${contact.name} ${contact.surname}", style = MaterialTheme.typography.titleMedium)
            Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = { viewModel.deleteContactById(contact.id) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Contact")
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(viewModel: ContactViewModel, navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<Bitmap?>(null) }

    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        Button(onClick = {
            val avatarKey = UUID.randomUUID().toString()

            viewModel.getAvatar(avatarKey).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        avatar = BitmapFactory.decodeStream(response.body()?.byteStream())

                        viewModel.insertContact(ContactModel(
                            name = name,
                            surname = surname,
                            phoneNumber = phoneNumber,
                            email = email,
                            address = address,
                            avatarKey = avatarKey
                        ))
                        navController.popBackStack()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }
            })
        }) {
            Text("Save Contact")
        }

        avatar?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "Avatar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contactId: Int,
    navController: NavHostController,
    viewModel: ContactViewModel,
    context: Context,
) {
    val contact = viewModel.contacts.value?.find { it.id == contactId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Contact Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            if (contact != null) {
                Text(text = "Name: ${contact.name} ${contact.surname}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Phone: ${contact.phoneNumber}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                contact.email?.let {
                    Text(text = "Email: $it", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                contact.address?.let {
                    Text(text = "Address: $it", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Text(text = "Contact not found", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
