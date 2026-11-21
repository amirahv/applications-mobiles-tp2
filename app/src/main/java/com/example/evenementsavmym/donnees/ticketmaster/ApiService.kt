package com.example.evenementsavmym.donnees.ticketmaster

import com.example.evenementsavmym.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("events.json")
    suspend fun obtenirEvenements(
        @Query("apikey")
        cleApi: String = BuildConfig.TICKETMASTER_API_KEY,

        @Query("countryCode")
        codePays: String = "CA",

        @Query("classificationName")
        categorie: String? = null,

        @Query("locale")
        langue: String = "*",

        @Query("city")
        ville: String? = null,

        @Query("startDateTime")
        dateDebutUtc: String? = null,

        @Query("endDateTime")
        dateFinUtc: String? = null,

        @Query("size")
        taillePage: Int = 20,

        @Query("page")
        page: Int = 0
    ): ReponseEvenements
}