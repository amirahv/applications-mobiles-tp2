package com.example.evenementsavmym.ui.evenements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.evenementsavmym.R
import com.example.evenementsavmym.modele.Evenement
import com.example.evenementsavmym.ui.theme.EvenementsAVMYMTheme

@Composable
fun ListeEvenements(
    evenements: List<Evenement>,
    onEvenementClique: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        // L'espace du bas permet de défiler au-delà du bouton flottant.
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 112.dp
        )
    ) {
        items(
            items = evenements,
            key = { it.id }
        ) { evenement ->
            Card(
                onClick = { onEvenementClique(evenement.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = evenement.imageUrl,
                        // Le nom est déjà présent dans le texte voisin.
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = evenement.nom,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = evenement.dateLocale
                                ?: stringResource(R.string.date_a_confirmer),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            // take(5) transforme "19:00:00" en "19:00"
                            text = evenement.heureLocale?.take(5)
                                ?: stringResource(R.string.heure_a_confirmer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = evenement.salle
                                ?: stringResource(R.string.salle_a_confirmer),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = evenement.ville
                                ?: stringResource(R.string.ville_a_confirmer),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ApercuListeEvenements() {
    val evenementsFictifs = listOf(
        Evenement(
            id = "evt-demo-001",
            nom = "Concert jazz",
            dateLocale = "2026-12-03",
            salle = "Salle A",
            ville = "Montréal"
        ),
        Evenement(
            id = "evt-demo-002",
            nom = "Match de hockey",
            dateLocale = "2026-12-05",
            salle = "Aréna B",
            ville = "Laval"
        )
    )

    EvenementsAVMYMTheme {
        ListeEvenements(
            evenements = evenementsFictifs,
            onEvenementClique = {}
        )
    }
}