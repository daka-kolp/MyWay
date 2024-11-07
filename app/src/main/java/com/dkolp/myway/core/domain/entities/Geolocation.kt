package com.dkolp.myway.core.domain.entities

data class Geolocation(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun nullable() = Geolocation(0.0,0.0)
    }

    override fun toString(): String {
        return "$latitude,$longitude"
    }

    fun formatted(): String {
        return "${latitude.roundTo6Decimals()}, ${longitude.roundTo6Decimals()}"
    }

    private fun Double.roundTo6Decimals(): String {
        return String.format("%.6f", this)
    }
}
