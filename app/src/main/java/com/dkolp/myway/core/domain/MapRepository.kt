package com.dkolp.myway.core.domain

import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Address

interface MapRepository {
    suspend fun findAddressesByText(text: String): List<Address>
    suspend fun findAddressesByGeolocation(geolocation: Geolocation): List<Address>
}
