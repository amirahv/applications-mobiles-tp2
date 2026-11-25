package com.example.evenementsavmym.ui.compte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evenementsavmym.donnees.firebase.AuthFirebase
import com.example.evenementsavmym.donnees.firebase.FirestoreRepository
import com.example.evenementsavmym.modele.ProfilUtilisateur
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConnexionEtat(
    val chargement: Boolean = false,
    val connecte: Boolean = false,
    val erreur: String? = null
)

class ConnexionViewModel : ViewModel() {

    private val auth = AuthFirebase()
    private val repository = FirestoreRepository()

    private val _etat = MutableStateFlow(
        ConnexionEtat(
            connecte = auth.utilisateurActuel() != null
        )
    )

    val etat: StateFlow<ConnexionEtat> = _etat

    fun connecter(
        courriel: String,
        motDePasse: String
    ) {

        if (courriel.isBlank() || motDePasse.isBlank()) {
            _etat.value = _etat.value.copy(
                erreur = "Veuillez remplir tous les champs."
            )
            return
        }

        viewModelScope.launch {

            _etat.value = ConnexionEtat(chargement = true)

            val resultat = auth.connecter(
                courriel,
                motDePasse
            )

            resultat.onSuccess { utilisateur ->

                repository.sauvegarderProfil(
                    ProfilUtilisateur(
                        uid = utilisateur.uid,
                        courriel = utilisateur.email ?: ""
                    )
                )

                _etat.value = ConnexionEtat(
                    connecte = true
                )
            }

            resultat.onFailure { erreur ->

                _etat.value = ConnexionEtat(
                    erreur = erreur.message
                        ?: "Erreur de connexion."
                )
            }
        }
    }

    fun inscrire(
        courriel: String,
        motDePasse: String
    ) {

        if (courriel.isBlank() || motDePasse.length < 6) {
            _etat.value = _etat.value.copy(
                erreur = "Entrez un courriel valide et un mot de passe d'au moins 6 caractères."
            )
            return
        }

        viewModelScope.launch {

            _etat.value = ConnexionEtat(chargement = true)

            val resultat = auth.inscrire(
                courriel,
                motDePasse
            )

            resultat.onSuccess { utilisateur ->

                repository.sauvegarderProfil(
                    ProfilUtilisateur(
                        uid = utilisateur.uid,
                        courriel = utilisateur.email ?: ""
                    )
                )

                _etat.value = ConnexionEtat(
                    connecte = true
                )
            }

            resultat.onFailure { erreur ->

                _etat.value = ConnexionEtat(
                    erreur = erreur.message
                        ?: "Erreur lors de l'inscription."
                )
            }
        }
    }

    fun deconnecter() {
        auth.deconnecter()

        _etat.value = ConnexionEtat(
            connecte = false
        )
    }

    fun effacerErreur() {
        _etat.value = _etat.value.copy(
            erreur = null
        )
    }
}