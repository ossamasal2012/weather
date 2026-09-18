package com.osama.weather.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.osama.weather.domain.model.GeoLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_prefs")

enum class TemperatureUnit(val apiSuffix: String) { CELSIUS("celsius"), FAHRENHEIT("fahrenheit") }
enum class WindUnit { KMH, MS, MPH, KNOTS }
enum class PrecipitationUnit { MM, INCH }

/** Which language body the Privacy Policy screen shows — defaults to Arabic for a person's very first visit. */
enum class PrivacyPolicyLanguage { ARABIC, ENGLISH }

class PreferencesManager(private val context: Context) {

    private object Keys {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val PRECIP_UNIT = stringPreferencesKey("precip_unit")
        val LAST_LOCATION_JSON = stringPreferencesKey("last_location_json")
        val RECENT_SEARCHES_JSON = stringPreferencesKey("recent_searches_json")
        val PENDING_DOWNLOAD_ID = longPreferencesKey("pending_download_id")
        val PENDING_DOWNLOAD_VERSION_CODE = intPreferencesKey("pending_download_version_code")
        val LAST_SEEN_REMOTE_VERSION_CODE = intPreferencesKey("last_seen_remote_version_code")
        val PRIVACY_POLICY_LANGUAGE = stringPreferencesKey("privacy_policy_language")
    }

    private val json = Json { ignoreUnknownKeys = true }

    // ------------------------------------------------------------------ units

    val temperatureUnit: Flow<TemperatureUnit> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.TEMP_UNIT]) {
            "fahrenheit" -> TemperatureUnit.FAHRENHEIT
            else -> TemperatureUnit.CELSIUS
        }
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[Keys.TEMP_UNIT] = unit.name.lowercase() }
    }

    val windUnit: Flow<WindUnit> = context.dataStore.data.map { prefs ->
        runCatching { WindUnit.valueOf(prefs[Keys.WIND_UNIT] ?: WindUnit.KMH.name) }
            .getOrDefault(WindUnit.KMH)
    }

    suspend fun setWindUnit(unit: WindUnit) {
        context.dataStore.edit { it[Keys.WIND_UNIT] = unit.name }
    }

    val precipitationUnit: Flow<PrecipitationUnit> = context.dataStore.data.map { prefs ->
        runCatching { PrecipitationUnit.valueOf(prefs[Keys.PRECIP_UNIT] ?: PrecipitationUnit.MM.name) }
            .getOrDefault(PrecipitationUnit.MM)
    }

    suspend fun setPrecipitationUnit(unit: PrecipitationUnit) {
        context.dataStore.edit { it[Keys.PRECIP_UNIT] = unit.name }
    }

    // ------------------------------------------------------------- privacy

    /**
     * Persists across app restarts, however long the gap — once a person
     * switches the Privacy Policy screen to English, it reopens in English
     * every time after, until they switch it back themselves.
     */
    val privacyPolicyLanguage: Flow<PrivacyPolicyLanguage> = context.dataStore.data.map { prefs ->
        runCatching { PrivacyPolicyLanguage.valueOf(prefs[Keys.PRIVACY_POLICY_LANGUAGE] ?: PrivacyPolicyLanguage.ARABIC.name) }
            .getOrDefault(PrivacyPolicyLanguage.ARABIC)
    }

    suspend fun setPrivacyPolicyLanguage(language: PrivacyPolicyLanguage) {
        context.dataStore.edit { it[Keys.PRIVACY_POLICY_LANGUAGE] = language.name }
    }

    // ------------------------------------------------------------- location

    suspend fun saveLastLocation(location: GeoLocation) {
        context.dataStore.edit { it[Keys.LAST_LOCATION_JSON] = json.encodeToString(location) }
    }

    suspend fun getLastLocation(): GeoLocation? {
        val raw = context.dataStore.data.first()[Keys.LAST_LOCATION_JSON] ?: return null
        return runCatching { json.decodeFromString<GeoLocation>(raw) }.getOrNull()
    }

    suspend fun addRecentSearch(location: GeoLocation) {
        val current = getRecentSearches().filterNot {
            it.name == location.name && it.admin1 == location.admin1 && it.country == location.country
        }
        val updated = (listOf(location) + current).take(8)
        context.dataStore.edit { it[Keys.RECENT_SEARCHES_JSON] = json.encodeToString(updated) }
    }

    suspend fun getRecentSearches(): List<GeoLocation> {
        val raw = context.dataStore.data.first()[Keys.RECENT_SEARCHES_JSON] ?: return emptyList()
        return runCatching { json.decodeFromString<List<GeoLocation>>(raw) }.getOrDefault(emptyList())
    }

    suspend fun clearRecentSearches() {
        context.dataStore.edit { it.remove(Keys.RECENT_SEARCHES_JSON) }
    }

    // --------------------------------------------------------- update system

    suspend fun savePendingDownload(downloadId: Long, targetVersionCode: Int) {
        context.dataStore.edit {
            it[Keys.PENDING_DOWNLOAD_ID] = downloadId
            it[Keys.PENDING_DOWNLOAD_VERSION_CODE] = targetVersionCode
        }
    }

    suspend fun getPendingDownloadId(): Long? = context.dataStore.data.first()[Keys.PENDING_DOWNLOAD_ID]

    suspend fun clearPendingDownload() {
        context.dataStore.edit {
            it.remove(Keys.PENDING_DOWNLOAD_ID)
            it.remove(Keys.PENDING_DOWNLOAD_VERSION_CODE)
        }
    }

    suspend fun setLastSeenRemoteVersionCode(code: Int) {
        context.dataStore.edit { it[Keys.LAST_SEEN_REMOTE_VERSION_CODE] = code }
    }
}
