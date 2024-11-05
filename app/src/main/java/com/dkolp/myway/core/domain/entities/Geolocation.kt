package com.dkolp.myway.core.domain.entities

data class Geolocation(
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }

    fun formatted(): String {
        return "${latitude.roundTo2Decimals()}, ${longitude.roundTo2Decimals()}"
    }

    private fun Double.roundTo2Decimals(): String {
        return String.format("%.3f", this)
    }
}
