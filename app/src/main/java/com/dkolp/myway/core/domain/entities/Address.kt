package com.dkolp.myway.core.domain.entities

data class Address(
    val address: String,
    val geolocation: Geolocation,
) {
    companion object {
        fun nullable() = Address("", Geolocation.nullable())
    }

    override fun toString(): String { return address }
}
