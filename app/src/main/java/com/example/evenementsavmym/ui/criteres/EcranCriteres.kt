package com.example.evenementsavmym.ui.criteres

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.evenementsavmym.modele.CriteresRecherche

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranCriteres(
    viewModel: CriteresViewModel,
    onDeconnexion: () -> Unit
) {

    val etat by viewModel.etat.collectAsState()

    var pays by remember {
        mutableStateOf("CA")
    }

    var ville by remember {
        mutableStateOf("")
    }

    var categorie by remember {
        mutableStateOf("")
    }

    var langue by remember {
        mutableStateOf("*")
    }

    var dateDebut by remember {
        mutableStateOf("")
    }

    var dateFin by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        viewModel.chargerCriteres()
    }

    LaunchedEffect(etat.criteres) {
        pays = etat.criteres.codePays
        ville = etat.criteres.ville ?: ""
        categorie = etat.criteres.categorie ?: ""
        langue = etat.criteres.langue
        dateDebut = etat.criteres.dateDebutUtc ?: ""
        dateFin = etat.criteres.dateFinUtc ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Critères de recherche")
                },
                actions = {
                    TextButton(
                        onClick = onDeconnexion
                    ) {
                        Text("Déconnexion")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            OutlinedTextField(
                value = pays,
                onValueChange = { pays = it.uppercase() },
                label = { Text("Code du pays") },
                supportingText = {
                    Text("Exemple : CA")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ville,
                onValueChange = { ville = it },
                label = { Text("Ville") },
                placeholder = { Text("Montréal") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Catégorie")

            var categorieOuverte by remember {
                mutableStateOf(false)
            }

            Box {

                OutlinedButton(
                    onClick = {
                        categorieOuverte = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (categorie.isBlank()) {
                            "Toutes les catégories"
                        } else {
                            categorie
                        }
                    )
                }

                DropdownMenu(
                    expanded = categorieOuverte,
                    onDismissRequest = {
                        categorieOuverte = false
                    }
                ) {

                    listOf(
                        "Toutes les catégories" to "",
                        "Musique" to "Music",
                        "Sports" to "Sports",
                        "Arts et théâtre" to "Arts & Theatre",
                        "Famille" to "Family",
                        "Autres" to "Miscellaneous"
                    ).forEach { (libelle, valeur) ->

                        DropdownMenuItem(
                            text = { Text(libelle) },
                            onClick = {
                                categorie = valeur
                                categorieOuverte = false
                            }
                        )
                    }
                }
            }

            Text("Langue des résultats")

            var langueOuverte by remember {
                mutableStateOf(false)
            }

            Box {

                OutlinedButton(
                    onClick = {
                        langueOuverte = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when (langue) {
                            "fr" -> "Français"
                            "en" -> "English"
                            else -> "Toutes les langues"
                        }
                    )
                }

                DropdownMenu(
                    expanded = langueOuverte,
                    onDismissRequest = {
                        langueOuverte = false
                    }
                ) {

                    listOf(
                        "Toutes les langues" to "*",
                        "Français" to "fr",
                        "English" to "en"
                    ).forEach { (libelle, valeur) ->

                        DropdownMenuItem(
                            text = { Text(libelle) },
                            onClick = {
                                langue = valeur
                                langueOuverte = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = dateDebut,
                onValueChange = { dateDebut = it },
                label = { Text("Date de début UTC") },
                placeholder = {
                    Text("2026-12-01T00:00:00Z")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateFin,
                onValueChange = { dateFin = it },
                label = { Text("Date de fin UTC") },
                placeholder = {
                    Text("2026-12-31T23:59:59Z")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {

                    viewModel.modifierCriteres(
                        CriteresRecherche(
                            codePays = pays.trim(),
                            categorie = categorie.ifBlank { null },
                            langue = langue,
                            ville = ville.trim().ifBlank { null },
                            dateDebutUtc = dateDebut.trim().ifBlank { null },
                            dateFinUtc = dateFin.trim().ifBlank { null }
                        )
                    )

                    viewModel.sauvegarderCriteres()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !etat.chargement
            ) {
                Text("Enregistrer les critères")
            }

            if (etat.chargement) {
                CircularProgressIndicator()
            }

            if (etat.sauvegardeReussie) {
                Text(
                    text = "Critères enregistrés avec succès.",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            etat.erreur?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}