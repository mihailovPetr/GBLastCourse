package com.example.gblastcourse.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    var id: Int = 0,
    @field:ColumnInfo(name = "name")
    var name: String? = null,
    @field:ColumnInfo(name = "description")
    var description: String? = null,
    @field:ColumnInfo(name = "latitude")
    var latitude: Double,
    @field:ColumnInfo(name = "longitude")
    var longitude: Double
)