package com.example.gblastcourse.model

interface IRepository {
    suspend fun saveMarker(marker: Marker)
    suspend fun updateMarker(marker: Marker)
    suspend fun getMarker(id: Int): Marker
    suspend fun getAllMarkers(): List<Marker>
}