package com.example.evenementsavmym.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavigationEvenementsViewModel : ViewModel() {

    var idEvenementSelectionne by mutableStateOf("")
}