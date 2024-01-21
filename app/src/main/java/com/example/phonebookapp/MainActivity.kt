package com.example.phonebookapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.phonebookapp.ui.theme.PhonebookAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var contactViewModel: ContactViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "contacts"
        ).allowMainThreadQueries().build()

        val contactRepository = ContactRepository(db.contactDao())
        contactViewModel = ContactViewModel(contactRepository)
        setContent {
            PhonebookAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    PhoneBookAppNavigation(contactViewModel,this)
                }
            }
        }
    }

    @Composable
    fun PhoneBookAppNavigation(contactsViewModel: ContactViewModel, context: Context) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "contactsList") {
            composable("contactsList") {
                ContactScreen(
                    viewModel = contactsViewModel,
                    navController = navController,
                )
            }
            composable(
                "contactDetail/{contactId}",
                arguments = listOf(navArgument("contactId") { type = NavType.IntType })
            ) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getInt("contactId") ?: return@composable
                ContactDetailScreen(contactId, navController,contactViewModel, context)
            }
            composable("addContact") {
                AddContactScreen(viewModel = contactsViewModel, navController = navController)
            }
        }
    }
}