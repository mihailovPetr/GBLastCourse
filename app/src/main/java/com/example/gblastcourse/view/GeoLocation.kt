package com.example.gblastcourse.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.gblastcourse.utils.toProjectLocation
import com.google.android.gms.location.LocationServices

class GeoLocation(private val context: Context) : IGeoLocation {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override val locationPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override val gpsIsEnabled: Boolean
        get() = locationPermissionGranted && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)


    override fun getMyLocation(callback: (IGeoLocation.Location?) -> Unit) {
        if (!locationPermissionGranted) {
            callback(null)
            return
        }
        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                task.result.apply {
                    callback(toProjectLocation())
                    return@addOnCompleteListener
                }
            } else {
                callback(null)
            }
        }
    }

    override fun getLastKnownLocation() =
        if (locationPermissionGranted)
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toProjectLocation()
        else null
}