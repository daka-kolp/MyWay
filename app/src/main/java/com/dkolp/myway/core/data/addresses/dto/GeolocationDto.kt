package com.dkolp.myway.core.data.addresses.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeolocationDto(val lat: Double, val lng: Double)
