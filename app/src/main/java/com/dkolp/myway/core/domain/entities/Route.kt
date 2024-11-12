package com.dkolp.myway.core.domain.entities

data class Route(val points: String, val distanceInMeters: Int, val timeInMinutes: Int) {
    fun getFormattedDistance(): String {
        val km = distanceInMeters / 1000
        val meters = distanceInMeters % 1000
        return "${km}km ${meters}m"
    }
}
