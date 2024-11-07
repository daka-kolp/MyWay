package com.dkolp.myway.core.domain.entities

data class Place(
    val address: String,
    val geolocation: Geolocation,
) {
    companion object {
        fun nullable() = Place("", Geolocation.nullable())
    }

    override fun toString(): String { return address }
}
