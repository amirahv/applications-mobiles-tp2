
package com.example.evenementsavmym.donnees.firebase

import com.example.evenementsavmym.modele.CriteresRecherche
import com.example.evenementsavmym.modele.ProfilUtilisateur
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun sauvegarderProfil(
        profil: ProfilUtilisateur
    ) {
        db.collection("utilisateurs")
            .document(profil.uid)
            .set(profil)
            .await()
    }

    suspend fun obtenirProfil(
        uid: String
    ): ProfilUtilisateur? {

        val document = db.collection("utilisateurs")
            .document(uid)
            .get()
            .await()

        return document.toObject(ProfilUtilisateur::class.java)
    }

    suspend fun sauvegarderCriteres(
        uid: String,
        criteres: CriteresRecherche
    ) {
        db.collection("utilisateurs")
            .document(uid)
            .collection("criteres")
            .document("recherche")
            .set(criteres)
            .await()
    }

    suspend fun obtenirCriteres(
        uid: String
    ): CriteresRecherche {

        val document = db.collection("utilisateurs")
            .document(uid)
            .collection("criteres")
            .document("recherche")
            .get()
            .await()

        return document.toObject(CriteresRecherche::class.java)
            ?: CriteresRecherche()
    }
}
