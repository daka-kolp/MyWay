package com.dkolp.myway.core.data.map.dto

import com.dkolp.myway.core.domain.entities.Route
import com.google.gson.annotations.SerializedName

data class RoutesDto(val routes: List<RouteDto>)

data class RouteDto(@SerializedName("overview_polyline") val polyline: PolylineDto, val legs: List<LegDto>) {
    fun toEntity(): Route = Route(
        distanceInMeters = legs.fold(0) { sum, element -> sum + element.distance.value },
        timeInMinutes = legs.fold(0) { sum, element -> sum + element.duration.value },
        points = polyline.points
    )
}

data class LegDto(val distance: DistanceDto, val duration: TimeDto)

data class DistanceDto(val value: Int)

data class TimeDto(val value: Int)

data class PolylineDto(val points: String)
