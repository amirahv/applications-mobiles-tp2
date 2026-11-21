package com.example.evenementsavmym.ui.evenements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evenementsavmym.donnees.ticketmaster.ApiClient
import com.example.evenementsavmym.modele.Evenement
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EvenementsViewModel : ViewModel() {

    val evenements = MutableStateFlow<List<Evenement>>(emptyList())
    val chargement = MutableStateFlow(false)
    val erreurChargement = MutableStateFlow(false)

    fun chargerEvenements() {
        // Empêche plusieurs requêtes simultanées.
        if (chargement.value) return

        chargement.value = true
        erreurChargement.value = false

        viewModelScope.launch {
            try {
                val reponse = ApiClient.apiService.obtenirEvenements()

                // Transforme les données de l'API en objets pour notre écran.
                evenements.value = reponse.contenu
                    ?.evenements
                    // Produit une liste vide si la réponse ne contient aucun évènement.
                    .orEmpty()
                    // Convertit chaque EvenementApi en notre modèle Evenement
                    .map { evenementApi ->
                        // Une salle peut être absente de la réponse.
                        val lieu = evenementApi.contenu?.lieux?.firstOrNull()

                        Evenement(
                            id = evenementApi.id,
                            nom = evenementApi.nom,
                            dateLocale = evenementApi.dates?.debut?.dateLocale,
                            salle = lieu?.nom,
                            ville = lieu?.ville?.nom,
                            imageUrl = evenementApi.images.firstOrNull()?.url
                        )
                    }

            } catch (e: CancellationException) {
                // Laisse Android annuler normalement la coroutine.
                throw e
            } catch (e: Exception) {
                erreurChargement.value = true
            } finally {
                chargement.value = false
            }
        }
    }
}