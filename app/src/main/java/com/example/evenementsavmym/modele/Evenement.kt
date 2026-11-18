package com.example.evenementsavmym.modele

data class Evenement(
    val id: String,
    val nom: String,
    val dateLocale: String? = null,
    val salle: String? = null,
    val ville: String? = null,
    val imageUrl: String? = null,
    val heureLocale: String? = null,
    val statut: String? = null,
    val categorie: String? = null,
    val genre: String? = null,
    val adresse: String? = null,
    val pays: String? = null,
    val prixMin: Double? = null,
    val prixMax: Double? = null,
    val devise: String? = null,
    val dateDebutVenteUtc: String? = null,
    val informations: String? = null,
    val billetterieUrl: String? = null
)