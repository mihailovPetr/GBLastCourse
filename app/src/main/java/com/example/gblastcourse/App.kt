package com.example.gblastcourse

import android.app.Application
import com.example.gblastcourse.di.application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(listOf(application))
        }
    }
}