package com.example.evenementsavmym.ui.evenements

import com.example.evenementsavmym.modele.Evenement

data class EtatDetailEvenement(
    val evenement: Evenement? = null,
    val chargement: Boolean = true,
    val erreur: Boolean = false
)