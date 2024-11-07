package com.dkolp.myway.core.domain.map

import com.dkolp.myway.core.domain.map.entities.Geolocation
import com.dkolp.myway.core.domain.map.entities.Place

interface MapRepository {
    suspend fun findPlaceByText(text: String): List<Place>
    suspend fun findPlaceByGeolocation(geolocation: Geolocation): List<Place>
}
