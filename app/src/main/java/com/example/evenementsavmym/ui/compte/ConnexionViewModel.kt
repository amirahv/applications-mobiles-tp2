
package com.example.evenementsavmym.ui.compte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evenementsavmym.donnees.firebase.AuthRepository
import com.example.evenementsavmym.donnees.firebase.FirestoreRepository
import com.example.evenementsavmym.modele.ProfilUtilisateur
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConnexionUiState(
    val email: String = "",
    val motDePasse: String = "",
    val chargement: Boolean = false,
    val erreur: String? = null
)

class ConnexionViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val firestoreRepository = FirestoreRepository()

    private val _uiState = MutableStateFlow(ConnexionUiState())
    val uiState: StateFlow<ConnexionUiState> = _uiState.asStateFlow()

    fun modifierEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            erreur = null
        )
    }

    fun modifierMotDePasse(motDePasse: String) {
        _uiState.value = _uiState.value.copy(
            motDePasse = motDePasse,
            erreur = null
        )
    }

    fun connecter(onSucces: () -> Unit) {

        val email = _uiState.value.email.trim()
        val motDePasse = _uiState.value.motDePasse

        if (email.isBlank() || motDePasse.isBlank()) {
            _uiState.value = _uiState.value.copy(
                erreur = "Veuillez remplir tous les champs."
            )
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                chargement = true,
                erreur = null
            )

            try {

                val utilisateur = authRepository.connecter(
                    email,
                    motDePasse
                )

                if (utilisateur != null) {

                    firestoreRepository.sauvegarderProfil(
                        ProfilUtilisateur(
                            uid = utilisateur.uid,
                            email = utilisateur.email ?: "",
                            nom = utilisateur.displayName ?: ""
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    chargement = false
                )

                onSucces()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    chargement = false,
                    erreur = e.localizedMessage
                        ?: "Erreur de connexion."
                )
            }
        }
    }

    fun creerCompte(onSucces: () -> Unit) {

        val email = _uiState.value.email.trim()
        val motDePasse = _uiState.value.motDePasse

        if (email.isBlank() || motDePasse.isBlank()) {
            _uiState.value = _uiState.value.copy(
                erreur = "Veuillez remplir tous les champs."
            )
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                chargement = true,
                erreur = null
            )

            try {

                val utilisateur = authRepository.creerCompte(
                    email,
                    motDePasse
                )

                if (utilisateur != null) {

                    firestoreRepository.sauvegarderProfil(
                        ProfilUtilisateur(
                            uid = utilisateur.uid,
                            email = utilisateur.email ?: ""
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    chargement = false
                )

                onSucces()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    chargement = false,
                    erreur = e.localizedMessage
                        ?: "Impossible de créer le compte."
                )
            }
        }
    }
}