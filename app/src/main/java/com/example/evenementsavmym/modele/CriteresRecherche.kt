package com.example.evenementsavmym.modele

data class CriteresRecherche(
    val codePays: String = "CA",
    val categorie: String? = null,
    val langue: String = "*",
    val ville: String? = null,
    val dateDebutUtc: String? = null,
    val dateFinUtc: String? = null
)