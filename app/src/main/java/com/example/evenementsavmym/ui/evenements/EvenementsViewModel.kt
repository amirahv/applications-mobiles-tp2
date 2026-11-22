package com.example.evenementsavmym.ui.evenements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evenementsavmym.donnees.room.BaseEvenements
import com.example.evenementsavmym.donnees.ticketmaster.ApiClient
import com.example.evenementsavmym.modele.Evenement
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EvenementsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = BaseEvenements
        .obtenirInstance(application)
        .evenementDao()

    // La liste affichée provient de Room.
    val evenements: StateFlow<List<Evenement>> = dao.observerTous()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val chargement = MutableStateFlow(false)
    val erreurChargement = MutableStateFlow(false)

    fun chargerEvenements() {
        if (chargement.value) return

        chargement.value = true
        erreurChargement.value = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val reponse = ApiClient.apiService.obtenirEvenements()

                val nouveauxEvenements = reponse.contenu
                    ?.evenements
                    .orEmpty()
                    .map { evenementApi ->
                        val lieu =
                            evenementApi.contenu?.lieux?.firstOrNull()

                        val classification =
                            evenementApi.classifications.firstOrNull()

                        val prix = evenementApi.prix.firstOrNull()

                        val informations = listOfNotNull(
                            evenementApi.informations,
                            evenementApi.remarques
                        )
                            .filter { it.isNotBlank() }
                            .joinToString("\n\n")
                            .takeIf { it.isNotBlank() }

                        Evenement(
                            id = evenementApi.id,
                            nom = evenementApi.nom,
                            dateLocale =
                                evenementApi.dates?.debut?.dateLocale,
                            salle = lieu?.nom,
                            ville = lieu?.ville?.nom,
                            imageUrl =
                                evenementApi.images.firstOrNull()?.url,
                            heureLocale =
                                evenementApi.dates?.debut?.heureLocale,
                            statut = evenementApi.dates?.statut?.code,
                            categorie = classification?.segment?.nom,
                            genre = classification?.genre?.nom,
                            adresse = lieu?.adresse?.ligne1,
                            pays = lieu?.pays?.nom,
                            prixMin = prix?.minimum,
                            prixMax = prix?.maximum,
                            devise = prix?.devise,
                            dateDebutVenteUtc =
                                evenementApi.ventes
                                    ?.ventePublique?.dateDebutUtc,
                            informations = informations,
                            billetterieUrl = evenementApi.billetterieUrl
                        )
                    }

                // Sauvegarde les nouveaux résultats.
                // Le Flow de Room mettra ensuite l'écran à jour.
                dao.remplacerTous(nouveauxEvenements)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Les anciens évènements restent disponibles si l'opération échoue.
                erreurChargement.value = true
            } finally {
                chargement.value = false
            }
        }
    }

    fun observerDetail(idEvenement: String): Flow<EtatDetailEvenement> {
        return dao.observerParId(idEvenement)
            // transforme le résultat en état pour l’écran.
            .map { evenement ->
                EtatDetailEvenement(
                    evenement = evenement,
                    chargement = false
                )
            }
            // transmet un état d’erreur si la lecture échoue. emit() envoie cet état aux observateurs.
            .catch {
                emit(
                    EtatDetailEvenement(
                        chargement = false,
                        erreur = true
                    )
                )
            }
    }
}