package com.example.evenementsavmym.ui.criteres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evenementsavmym.donnees.firebase.AuthFirebase
import com.example.evenementsavmym.donnees.firebase.FirestoreRepository
import com.example.evenementsavmym.modele.CriteresRecherche
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CriteresEtat(
    val criteres: CriteresRecherche = CriteresRecherche(),
    val chargement: Boolean = false,
    val sauvegardeReussie: Boolean = false,
    val erreur: String? = null
)

class CriteresViewModel : ViewModel() {

    private val auth = AuthFirebase()
    private val repository = FirestoreRepository()

    private val _etat = MutableStateFlow(CriteresEtat())

    val etat: StateFlow<CriteresEtat> = _etat

    fun chargerCriteres() {

        val utilisateur = auth.utilisateurActuel() ?: return

        viewModelScope.launch {

            _etat.value = _etat.value.copy(
                chargement = true,
                erreur = null
            )

            val resultat = repository.lireCriteres(
                utilisateur.uid
            )

            resultat.onSuccess { criteres ->

                _etat.value = CriteresEtat(
                    criteres = criteres,
                    chargement = false
                )
            }

            resultat.onFailure { erreur ->

                _etat.value = _etat.value.copy(
                    chargement = false,
                    erreur = erreur.message
                        ?: "Impossible de charger les critères."
                )
            }
        }
    }

    fun modifierCriteres(
        criteres: CriteresRecherche
    ) {
        _etat.value = _etat.value.copy(
            criteres = criteres,
            sauvegardeReussie = false,
            erreur = null
        )
    }

    fun sauvegarderCriteres() {

        val utilisateur = auth.utilisateurActuel()

        if (utilisateur == null) {
            _etat.value = _etat.value.copy(
                erreur = "Vous devez être connecté."
            )
            return
        }

        viewModelScope.launch {

            _etat.value = _etat.value.copy(
                chargement = true,
                erreur = null,
                sauvegardeReussie = false
            )

            val resultat = repository.sauvegarderCriteres(
                utilisateur.uid,
                _etat.value.criteres
            )

            resultat.onSuccess {

                _etat.value = _etat.value.copy(
                    chargement = false,
                    sauvegardeReussie = true
                )
            }

            resultat.onFailure { erreur ->

                _etat.value = _etat.value.copy(
                    chargement = false,
                    erreur = erreur.message
                        ?: "Erreur de sauvegarde."
                )
            }
        }
    }
}