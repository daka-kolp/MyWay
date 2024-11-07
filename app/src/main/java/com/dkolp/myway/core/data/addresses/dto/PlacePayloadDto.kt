package com.dkolp.myway.core.data.addresses.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlacePayloadDto(
    val address: String,
    val geolocation: GeolocationDto,
    val type: String,
)
