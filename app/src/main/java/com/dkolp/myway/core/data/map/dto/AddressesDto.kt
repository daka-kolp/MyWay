package com.dkolp.myway.core.data.map.dto

import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Address
import com.google.gson.annotations.SerializedName

data class CandidatesDto(val candidates: List<AddressDto>)

data class AddressesDto(val results: List<AddressDto>)

data class AddressDto(
    @SerializedName("formatted_address") val formattedAddress: String,
    val geometry: GeometryDto,
) {
    fun toEntity(): Address {
        val location = geometry.location
        return Address(
            address = formattedAddress,
            geolocation = Geolocation(location.lat, location.lng),
        )
    }
}
