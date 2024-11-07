package com.dkolp.myway.core.data.addresses.dto

import com.dkolp.myway.core.domain.entities.Address
import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Place
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDto(
    val address: String,
    val geolocation: GeolocationDto,
    val type: String,
) {
    fun toEntity(): Place {
        return Place(
            address = Address(
                address = address,
                geolocation = Geolocation(geolocation.lat, geolocation.lng),
            ),
            type = type,
        )
    }
}
