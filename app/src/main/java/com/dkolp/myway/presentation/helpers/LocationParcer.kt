package com.dkolp.myway.presentation.helpers

import android.location.Location
import com.dkolp.myway.core.domain.entities.Geolocation
import com.google.android.gms.maps.model.LatLng

fun Location.latLngFromLocation(): LatLng {
    return LatLng(latitude, longitude)
}

fun Location.geolocationFromLocation(): Geolocation {
    return Geolocation(latitude, longitude)
}
