package com.dkolp.myway.core.domain.map.entities

data class Place(
    val address: String,
    val geolocation: Geolocation,
) {
    companion object {
        fun nullable() = Place("", Geolocation.nullable())
    }

    override fun toString(): String { return address }
}
