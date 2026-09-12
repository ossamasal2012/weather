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

    /** A fresh, high-accuracy GPS fix — the brief explicitly wants precise, not coarse, positioning. */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentFix(): Result<Pair<Double, Double>> {
        if (!hasLocationPermission()) return Result.failure(SecurityException("Location permission not granted"))

        return runCatching {
            withContext(Dispatchers.IO) {
                val client = LocationServices.getFusedLocationProviderClient(context)
                val cancellationSource = CancellationTokenSource()
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(60_000L)
                    .build()

                val location = suspendCancellableCoroutine { continuation ->
                    client.getCurrentLocation(request, cancellationSource.token)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                continuation.resume(location)
                            } else {
                                continuation.resumeWithException(IllegalStateException("Null location fix"))
                            }
                        }
                        .addOnFailureListener { e -> continuation.resumeWithException(e) }

                    continuation.invokeOnCancellation { cancellationSource.cancel() }
                }

                location.latitude to location.longitude
            }
        }
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
                    name = address.locality
                        ?: address.subAdminArea
                        ?: address.adminArea
                        ?: address.countryName.orEmpty(),
                    admin1 = address.adminArea,
                    admin2 = address.subAdminArea,
                    country = address.countryName,
                    countryCode = address.countryCode,
                    latitude = latitude,
                    longitude = longitude,
                    timezone = null,
                    isCurrentLocation = true
                )
            }.getOrDefault(fallback)
        }
}
