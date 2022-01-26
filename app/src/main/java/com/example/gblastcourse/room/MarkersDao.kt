package com.example.gblastcourse.room

import androidx.room.*

@Dao
interface MarkersDao {
    @Query("SELECT * FROM MarkerEntity")
    suspend fun getAll(): List<MarkerEntity>

    @Query("SELECT * FROM MarkerEntity WHERE id = :id")
    suspend fun getMarker(id: Int): MarkerEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: MarkerEntity)

    @Update
    suspend fun update(entity: MarkerEntity)

    @Delete
    suspend fun delete(entity: MarkerEntity)
}