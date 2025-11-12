package com.example.livequery

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

data class MensajeResponse(
    @SerializedName("objectId") val objectId: String,
    @SerializedName("texto") val texto: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class MensajesListResponse(
    @SerializedName("results") val results: List<MensajeResponse>
)

interface Back4AppApi {
    @GET("classes/Mensajes")
    suspend fun getMensajes(
        @Query("order") order: String = "-updatedAt",
        @Query("limit") limit: Int = 20
    ): MensajesListResponse
}

object Back4AppApiClient {
    private const val BASE_URL = "https://parseapi.back4app.com/"
    private const val APP_ID = "ocewe7lAehoWyPMZbupxuBdYpiVPouQ3aM9druBD"
    private const val REST_API_KEY = "aV2d5gPTu33XMMcQGopuHEyvCpoi72JDXs8k10eU"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val headersInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Parse-Application-Id", APP_ID)
            .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headersInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: Back4AppApi = retrofit.create(Back4AppApi::class.java)
}

