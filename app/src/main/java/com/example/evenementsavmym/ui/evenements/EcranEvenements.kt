package com.example.evenementsavmym.ui.evenements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evenementsavmym.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranEvenements(
    onEvenementClique: (String) -> Unit,
    modifier: Modifier = Modifier,
    evenementsViewModel: EvenementsViewModel = viewModel()
) {
    val evenements by evenementsViewModel.evenements.collectAsState()
    val chargement by evenementsViewModel.chargement.collectAsState()
    val erreurChargement by
    evenementsViewModel.erreurChargement.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.titre_evenements))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (!chargement) {
                        evenementsViewModel.chargerEvenements()
                    }
                }
            ) {
                Text(
                    stringResource(
                        if (chargement) {
                            R.string.chargement_evenements
                        } else {
                            R.string.rafraichir_evenements
                        }
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (chargement) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (erreurChargement) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Text(
                        text = stringResource(
                            R.string.erreur_chargement_evenements
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (evenements.isEmpty() && !chargement && !erreurChargement) {
                    Text(
                        text = stringResource(R.string.aucun_evenement),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }

                ListeEvenements(
                    evenements = evenements,
                    onEvenementClique = onEvenementClique,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}