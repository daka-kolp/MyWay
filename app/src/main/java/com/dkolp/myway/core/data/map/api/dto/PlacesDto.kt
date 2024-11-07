package com.dkolp.myway.core.data.map.api.dto

import com.dkolp.myway.core.domain.map.entities.Geolocation
import com.dkolp.myway.core.domain.map.entities.Place
import com.google.gson.annotations.SerializedName

data class CandidatesDto(val candidates: List<PlaceDto>)

data class PlacesDto(val results: List<PlaceDto>)

data class PlaceDto(
    @SerializedName("formatted_address") val formattedAddress: String,
    val geometry: GeometryDto,
) {
    fun toEntity(): Place {
        val location = geometry.location
        return Place(
            address = formattedAddress,
            geolocation = Geolocation(location.lat, location.lng),
        )
    }
}
