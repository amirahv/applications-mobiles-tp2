
package com.example.evenementsavmym.ui.criteres

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriteresScreen(
    onRetour: () -> Unit,
    viewModel: CriteresViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Critères de recherche")
                },
                navigationIcon = {
                    TextButton(onClick = onRetour) {
                        Text("Retour")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (state.chargement) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Pays",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = state.criteres.codePays,
                onValueChange = viewModel::modifierPays,
                label = { Text("Code du pays (CA)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Ville",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = state.criteres.ville ?: "",
                onValueChange = viewModel::modifierVille,
                label = { Text("Ville") },
                placeholder = { Text("Montréal") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Catégorie",
                style = MaterialTheme.typography.titleMedium
            )

            var categorieOuverte by remember {
                mutableStateOf(false)
            }

            val categories = listOf(
                "Toutes" to "",
                "Musique" to "Music",
                "Sports" to "Sports",
                "Arts et théâtre" to "Arts & Theatre",
                "Famille" to "Family"
            )

            ExposedDropdownMenuBox(
                expanded = categorieOuverte,
                onExpandedChange = {
                    categorieOuverte = !categorieOuverte
                }
            ) {

                OutlinedTextField(
                    value = categories.firstOrNull {
                        it.second == (state.criteres.categorie ?: "")
                    }?.first ?: "Toutes",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Catégorie") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categorieOuverte
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categorieOuverte,
                    onDismissRequest = {
                        categorieOuverte = false
                    }
                ) {
                    categories.forEach { (label, valeur) ->

                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.modifierCategorie(valeur)
                                categorieOuverte = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Langue",
                style = MaterialTheme.typography.titleMedium
            )

            var langueOuverte by remember {
                mutableStateOf(false)
            }

            ExposedDropdownMenuBox(
                expanded = langueOuverte,
                onExpandedChange = {
                    langueOuverte = !langueOuverte
                }
            ) {

                OutlinedTextField(
                    value = when (state.criteres.langue) {
                        "fr" -> "Français"
                        "en" -> "English"
                        else -> "Toutes"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Langue") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = langueOuverte
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = langueOuverte,
                    onDismissRequest = {
                        langueOuverte = false
                    }
                ) {

                    listOf(
                        "Toutes" to "*",
                        "Français" to "fr",
                        "English" to "en"
                    ).forEach { (label, valeur) ->

                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.modifierLangue(valeur)
                                langueOuverte = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Dates facultatives",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = state.criteres.dateDebutUtc ?: "",
                onValueChange = viewModel::modifierDateDebut,
                label = { Text("Date de début UTC") },
                placeholder = {
                    Text("2026-12-01T00:00:00Z")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.criteres.dateFinUtc ?: "",
                onValueChange = viewModel::modifierDateFin,
                label = { Text("Date de fin UTC") },
                placeholder = {
                    Text("2026-12-31T23:59:59Z")
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.erreur != null) {
                Text(
                    text = state.erreur!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.message != null) {
                Text(
                    text = state.message!!,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = viewModel::sauvegarderCriteres,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.chargement
            ) {
                Text("Enregistrer les critères")
            }

            OutlinedButton(
                onClick = viewModel::chargerCriteres,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.chargement
            ) {
                Text("Recharger les critères")
            }
        }
    }
}
