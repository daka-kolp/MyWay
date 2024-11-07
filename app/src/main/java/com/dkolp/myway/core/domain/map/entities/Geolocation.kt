package com.dkolp.myway.core.domain.map.entities

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
        return "${latitude.roundTo2Decimals()}, ${longitude.roundTo2Decimals()}"
    }

    private fun Double.roundTo2Decimals(): String {
        return String.format("%.3f", this)
    }
}
