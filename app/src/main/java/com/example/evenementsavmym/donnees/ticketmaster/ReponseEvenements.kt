package com.example.evenementsavmym.donnees.ticketmaster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReponseEvenements(
    @SerialName("_embedded")
    val contenu: ContenuEvenements? = null
)

@Serializable
data class ContenuEvenements(
    @SerialName("events")
    val evenements: List<EvenementApi> = emptyList()
)

@Serializable
data class EvenementApi(
    val id: String,

    @SerialName("name")
    val nom: String
)