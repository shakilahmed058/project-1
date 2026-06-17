package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppViewModel
import com.example.ui.screens.MainScreen
import com.example.ui.theme.CholoBdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup systems edge bleeding
        enableEdgeToEdge()
        
        setContent {
            val viewModel: AppViewModel = viewModel()
            
            CholoBdTheme(darkTheme = viewModel.darkThemeOverride) {
                MainScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
