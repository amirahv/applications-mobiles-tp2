package com.example.evenementsavmym.ui.evenements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evenementsavmym.R

@Composable
fun EcranEvenements(
    onEvenementClique: (String) -> Unit,
    modifier: Modifier = Modifier,
    evenementsViewModel: EvenementsViewModel = viewModel()
) {
    // Observe les données du ViewModel pour mettre l'écran à jour.
    val evenements by evenementsViewModel.evenements.collectAsState()
    val chargement by evenementsViewModel.chargement.collectAsState()
    val erreurChargement by
    evenementsViewModel.erreurChargement.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { evenementsViewModel.chargerEvenements() },
            enabled = !chargement
        ) {
            Text(stringResource(R.string.rafraichir_evenements))
        }

        if (chargement) {
            // pour afficher un cercle animé pour indiquer que le chargement est en cours.
            CircularProgressIndicator()
        }

        if (erreurChargement) {
            Text(
                text = stringResource(R.string.erreur_chargement_evenements),
                color = MaterialTheme.colorScheme.error
            )
        }

        if (evenements.isEmpty() && !chargement && !erreurChargement) {
            Text(stringResource(R.string.aucun_evenement))
        }

        ListeEvenements(
            evenements = evenements,
            onEvenementClique = onEvenementClique,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}