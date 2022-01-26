package com.example.gblastcourse.utils

import android.location.Location

import com.example.gblastcourse.room.MarkerEntity
import com.example.gblastcourse.view.IGeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker as GoogleMarker
import com.example.gblastcourse.model.Marker

fun List<MarkerEntity>.toMarkerList() = map { it.toMarker() }
fun MarkerEntity.toMarker() = Marker(id, name ?: "", description ?: "", latitude, longitude)
fun Marker.toMarkerEntity() = MarkerEntity(id, name, description, latitude, longitude)
fun LatLng.toLocation() = IGeoLocation.Location(latitude, longitude)
fun Location.toProjectLocation() = IGeoLocation.Location(latitude, longitude)
fun GoogleMarker.toProjectMarker() = Marker(latitude = position.latitude, longitude = position.longitude)