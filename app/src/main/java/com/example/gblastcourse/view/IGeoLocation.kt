package com.example.gblastcourse.view

interface IGeoLocation {
    val locationPermissionGranted: Boolean
    val gpsIsEnabled: Boolean
    fun getMyLocation(callback: (Location?) -> Unit)
    fun getLastKnownLocation(): Location?

    data class Location(val latitude: Double, val longitude: Double)
}