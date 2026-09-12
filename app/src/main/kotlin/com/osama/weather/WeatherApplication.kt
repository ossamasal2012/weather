package com.osama.weather

import android.app.Application
import com.osama.weather.data.local.PreferencesManager
import com.osama.weather.data.repository.AirQualityRepository
import com.osama.weather.data.repository.GeocodingRepository
import com.osama.weather.data.repository.WeatherRepository
import com.osama.weather.location.LocationHelper
import com.osama.weather.update.UpdateDownloadManager
import com.osama.weather.update.UpdateManager

/**
 * Small, explicit, framework-free dependency container — the app is not
 * large enough to need Hilt/Dagger, and keeping construction visible here in
 * one place is easier to audit than a DI graph.
 */
class WeatherApplication : Application() {

    val preferencesManager: PreferencesManager by lazy { PreferencesManager(this) }
    val locationHelper: LocationHelper by lazy { LocationHelper(this) }

    val weatherRepository: WeatherRepository by lazy { WeatherRepository() }
    val airQualityRepository: AirQualityRepository by lazy { AirQualityRepository() }
    val geocodingRepository: GeocodingRepository by lazy { GeocodingRepository() }

    val updateManager: UpdateManager by lazy { UpdateManager() }
    val updateDownloadManager: UpdateDownloadManager by lazy {
        UpdateDownloadManager(this, preferencesManager)
    }

    override fun onCreate() {
        super.onCreate()
        // Lives for the whole process lifetime so a download that completes
        // while the app is merely backgrounded (not killed) still triggers
        // an immediate install, per the update-system brief.
        updateDownloadManager.startListeningForCompletion()
    }
}
