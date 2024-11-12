package com.dkolp.myway.core.domain.entities

data class Route(val points: String, val distanceInMeters: Int, val timeInMinutes: Int) {
    fun getFormattedDistance(): String {
        val km = distanceInMeters / 1000
        val meters = distanceInMeters % 1000
        return "${km}km ${meters}m"
    }

    fun getFormattedTime(): String {
        val hours = timeInMinutes / 3600
        val minutes = timeInMinutes % 3600 / 60
        if (hours == 0) return "${minutes}min"
        return "${hours}h ${minutes}min"
    }
}
