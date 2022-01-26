package com.example.gblastcourse.di

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.gblastcourse.model.IRepository
import com.example.gblastcourse.model.Repository
import com.example.gblastcourse.room.MarkersDataBase
import com.example.gblastcourse.model.AppState
import com.example.gblastcourse.view.GeoLocation
import com.example.gblastcourse.view.IGeoLocation
import com.example.gblastcourse.viewmodel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val application = module {
    single { Room.databaseBuilder(get(), MarkersDataBase::class.java, "MarkersDB").build() }
    single { get<MarkersDataBase>().markersDao() }
    single(named("default")) { MutableLiveData<AppState>() }
    single<IRepository> { Repository(get()) }
    single<IGeoLocation> { GeoLocation(get()) }
    viewModel { MainViewModel(get(named("default")), get()) }
}
