package com.example.evenementsavmym.ui.compte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnexionScreen(
    viewModel: ConnexionViewModel,
    onConnexionReussie: () -> Unit
) {

    val etat by viewModel.etat.collectAsState()

    var courriel by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }

    var modeInscription by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(etat.connecte) {
        if (etat.connecte) {
            onConnexionReussie()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("EvenementsAVMYM")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = if (modeInscription) {
                    "Créer un compte"
                } else {
                    "Connexion"
                },
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = courriel,
                onValueChange = { courriel = it },
                label = { Text("Adresse courriel") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = motDePasse,
                onValueChange = { motDePasse = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (modeInscription) {
                        viewModel.inscrire(
                            courriel,
                            motDePasse
                        )
                    } else {
                        viewModel.connecter(
                            courriel,
                            motDePasse
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !etat.chargement
            ) {
                Text(
                    if (modeInscription) {
                        "Créer un compte"
                    } else {
                        "Se connecter"
                    }
                )
            }

            TextButton(
                onClick = {
                    modeInscription = !modeInscription
                    viewModel.effacerErreur()
                }
            ) {
                Text(
                    if (modeInscription) {
                        "Déjà inscrit ? Se connecter"
                    } else {
                        "Créer un compte"
                    }
                )
            }

            if (etat.chargement) {
                CircularProgressIndicator()
            }

            etat.erreur?.let { erreur ->
                Text(
                    text = erreur,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}