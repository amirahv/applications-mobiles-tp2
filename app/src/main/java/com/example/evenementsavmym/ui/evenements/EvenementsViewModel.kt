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
import com.example.evenementsavmym.modele.CriteresRecherche
import com.example.evenementsavmym.donnees.ticketmaster.PaginationApi

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

    // Informations sur la page du dernier chargement réussi.
    val pagination = MutableStateFlow<PaginationApi?>(null)

    // Les changements de page conservent la même recherche.
    private var criteresDernierChargement: CriteresRecherche? = null

    private val taillePage = 20

    // Ticketmaster limite la pagination aux 1 000 premiers résultats.
    val nombreMaximumPages = 1000 / taillePage

    // Copie conservée en mémoire pour restaurer l'état précédent.
    private data class SauvegardeEvenements(
        val evenements: List<Evenement>,
        val pagination: PaginationApi?,
        val criteres: CriteresRecherche?
    )

    private var sauvegardePrecedente: SauvegardeEvenements? = null

    private var prochainIdentifiant = 0L

    // Identifie la mise à jour que le Snackbar permettra d'annuler.
    val miseAJourAnnulable = MutableStateFlow<Long?>(null)

    fun chargerEvenements(
        criteres: CriteresRecherche,
        pageDemandee: Int = 0
    ) {
        if (chargement.value) return
        if (pageDemandee !in 0 until nombreMaximumPages) return

        // Une nouvelle demande remplace la possibilité d'annuler l'ancienne.
        miseAJourAnnulable.value = null
        sauvegardePrecedente = null

        chargement.value = true
        erreurChargement.value = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val reponse = ApiClient.apiService.obtenirEvenements(
                    codePays = criteres.codePays
                        .trim()
                        .uppercase()
                        .ifBlank { "CA" },

                    categorie = criteres.categorie
                        ?.trim()
                        ?.takeIf { it.isNotBlank() },

                    langue = criteres.langue
                        .trim()
                        .ifBlank { "*" },

                    ville = criteres.ville
                        ?.trim()
                        ?.takeIf { it.isNotBlank() },

                    dateDebutUtc = criteres.dateDebutUtc
                        ?.trim()
                        ?.takeIf { it.isNotBlank() },

                    dateFinUtc = criteres.dateFinUtc
                        ?.trim()
                        ?.takeIf { it.isNotBlank() },

                    taillePage = taillePage,
                    page = pageDemandee
                )

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

                // Lit directement Room pour conserver les résultats précédents.
                val sauvegarde = SauvegardeEvenements(
                    evenements = dao.obtenirTous(),
                    pagination = pagination.value,
                    criteres = criteresDernierChargement
                )

                // Sauvegarde les nouveaux résultats.
                // Le Flow de Room mettra ensuite l'écran à jour.
                dao.remplacerTous(nouveauxEvenements)

                // Ces informations changent seulement après une sauvegarde réussie.
                criteresDernierChargement = criteres
                pagination.value = reponse.page

                // L'annulation devient disponible seulement après la réussite.
                sauvegardePrecedente = sauvegarde

                prochainIdentifiant += 1
                miseAJourAnnulable.value = prochainIdentifiant

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

    fun chargerPagePrecedente() {
        val pageActuelle = pagination.value ?: return
        val criteres = criteresDernierChargement ?: return

        if (pageActuelle.numero <= 0) return

        chargerEvenements(
            criteres = criteres,
            pageDemandee = pageActuelle.numero - 1
        )
    }

    fun chargerPageSuivante() {
        val pageActuelle = pagination.value ?: return
        val criteres = criteresDernierChargement ?: return

        // Respecte le nombre de pages annoncé et la limite de Ticketmaster.
        val nombrePagesAccessibles = minOf(
            pageActuelle.nombreTotalPages,
            nombreMaximumPages
        )

        if (pageActuelle.numero + 1 >= nombrePagesAccessibles) return

        chargerEvenements(
            criteres = criteres,
            pageDemandee = pageActuelle.numero + 1
        )
    }

    fun oublierAnnulation(identifiant: Long) {
        // Un ancien message ne doit pas effacer une annulation plus récente.
        if (miseAJourAnnulable.value != identifiant) return

        miseAJourAnnulable.value = null
        sauvegardePrecedente = null
    }

    fun annulerMiseAJour(identifiant: Long) {
        if (chargement.value) return
        if (miseAJourAnnulable.value != identifiant) return

        val sauvegarde = sauvegardePrecedente ?: return

        chargement.value = true
        erreurChargement.value = false

        // La copie locale reste utilisable pour la restauration.
        oublierAnnulation(identifiant)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Restaure les évènements sans appeler Ticketmaster.
                dao.remplacerTous(sauvegarde.evenements)

                criteresDernierChargement = sauvegarde.criteres
                pagination.value = sauvegarde.pagination

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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
            // transmet un état d’erreur si la lecture échoue. emit() envoie cet état aux observateurs (l’écran de détail).
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