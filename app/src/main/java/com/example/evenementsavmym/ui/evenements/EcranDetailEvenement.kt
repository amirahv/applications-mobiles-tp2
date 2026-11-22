package com.example.evenementsavmym.ui.evenements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.evenementsavmym.R
import com.example.evenementsavmym.modele.Evenement
import kotlinx.coroutines.launch
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranDetailEvenement(
    idEvenement: String,
    onRetour: () -> Unit,
    modifier: Modifier = Modifier,
    evenementsViewModel: EvenementsViewModel = viewModel()
) {
    // Conserve le même Flow tant que l'identifiant reste identique.
    val fluxDetail = remember(idEvenement, evenementsViewModel) {
        evenementsViewModel.observerDetail(idEvenement)
    }

    // L'écran observe ici les états transmis par le ViewModel.
    val etat by fluxDetail.collectAsState(
        initial = EtatDetailEvenement()
    )

    val evenement = etat.evenement
    val gestionnaireLiens = LocalUriHandler.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val erreurLien = stringResource(R.string.detail_erreur_billetterie)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.detail_titre))
                },
                navigationIcon = {
                    TextButton(onClick = onRetour) {
                        Text(
                            text = stringResource(R.string.detail_retour),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbar)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                etat.chargement -> {
                    CircularProgressIndicator()
                }

                etat.erreur -> {
                    Text(
                        text = stringResource(R.string.detail_erreur),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                evenement == null -> {
                    Text(
                        text = stringResource(R.string.detail_introuvable),
                        modifier = Modifier.padding(24.dp)
                    )
                }

                else -> {
                    ContenuDetailEvenement(
                        evenement = evenement,
                        onOuvrirBilletterie = { url ->
                            try {
                                gestionnaireLiens.openUri(url)
                            } catch (e: IllegalArgumentException) {
                                scope.launch {
                                    snackbar.showSnackbar(erreurLien)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenuDetailEvenement(
    evenement: Evenement,
    onOuvrirBilletterie: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val formatNombre = remember(locale) {
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    // Présente les montants disponibles sans répéter un prix identique.
    val prix = listOfNotNull(evenement.prixMin, evenement.prixMax)
        .distinct()
        .joinToString(" – ") { formatNombre.format(it) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = evenement.nom,
            style = MaterialTheme.typography.headlineSmall
        )

        AsyncImage(
            model = evenement.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChampDetail(
                    stringResource(R.string.detail_date),
                    evenement.dateLocale
                )

                ChampDetail(
                    stringResource(R.string.detail_heure),
                    evenement.heureLocale?.take(5)
                )

                ChampDetail(
                    stringResource(R.string.detail_statut),
                    texteStatut(evenement.statut)
                )

                ChampDetail(
                    stringResource(R.string.detail_categorie),
                    evenement.categorie
                )

                ChampDetail(
                    stringResource(R.string.detail_genre),
                    evenement.genre
                )

                ChampDetail(
                    stringResource(R.string.detail_salle),
                    evenement.salle
                )

                ChampDetail(
                    stringResource(R.string.detail_adresse),
                    evenement.adresse
                )

                ChampDetail(
                    stringResource(R.string.detail_ville),
                    evenement.ville
                )

                ChampDetail(
                    stringResource(R.string.detail_pays),
                    evenement.pays
                )

                if (prix.isNotBlank()) {
                    ChampDetail(
                        stringResource(R.string.detail_prix),
                        "$prix ${evenement.devise.orEmpty()}".trim()
                    )
                }

                ChampDetail(
                    stringResource(R.string.detail_debut_vente),
                    evenement.dateDebutVenteUtc
                )

                if (!evenement.informations.isNullOrBlank()) {
                    ChampDetail(
                        stringResource(R.string.detail_informations),
                        evenement.informations
                    )
                }
            }
        }

        val url = evenement.billetterieUrl

        if (!url.isNullOrBlank()) {
            Button(
                onClick = { onOuvrirBilletterie(url) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.detail_billetterie))
            }
        }
    }
}

@Composable
private fun ChampDetail(
    libelle: String,
    valeur: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = libelle,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = valeur?.takeIf { it.isNotBlank() }
                ?: stringResource(R.string.detail_non_disponible),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun texteStatut(code: String?): String {
    val ressource = when (code) {
        "onsale" -> R.string.detail_en_vente
        "offsale" -> R.string.detail_hors_vente
        "canceled" -> R.string.detail_annule
        "postponed" -> R.string.detail_reporte
        "rescheduled" -> R.string.detail_reprogramme
        else -> R.string.detail_non_disponible
    }

    return stringResource(ressource)
}