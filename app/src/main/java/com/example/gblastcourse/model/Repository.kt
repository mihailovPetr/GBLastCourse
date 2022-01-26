package com.example.gblastcourse.model

import com.example.gblastcourse.room.MarkersDao
import com.example.gblastcourse.utils.convertMarkerEntityListToModel
import com.example.gblastcourse.utils.`Marker.toMarkerEntity`

class Repository(private val markersDao: MarkersDao) : IRepository {
    override suspend fun saveMarker(marker: Marker) {
        markersDao.insert(`Marker.toMarkerEntity`(marker))
    }

    override suspend fun updateMarker(marker: Marker) {
        markersDao.update(`Marker.toMarkerEntity`(marker))
    }

    override suspend fun getMarker(id: Int): Marker =
        com.example.gblastcourse.utils.toMarker(markersDao.getMarker(id))

    override suspend fun getAllMarkers(): List<Marker> = convertMarkerEntityListToModel(markersDao.getAll())
}