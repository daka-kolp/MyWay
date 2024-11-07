package com.dkolp.myway.core.domain

import com.dkolp.myway.core.domain.entities.Address
import com.dkolp.myway.core.domain.entities.Place

interface PlacesRepository {
    suspend fun savePlace(place: Address, type: String)
    suspend fun getPlaces(): List<Place>
}
