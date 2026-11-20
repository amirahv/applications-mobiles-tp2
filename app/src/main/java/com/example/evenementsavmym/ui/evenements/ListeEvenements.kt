package com.example.evenementsavmym.ui.evenements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.evenementsavmym.modele.Evenement
import com.example.evenementsavmym.ui.theme.EvenementsAVMYMTheme

@Composable
fun ListeEvenements(
    evenements: List<Evenement>,
    onEvenementClique: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = evenements,
            key = { it.id }
        ) { evenement ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvenementClique(evenement.id)
                    }
                    .padding(16.dp)
            ) {
                Text(text = evenement.nom)
                Text(text = evenement.dateLocale ?: "Date à confirmer")
                Text(text = evenement.salle ?: "Salle à confirmer")
                Text(text = evenement.ville ?: "Ville à confirmer")
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