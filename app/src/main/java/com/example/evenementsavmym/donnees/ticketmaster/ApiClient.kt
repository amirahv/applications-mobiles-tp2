package com.example.evenementsavmym.donnees.ticketmaster

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {

    private const val BASE_URL =
        "https://app.ticketmaster.com/discovery/v2/"

    private val retrofit by lazy {
        val json = Json {
            // Accepte les champs JSON absents de nos classes Kotlin.
            ignoreUnknownKeys = true
        }

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}