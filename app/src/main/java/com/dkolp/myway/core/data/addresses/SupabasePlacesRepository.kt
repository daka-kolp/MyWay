package com.dkolp.myway.core.data.addresses

import com.dkolp.myway.core.data.addresses.dto.PlacePayloadDto
import com.dkolp.myway.core.data.addresses.dto.GeolocationDto
import com.dkolp.myway.core.data.addresses.dto.PlaceDto
import com.dkolp.myway.core.domain.PlacesRepository
import com.dkolp.myway.core.domain.entities.Place
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabasePlacesRepository(private val db: SupabaseClient) : PlacesRepository {
    override suspend fun savePlace(newPlace: Place) {
        val address = newPlace.address
        val location = address.geolocation
        db.from("places").insert(
            PlacePayloadDto(
                address = address.address,
                geolocation = GeolocationDto(location.latitude, location.longitude),
                type = newPlace.type
            )
        )
    }

    override suspend fun getPlaces(): List<Place> {
        val response = db.from("places").select().decodeList<PlaceDto>()
        return response.map { it.toEntity() }
    }
}
