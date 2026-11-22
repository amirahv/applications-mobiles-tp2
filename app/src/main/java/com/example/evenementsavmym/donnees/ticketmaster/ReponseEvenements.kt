package com.example.evenementsavmym.donnees.ticketmaster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReponseEvenements(
    @SerialName("_embedded")
    val contenu: ContenuEvenements? = null,

    val page: PaginationApi? = null
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

    val classifications: List<ClassificationApi> = emptyList(),

    @SerialName("_embedded")
    val contenu: ContenuLieuxApi? = null,

    @SerialName("priceRanges")
    val prix: List<PrixApi> = emptyList(),

    @SerialName("sales")
    val ventes: VentesApi? = null,

    @SerialName("info")
    val informations: String? = null,

    @SerialName("pleaseNote")
    val remarques: String? = null,

    @SerialName("url")
    val billetterieUrl: String? = null
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
data class StatutApi(
    val code: String? = null
)

@Serializable
data class ImageApi(
    val url: String? = null
)

@Serializable
data class ClassificationApi(
    val segment: NomClassificationApi? = null,
    val genre: NomClassificationApi? = null
)

@Serializable
data class NomClassificationApi(
    @SerialName("name")
    val nom: String? = null
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
    val ville: VilleApi? = null,

    @SerialName("address")
    val adresse: AdresseApi? = null,

    @SerialName("country")
    val pays: PaysApi? = null
)

@Serializable
data class VilleApi(
    @SerialName("name")
    val nom: String? = null
)

@Serializable
data class AdresseApi(
    @SerialName("line1")
    val ligne1: String? = null
)

@Serializable
data class PaysApi(
    @SerialName("name")
    val nom: String? = null
)

@Serializable
data class PrixApi(
    @SerialName("min")
    val minimum: Double? = null,

    @SerialName("max")
    val maximum: Double? = null,

    @SerialName("currency")
    val devise: String? = null
)

@Serializable
data class VentesApi(
    @SerialName("public")
    val ventePublique: VentePubliqueApi? = null
)

@Serializable
data class VentePubliqueApi(
    @SerialName("startDateTime")
    val dateDebutUtc: String? = null
)

@Serializable
data class PaginationApi(
    @SerialName("size")
    val taille: Int = 20,

    @SerialName("totalElements")
    val nombreTotalEvenements: Int = 0,

    @SerialName("totalPages")
    val nombreTotalPages: Int = 0,

    @SerialName("number")
    val numero: Int = 0
)