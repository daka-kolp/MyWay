package com.dkolp.myway.core.data.map

import com.dkolp.myway.core.domain.MapRepository
import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Address

class GoogleMapRepository(private val client: GoogleApiClient) : MapRepository {
    private val service: GoogleApiService
        get() = client.retrofit.create(GoogleApiService::class.java)

    override suspend fun findAddressesByText(text: String): List<Address> {
        val result = service.findAddressesByText(text)
        return result.body()!!.candidates.map { it.toEntity() }
    }

    override suspend fun findAddressesByGeolocation(geolocation: Geolocation): List<Address> {
        val result = service.findAddressesByLatLng(geolocation.toString())
        return result.body()!!.results.map { it.toEntity() }
    }
}
