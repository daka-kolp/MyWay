package com.dkolp.myway.core.domain.entities

data class Place(
    val address: String,
    val geolocation: Geolocation,
) {
    override fun toString(): String {
        return "$address, ${geolocation.formatted()}"
    }
}
