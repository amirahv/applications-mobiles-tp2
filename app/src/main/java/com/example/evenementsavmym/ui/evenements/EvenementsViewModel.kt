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
                        val lieu = evenementApi.contenu?.lieux?.firstOrNull()
                        val classification = evenementApi.classifications.firstOrNull()
                        val prix = evenementApi.prix.firstOrNull()
                        // Regroupe les informations et les remarques disponibles.
                        val informations = listOfNotNull(
                            evenementApi.informations,
                            evenementApi.remarques
                        )
                            .filter { it.isNotBlank() }
                            // les sépare par une ligne vide
                            .joinToString("\n\n")
                            // retourne null si aucun texte ne reste
                            .takeIf { it.isNotBlank() }


                        Evenement(
                            id = evenementApi.id,
                            nom = evenementApi.nom,
                            dateLocale = evenementApi.dates?.debut?.dateLocale,
                            salle = lieu?.nom,
                            ville = lieu?.ville?.nom,
                            imageUrl = evenementApi.images.firstOrNull()?.url,
                            heureLocale = evenementApi.dates?.debut?.heureLocale,
                            statut = evenementApi.dates?.statut?.code,
                            categorie = classification?.segment?.nom,
                            genre = classification?.genre?.nom,
                            adresse = lieu?.adresse?.ligne1,
                            pays = lieu?.pays?.nom,
                            prixMin = prix?.minimum,
                            prixMax = prix?.maximum,
                            devise = prix?.devise,
                            dateDebutVenteUtc = evenementApi.ventes?.ventePublique?.dateDebutUtc,
                            informations = informations,
                            billetterieUrl = evenementApi.billetterieUrl
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