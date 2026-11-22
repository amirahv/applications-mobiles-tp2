package com.example.evenementsavmym.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evenementsavmym.ui.evenements.EcranDetailEvenement
import com.example.evenementsavmym.ui.evenements.EcranEvenements
import com.example.evenementsavmym.ui.evenements.EvenementsViewModel

@Composable
fun NavigationEvenements() {
    val navController = rememberNavController()

    // Créés avant le NavHost pour être partagés entre les écrans.
    val navigationViewModel: NavigationEvenementsViewModel = viewModel()
    val evenementsViewModel: EvenementsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "liste"
    ) {
        composable("liste") {
            EcranEvenements(
                onEvenementClique = { idEvenement ->
                    navigationViewModel.idEvenementSelectionne = idEvenement
                    navController.navigate("detail")
                },
                evenementsViewModel = evenementsViewModel
            )
        }

        composable("detail") {
            EcranDetailEvenement(
                idEvenement =
                    navigationViewModel.idEvenementSelectionne,
                onRetour = {
                    navController.navigateUp()
                },
                evenementsViewModel = evenementsViewModel
            )
        }
    }
}