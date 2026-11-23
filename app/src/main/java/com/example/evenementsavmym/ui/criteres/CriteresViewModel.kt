
package com.example.evenementsavmym.ui.criteres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evenementsavmym.donnees.firebase.AuthRepository
import com.example.evenementsavmym.donnees.firebase.FirestoreRepository
import com.example.evenementsavmym.modele.CriteresRecherche
import com.example.evenementsavmym.modele.ProfilUtilisateur
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CriteresUiState(
    val criteres: CriteresRecherche = CriteresRecherche(),
    val chargement: Boolean = false,
    val message: String? = null,
    val erreur: String? = null,
    val profil: ProfilUtilisateur? = null
)

class CriteresViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val firestoreRepository = FirestoreRepository()

    private val _uiState = MutableStateFlow(CriteresUiState())
    val uiState: StateFlow<CriteresUiState> = _uiState.asStateFlow()

    init {
        chargerCriteres()
    }

    fun modifierPays(value: String) {
        modifierCriteres {
            copy(codePays = value)
        }
    }

    fun modifierCategorie(value: String) {
        modifierCriteres {
            copy(categorie = value.ifBlank { null })
        }
    }

    fun modifierLangue(value: String) {
        modifierCriteres {
            copy(langue = value)
        }
    }

    fun modifierVille(value: String) {
        modifierCriteres {
            copy(ville = value.ifBlank { null })
        }
    }

    fun modifierDateDebut(value: String) {
        modifierCriteres {
            copy(dateDebutUtc = value.ifBlank { null })
        }
    }

    fun modifierDateFin(value: String) {
        modifierCriteres {
            copy(dateFinUtc = value.ifBlank { null })
        }
    }

    private fun modifierCriteres(
        transformation: CriteresRecherche.() -> CriteresRecherche
    ) {
        _uiState.value = _uiState.value.copy(
            criteres = _uiState.value.criteres.transformation(),
            message = null,
            erreur = null
        )
    }

    fun chargerCriteres() {

        val uid = authRepository.utilisateurActuel?.uid

        if (uid == null) {
            _uiState.value = _uiState.value.copy(
                erreur = "Aucun utilisateur connecté."
            )
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                chargement = true,
                erreur = null
            )

            try {

                val profil = firestoreRepository.obtenirProfil(uid)

                val criteres = firestoreRepository.obtenirCriteres(uid)

                _uiState.value = _uiState.value.copy(
                    profil = profil,
                    criteres = criteres,
                    chargement = false
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    chargement = false,
                    erreur = e.localizedMessage
                        ?: "Impossible de charger les critères."
                )
            }
        }
    }

    fun sauvegarderCriteres() {

        val uid = authRepository.utilisateurActuel?.uid

        if (uid == null) {
            _uiState.value = _uiState.value.copy(
                erreur = "Vous devez être connecté."
            )
            return
        }

        val criteres = _uiState.value.criteres

        if (criteres.codePays.isBlank()) {
            _uiState.value = _uiState.value.copy(
                erreur = "Le pays est obligatoire."
            )
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                chargement = true,
                message = null,
                erreur = null
            )

            try {

                firestoreRepository.sauvegarderCriteres(
                    uid,
                    criteres
                )

                _uiState.value = _uiState.value.copy(
                    chargement = false,
                    message = "Critères sauvegardés avec succès."
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    chargement = false,
                    erreur = e.localizedMessage
                        ?: "Erreur pendant la sauvegarde."
                )
            }
        }
    }
}
