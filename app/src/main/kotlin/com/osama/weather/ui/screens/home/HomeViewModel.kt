package com.osama.weather.ui.screens.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.osama.weather.R
import com.osama.weather.WeatherApplication
import com.osama.weather.data.local.PrecipitationUnit
import com.osama.weather.data.local.PrivacyPolicyLanguage
import com.osama.weather.data.local.TemperatureUnit
import com.osama.weather.data.local.WindUnit
import com.osama.weather.domain.model.AirQualityBundle
import com.osama.weather.domain.model.GeoLocation
import com.osama.weather.domain.model.WeatherBundle
import com.osama.weather.ui.components.UpdateDialogState
import com.osama.weather.update.DownloadStatus
import com.osama.weather.update.model.UpdateCheckResult
import com.osama.weather.update.model.VersionInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val location: GeoLocation? = null,
    val weather: WeatherBundle? = null,
    val airQuality: AirQualityBundle? = null,
    val isLoading: Boolean = true,
    val errorMessageRes: Int? = null,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windUnit: WindUnit = WindUnit.KMH,
    val precipitationUnit: PrecipitationUnit = PrecipitationUnit.MM,
    val locationPermissionPermanentlyUnavailable: Boolean = false,
    val privacyPolicyLanguage: PrivacyPolicyLanguage = PrivacyPolicyLanguage.ARABIC
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app get() = getApplication<WeatherApplication>()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateDialogState?>(null)
    val updateState: StateFlow<UpdateDialogState?> = _updateState.asStateFlow()

    private var pendingVersionInfo: VersionInfo? = null

    init {
        viewModelScope.launch { app.preferencesManager.temperatureUnit.collect { u -> _uiState.update { it.copy(temperatureUnit = u) } } }
        viewModelScope.launch { app.preferencesManager.windUnit.collect { u -> _uiState.update { it.copy(windUnit = u) } } }
        viewModelScope.launch { app.preferencesManager.precipitationUnit.collect { u -> _uiState.update { it.copy(precipitationUnit = u) } } }
        viewModelScope.launch { app.preferencesManager.privacyPolicyLanguage.collect { l -> _uiState.update { it.copy(privacyPolicyLanguage = l) } } }

        loadInitialLocation()
        checkForUpdate()

        viewModelScope.launch { app.updateDownloadManager.checkPendingDownloadOnLaunch() }
    }

    // --------------------------------------------------------------- location

    private fun loadInitialLocation() {
        viewModelScope.launch {
            val cached = app.preferencesManager.getLastLocation()
            if (cached != null) {
                _uiState.update { it.copy(location = cached) }
                loadWeatherFor(cached)
            }

            if (app.locationHelper.hasLocationPermission()) {
                useCurrentLocationInternal()
            } else if (cached == null) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun useCurrentLocation() {
        viewModelScope.launch { useCurrentLocationInternal() }
    }

    private suspend fun useCurrentLocationInternal() {
        val hadData = _uiState.value.weather != null
        if (!hadData) _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        app.locationHelper.getCurrentFix()
            .onSuccess { (lat, lon) ->
                val geo = app.locationHelper.reverseGeocode(lat, lon)
                selectLocation(geo)
            }
            .onFailure {
                if (!hadData) {
                    _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_location_not_found) }
                }
            }
    }

    fun selectLocation(location: GeoLocation) {
        _uiState.update { it.copy(location = location) }
        viewModelScope.launch {
            app.preferencesManager.saveLastLocation(location)
            if (!location.isCurrentLocation) {
                app.preferencesManager.addRecentSearch(location)
            }
        }
        loadWeatherFor(location)
    }

    private fun loadWeatherFor(location: GeoLocation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val weatherDeferred = async { app.weatherRepository.getWeather(location.latitude, location.longitude) }
            val airDeferred = async { app.airQualityRepository.getAirQuality(location.latitude, location.longitude) }

            weatherDeferred.await()
                .onSuccess { bundle -> _uiState.update { it.copy(weather = bundle, isLoading = false, errorMessageRes = null) } }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessageRes = if (state.weather == null) R.string.error_network else null)
                    }
                }

            // Air quality is supplementary — a failure here never blocks the weather screen.
            airDeferred.await().onSuccess { bundle -> _uiState.update { it.copy(airQuality = bundle) } }
        }
    }

    fun refresh() {
        _uiState.value.location?.let { loadWeatherFor(it) }
    }

    fun markLocationPermissionPermanentlyDenied() {
        _uiState.update { it.copy(locationPermissionPermanentlyUnavailable = true, isLoading = false) }
    }

    // ------------------------------------------------------------- settings

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { app.preferencesManager.setTemperatureUnit(unit) }
    }

    fun setWindUnit(unit: WindUnit) {
        viewModelScope.launch { app.preferencesManager.setWindUnit(unit) }
    }

    fun setPrecipitationUnit(unit: PrecipitationUnit) {
        viewModelScope.launch { app.preferencesManager.setPrecipitationUnit(unit) }
    }

    fun setPrivacyPolicyLanguage(language: PrivacyPolicyLanguage) {
        viewModelScope.launch { app.preferencesManager.setPrivacyPolicyLanguage(language) }
    }

    // --------------------------------------------------------------- update

    /**
     * @param showResultIfNoUpdate Whether to surface a dialog even when there's
     * nothing to install — true for the explicit "Check for Updates" tap in
     * Settings (the person is waiting for an answer either way), false for the
     * silent automatic check on every cold start (which should only ever
     * interrupt the person when an update genuinely needs installing).
     */
    fun checkForUpdate(showResultIfNoUpdate: Boolean = false) {
        viewModelScope.launch {
            when (val result = app.updateManager.checkForUpdate()) {
                is UpdateCheckResult.UpdateAvailable -> {
                    pendingVersionInfo = result.info
                    _updateState.value = UpdateDialogState.Available(result.info.versionName, result.info.forceUpdate)
                }
                is UpdateCheckResult.UpToDate -> {
                    if (showResultIfNoUpdate) _updateState.value = UpdateDialogState.UpToDate
                }
                is UpdateCheckResult.Error -> {
                    if (showResultIfNoUpdate) _updateState.value = UpdateDialogState.CheckFailed
                }
            }
        }
    }

    fun dismissUpdateDialog() {
        _updateState.value = null
    }

    fun onUpdateNowClick() {
        val info = pendingVersionInfo ?: return
        if (!app.updateDownloadManager.canInstallPackages()) {
            _updateState.value = UpdateDialogState.RequestingInstallPermission
            return
        }
        startDownload(info)
    }

    fun onReturnedFromInstallSettings() {
        val info = pendingVersionInfo ?: return
        if (app.updateDownloadManager.canInstallPackages()) {
            startDownload(info)
        }
    }

    fun installSettingsIntent(): Intent = app.updateDownloadManager.installPermissionSettingsIntent()

    fun onRetryDownload() {
        pendingVersionInfo?.let { startDownload(it) }
    }

    private fun startDownload(info: VersionInfo) {
        viewModelScope.launch {
            _updateState.value = UpdateDialogState.Downloading(0)
            val id = app.updateDownloadManager.enqueueDownload(info.apkUrl, info.versionCode)
            app.updateDownloadManager.observeProgress(id).collect { progress ->
                _updateState.value = when (progress.status) {
                    DownloadStatus.SUCCESSFUL -> {
                        app.updateDownloadManager.triggerInstall(id)
                        UpdateDialogState.Installing
                    }
                    DownloadStatus.FAILED, DownloadStatus.NOT_FOUND -> UpdateDialogState.Failed
                    else -> UpdateDialogState.Downloading(progress.percent)
                }
            }
        }
    }
}
