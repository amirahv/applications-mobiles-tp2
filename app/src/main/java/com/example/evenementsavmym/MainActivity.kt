package com.example.evenementsavmym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.evenementsavmym.ui.evenements.EcranEvenements
import com.example.evenementsavmym.ui.theme.EvenementsAVMYMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvenementsAVMYMTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcranEvenements(
                        onEvenementClique = { idEvenement ->
                            // La navigation vers le détail sera branchée ensuite.
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
