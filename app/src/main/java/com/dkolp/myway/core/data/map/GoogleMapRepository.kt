package com.dkolp.myway.core.data.map

import com.dkolp.myway.core.data.map.api.GoogleApiClient
import com.dkolp.myway.core.data.map.api.GoogleApiService
import com.dkolp.myway.core.domain.map.MapRepository
import com.dkolp.myway.core.domain.map.entities.Geolocation
import com.dkolp.myway.core.domain.map.entities.Place

class GoogleMapRepository(private val client: GoogleApiClient) : MapRepository {
    private val service: GoogleApiService
        get() = client.retrofit.create(GoogleApiService::class.java)

    override suspend fun findPlaceByText(text: String): List<Place> {
        val result = service.findPlaceByText(text)
        return result.body()!!.candidates.map { it.toEntity() }
    }

    override suspend fun findPlaceByGeolocation(geolocation: Geolocation): List<Place> {
        val result = service.findPlaceByLatLng(geolocation.toString())
        return result.body()!!.results.map { it.toEntity() }
    }
}
