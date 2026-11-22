package com.example.evenementsavmym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.evenementsavmym.navigation.NavigationEvenements
import com.example.evenementsavmym.ui.theme.EvenementsAVMYMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EvenementsAVMYMTheme {
                NavigationEvenements()
            }
        }
    }
}