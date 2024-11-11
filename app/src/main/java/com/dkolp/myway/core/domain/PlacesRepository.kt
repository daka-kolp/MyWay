package com.dkolp.myway.core.domain

import com.dkolp.myway.core.domain.entities.Place

interface PlacesRepository {
    suspend fun savePlace(newPlace: Place)
    suspend fun getPlaces(): List<Place>
}
