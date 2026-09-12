package com.osama.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * A resolved place — either picked from search results or produced by
 * reverse-geocoding the device's GPS fix. Serializable so it can be cached
 * locally (last-viewed location, recent search history) with no separate
 * storage-layer DTO needed.
 */
@Serializable
data class GeoLocation(
    val id: Long,
    val name: String,
    val admin1: String?,
    val admin2: String?,
    val country: String?,
    val countryCode: String?,
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
    val population: Long? = null,
    val isCurrentLocation: Boolean = false
) {
    /**
     * The label shown throughout the app, following the exact rule from the
     * brief:
     *  - country only known           -> "العراق"
     *  - city IS the governorate seat -> "بغداد - العراق"            (no repeat)
     *  - city within a governorate    -> "سدة الهندية - بابل - العراق"
     */
    val displayName: String
        get() {
            val parts = mutableListOf<String>()
            val cityDiffersFromAdmin1 = name.isNotBlank() && !name.equals(admin1, ignoreCase = true)

            if (cityDiffersFromAdmin1) parts.add(name)

            if (!admin1.isNullOrBlank()) {
                parts.add(admin1)
            } else if (!cityDiffersFromAdmin1 && name.isNotBlank()) {
                // No admin1 available at all — fall back to the city name itself
                // so we never produce an empty leading segment.
                parts.add(name)
            }

            if (!country.isNullOrBlank()) parts.add(country)

            return if (parts.isEmpty()) name else parts.distinct().joinToString(" - ")
        }

    /** Short label for compact spaces (search rows): "City, Country". */
    val shortLabel: String
        get() = listOfNotNull(name.ifBlank { null }, country).joinToString("، ")
}
