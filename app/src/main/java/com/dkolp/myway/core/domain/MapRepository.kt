package com.dkolp.myway.core.domain

import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Place

interface MapRepository {
    suspend fun findPlaceByText(text: String): List<Place>
    suspend fun findPlaceByGeolocation(geolocation: Geolocation): List<Place>
}
