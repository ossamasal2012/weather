package com.osama.weather.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.osama.weather.domain.model.GeoLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Wraps GPS acquisition (FusedLocationProviderClient) and reverse geocoding
 * (Android's on-device Geocoder) behind two simple suspend functions.
 */
class LocationHelper(private val context: Context) {

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * Resolves the device's current coordinates, favoring speed when a
     * recent fix is already cached (e.g. switching back to "my location"
     * after viewing a searched city) while still guaranteeing a precise fix
     * when nothing recent is available.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentFix(): Result<Pair<Double, Double>> {
        if (!hasLocationPermission()) return Result.failure(SecurityException("Location permission not granted"))

        return runCatching {
            withContext(Dispatchers.IO) {
                val client = LocationServices.getFusedLocationProviderClient(context)

                // Fast path: Play Services keeps a recent fix cached virtually
                // for free — if it's fresh enough, use it immediately instead
                // of waiting on a brand new GPS/network request.
                val cached = runCatching {
                    suspendCancellableCoroutine<android.location.Location?> { continuation ->
                        client.lastLocation
                            .addOnSuccessListener { continuation.resume(it) }
                            .addOnFailureListener { continuation.resume(null) }
                    }
                }.getOrNull()

                val cachedIsFresh = cached != null &&
                    (System.currentTimeMillis() - cached.time) < CACHED_FIX_MAX_AGE_MILLIS

                val location = if (cachedIsFresh) {
                    cached!!
                } else {
                    val cancellationSource = CancellationTokenSource()
                    val request = CurrentLocationRequest.Builder()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setMaxUpdateAgeMillis(60_000L)
                        .build()

                    suspendCancellableCoroutine { continuation ->
                        client.getCurrentLocation(request, cancellationSource.token)
                            .addOnSuccessListener { fresh ->
                                if (fresh != null) {
                                    continuation.resume(fresh)
                                } else {
                                    continuation.resumeWithException(IllegalStateException("Null location fix"))
                                }
                            }
                            .addOnFailureListener { e -> continuation.resumeWithException(e) }

                        continuation.invokeOnCancellation { cancellationSource.cancel() }
                    }
                }

                location.latitude to location.longitude
            }
        }
    }

    private companion object {
        const val CACHED_FIX_MAX_AGE_MILLIS = 5 * 60 * 1000L // 5 minutes
    }

    /**
     * Turns coordinates into a human place name via the on-device Geocoder,
     * assembling the exact "City - Governorate - Country" hierarchy the app
     * displays everywhere. Gracefully degrades to a generic "current location"
     * result (still usable for weather lookups) on devices with no geocoding
     * backend — this never blocks showing the weather itself.
     */
    @Suppress("DEPRECATION")
    suspend fun reverseGeocode(latitude: Double, longitude: Double): GeoLocation =
        withContext(Dispatchers.IO) {
            val fallback = GeoLocation(
                id = -1L,
                name = "",
                admin1 = null,
                admin2 = null,
                country = null,
                countryCode = null,
                latitude = latitude,
                longitude = longitude,
                timezone = null,
                isCurrentLocation = true
            )

            if (!Geocoder.isPresent()) return@withContext fallback

            runCatching {
                val geocoder = Geocoder(context, java.util.Locale.forLanguageTag("ar"))
                val results = geocoder.getFromLocation(latitude, longitude, 1)
                val address = results?.firstOrNull() ?: return@withContext fallback

                GeoLocation(
                    id = -1L,
                    name = normalizeArabicAdminName(
                        address.locality ?: address.subAdminArea ?: address.adminArea ?: address.countryName.orEmpty()
                    ).orEmpty(),
                    admin1 = normalizeArabicAdminName(address.adminArea),
                    admin2 = normalizeArabicAdminName(address.subAdminArea),
                    country = address.countryName,
                    countryCode = address.countryCode,
                    latitude = latitude,
                    longitude = longitude,
                    timezone = null,
                    isCurrentLocation = true
                )
            }.getOrDefault(fallback)
        }

    /**
     * The device Geocoder sometimes returns administrative names with the
     * unit word trailing — "بابل محافظة" — following an English-style word
     * order ("Babil Governorate") rather than correct Arabic ("محافظة بابل").
     * This detects a small set of common Arabic administrative-unit words at
     * the *end* of the string and moves them to the front. Names that are
     * already correctly ordered, or that don't end in one of these words,
     * pass through unchanged.
     */
    private fun normalizeArabicAdminName(raw: String?): String? {
        if (raw.isNullOrBlank()) return raw
        val trimmed = raw.trim()
        val unitWords = listOf("محافظة", "منطقة", "إقليم", "ولاية", "مقاطعة", "إمارة")
        for (word in unitWords) {
            if (trimmed.endsWith(" $word")) {
                val name = trimmed.removeSuffix(" $word").trim()
                if (name.isNotEmpty()) return "$word $name"
            }
        }
        return trimmed
    }
}
