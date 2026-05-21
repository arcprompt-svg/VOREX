package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.VorexDatabase
import com.example.data.VorexRepository
import com.example.ui.VorexApp
import com.example.ui.VorexViewModel
import com.example.ui.VorexViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SharedPreferences and Room Local Storage persistence
        val database = VorexDatabase.getDatabase(applicationContext)
        val repository = VorexRepository(database.handoffDao())
        val sharedPreferences = getSharedPreferences("vorex_prefs", android.content.Context.MODE_PRIVATE)

        // Create the active multi-agent ViewModel
        val viewModel = ViewModelProvider(
            this,
            VorexViewModelFactory(repository, sharedPreferences)
        )[VorexViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = com.example.ui.theme.ElegantBackground
                ) { innerPadding ->
                    VorexApp(viewModel = viewModel)
                }
            }
        }
    }
}
