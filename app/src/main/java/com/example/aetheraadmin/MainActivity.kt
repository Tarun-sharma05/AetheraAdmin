package com.example.aetheraadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aetheraadmin.presentation.Screen.AddProductScreen
import com.example.aetheraadmin.presentation.Screen.addCategoryScreen
import com.example.aetheraadmin.ui.theme.AetheraAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetheraAdminTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddProductScreen()
                }
            }
        }
    }
}
