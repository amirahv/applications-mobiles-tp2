
package com.example.evenementsavmym.donnees.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    val utilisateurActuel: FirebaseUser?
        get() = auth.currentUser

    suspend fun connecter(
        email: String,
        motDePasse: String
    ): FirebaseUser? {

        return auth.signInWithEmailAndPassword(
            email,
            motDePasse
        ).await().user
    }

    suspend fun creerCompte(
        email: String,
        motDePasse: String
    ): FirebaseUser? {

        return auth.createUserWithEmailAndPassword(
            email,
            motDePasse
        ).await().user
    }

    fun deconnecter() {
        auth.signOut()
    }
}
