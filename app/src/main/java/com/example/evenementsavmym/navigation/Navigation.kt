package com.example.evenementsavmym.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.evenementsavmym.ui.compte.ConnexionScreen
import com.example.evenementsavmym.ui.compte.ConnexionViewModel
import com.example.evenementsavmym.ui.criteres.CriteresViewModel
import com.example.evenementsavmym.ui.criteres.EcranCriteres
import androidx.lifecycle.viewmodel.compose.viewModel

object Routes {
    const val CONNEXION = "connexion"
    const val EVENEMENTS = "evenements"
    const val CRITERES = "criteres"
    const val DETAIL = "detail/{idEvenement}"

    fun detail(idEvenement: String): String {
        return "detail/$idEvenement"
    }
}

@Composable
fun Navigation() {

    val navController = rememberNavController()

    val connexionViewModel: ConnexionViewModel = viewModel()
    val criteresViewModel: CriteresViewModel = viewModel()

    val connexionEtat by connexionViewModel.etat.collectAsState()

    val destinationInitiale = if (connexionEtat.connecte) {
        Routes.EVENEMENTS
    } else {
        Routes.CONNEXION
    }

    NavHost(
        navController = navController,
        startDestination = destinationInitiale
    ) {

        composable(Routes.CONNEXION) {

            ConnexionScreen(
                viewModel = connexionViewModel,
                onConnexionReussie = {
                    navController.navigate(Routes.EVENEMENTS) {
                        popUpTo(Routes.CONNEXION) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.EVENEMENTS) {

            EcranEvenementsTemporaire(
                onOuvrirDetail = { id ->
                    navController.navigate(
                        Routes.detail(id)
                    )
                },
                onOuvrirCriteres = {
                    navController.navigate(Routes.CRITERES)
                }
            )
        }

        composable(Routes.CRITERES) {

            EcranCriteres(
                viewModel = criteresViewModel,
                onDeconnexion = {
                    connexionViewModel.deconnecter()

                    navController.navigate(Routes.CONNEXION) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("idEvenement") {
                    type = NavType.StringType
                }
            )
        ) { entree ->

            val idEvenement = entree.arguments
                ?.getString("idEvenement") ?: ""

            EcranDetailTemporaire(
                idEvenement = idEvenement,
                onRetour = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranEvenementsTemporaire(
    onOuvrirDetail: (String) -> Unit,
    onOuvrirCriteres: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Évènements")
                },
                actions = {
                    TextButton(
                        onClick = onOuvrirCriteres
                    ) {
                        Text("Critères")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Liste temporaire des évènements")

            Button(
                onClick = {
                    onOuvrirDetail("evt-demo-001")
                }
            ) {
                Text("Ouvrir évènement 001")
            }

            Button(
                onClick = {
                    onOuvrirDetail("evt-demo-002")
                }
            ) {
                Text("Ouvrir évènement 002")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranDetailTemporaire(
    idEvenement: String,
    onRetour: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Détail de l'évènement")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Identifiant : $idEvenement",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = onRetour
            ) {
                Text("Retour")
            }
        }
    }
}