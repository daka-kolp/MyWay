package com.dkolp.myway.presentation.helpers

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dkolp.myway.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getPointerIcon(resources: Resources): BitmapDescriptor {
    val size = 124
    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.location_pin)
    val marker = Bitmap.createScaledBitmap(bitmap, size, size, false)
    return BitmapDescriptorFactory.fromBitmap(marker)
}
