
package com.example.evenementsavmym.ui.compte

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConnexionScreen(
    onConnexionReussie: () -> Unit,
    viewModel: ConnexionViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "EvenementsAVMYM",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::modifierEmail,
            label = { Text("Adresse courriel") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.motDePasse,
            onValueChange = viewModel::modifierMotDePasse,
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (state.erreur != null) {
            Text(
                text = state.erreur!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                viewModel.connecter(onConnexionReussie)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.chargement
        ) {
            if (state.chargement) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Se connecter")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                viewModel.creerCompte(onConnexionReussie)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.chargement
        ) {
            Text("Créer un compte")
        }
    }
}
