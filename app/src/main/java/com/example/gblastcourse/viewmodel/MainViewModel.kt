package com.example.gblastcourse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gblastcourse.model.AppState
import com.example.gblastcourse.model.Marker
import com.example.gblastcourse.model.IRepository
import com.example.gblastcourse.view.IGeoLocation
import com.example.gblastcourse.view.IGeoLocation.Location
import kotlinx.coroutines.*

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState>,
    private val repo: IRepository,
    private val geoLocation: IGeoLocation
) : ViewModel() {

    private var marker: Marker? = null

    private val viewModelCoroutineScope = CoroutineScope(
        Dispatchers.Main
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        })

    private fun cancelJob() = viewModelCoroutineScope.coroutineContext.cancelChildren()

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }

    private fun handleError(error: Throwable) {
        liveDataToObserve.postValue(AppState.Error(error))
    }

    fun getLiveData() = liveDataToObserve as LiveData<AppState>

    fun onMapClicked(location: Location){

    }

    fun onMarkerClicked(location: Location){

    }

    fun getAllMarkers() = getAllMarkersFromLocalStorage()

    fun setMarker(latitude: Double, longitude: Double) {
        cancelJob()
        viewModelCoroutineScope.launch {
            repo.saveMarker(Marker(latitude = latitude, longitude = longitude))
        }
    }

    fun getLocalMarker(): Marker? {
        return marker
    }

    fun setLocalMarkerFields(name: String, desctiption: String) {
        marker?.let {
            it.name = name
            it.description = desctiption
        }
    }

    fun setMarkerById() {
        marker?.let {
            cancelJob()
            viewModelCoroutineScope.launch {
                repo.saveMarker(it)
            }
        }


    }

    private fun getMarkerByLatitudeAndLongitudeFromLocalStorage(
        latitude: Double,
        longitude: Double
    ) {
        liveDataToObserveSingle.value = AppStateSingle.Loading
        cancelJob()
        viewModelCoroutineScope.launch {
            marker = repo.getDataByLatitudeAndLongitude(latitude, longitude)
            marker?.let {
                liveDataToObserveSingle.postValue(AppStateSingle.Success(it))
            }

        }
    }

    private fun getAllMarkersFromLocalStorage() {
        liveDataToObserve.value = AppState.Loading
        cancelJob()
        viewModelCoroutineScope.launch {
            liveDataToObserve.postValue(AppState.Success(repo.getAllMarkers()))
        }
    }
}