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
    val nom: String,

    val dates: DatesApi? = null,

    val images: List<ImageApi> = emptyList(),

    @SerialName("_embedded")
    val contenu: ContenuLieuxApi? = null
)

@Serializable
data class DatesApi(
    @SerialName("start")
    val debut: DebutApi? = null,

    @SerialName("status")
    val statut: StatutApi? = null
)

@Serializable
data class DebutApi(
    @SerialName("localDate")
    val dateLocale: String? = null,

    @SerialName("localTime")
    val heureLocale: String? = null
)

@Serializable
data class ImageApi(
    val url: String? = null
)

@Serializable
data class ContenuLieuxApi(
    @SerialName("venues")
    val lieux: List<LieuApi> = emptyList()
)

@Serializable
data class LieuApi(
    @SerialName("name")
    val nom: String? = null,

    @SerialName("city")
    val ville: VilleApi? = null
)

@Serializable
data class VilleApi(
    @SerialName("name")
    val nom: String? = null
)

@Serializable
data class StatutApi(
    val code: String? = null
)