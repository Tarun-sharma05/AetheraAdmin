package com.example.aetheraadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aetheraadmin.presentation.navigation.NavGraph
import com.example.aetheraadmin.ui.theme.AetheraAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetheraAdminTheme {
                NavGraph()
            }
        }
    }
}
