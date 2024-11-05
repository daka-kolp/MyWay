package com.dkolp.myway.core.data.api

import com.dkolp.myway.core.data.api.dto.CandidatesDto
import com.dkolp.myway.core.data.api.dto.PlacesDto
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class GoogleApiClient {
    private val baseUrl = "https://maps.googleapis.com/maps/api/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()
}

const val apiKey = "AIzaSyCDuFkfLEUe-l7vyaFOU4r5D7FJFqjSA_4"

interface GoogleApiService {
    @GET("place/findplacefromtext/json")
    suspend fun findPlaceByText(
        @Query("input") text: String,
        @Query("fields") fields: String = "formatted_address,name,geometry",
        @Query("inputtype") type: String = "textquery",
        @Query("key") key: String = apiKey
    ): Response<CandidatesDto>

    @GET("geocode/json")
    suspend fun findPlaceByLatLng(
        @Query("latlng") latLng: String,
        @Query("key") key: String = apiKey
    ): Response<PlacesDto>
}
