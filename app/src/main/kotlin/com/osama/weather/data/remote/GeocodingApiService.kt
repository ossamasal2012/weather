package com.osama.weather.data.remote

import com.osama.weather.data.remote.dto.GeocodingResponseDto
import com.osama.weather.util.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

/**
 * Talks to https://geocoding-api.open-meteo.com/v1/search — a free, keyless,
 * fuzzy, multi-language place search (2 characters match exact names, 3+
 * characters use normalized fuzzy prefix matching, so typing "بغد" already
 * surfaces "بغداد"). Requested in Arabic; the API itself falls back to the
 * native/English name whenever no Arabic translation exists, so no extra
 * client-side fallback call is needed.
 */
class GeocodingApiService {

    suspend fun search(query: String, count: Int = 12): Result<GeocodingResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (query.trim().length < 2) return@runCatching GeocodingResponseDto(results = emptyList())

                val url = ApiConfig.GEOCODING_BASE_URL.toHttpUrl().newBuilder()
                    .addQueryParameter("name", query.trim())
                    .addQueryParameter("count", count.toString())
                    .addQueryParameter("language", "ar")
                    .addQueryParameter("format", "json")
                    .build()

                val request = Request.Builder().url(url).get().build()

                NetworkModule.okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("Geocoding request failed: HTTP ${response.code}")
                    }
                    val body = response.body?.string().orEmpty()
                    NetworkModule.json.decodeFromString(GeocodingResponseDto.serializer(), body)
                }
            }
        }
}
