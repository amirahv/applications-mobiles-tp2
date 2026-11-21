package com.example.evenementsavmym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.evenementsavmym.ui.evenements.EcranEvenements

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcranEvenements(
                onEvenementClique = { idEvenement ->
                    // La navigation vers le détail sera branchée ensuite.
                }
            )
        }
    }
}
