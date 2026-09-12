package com.osama.weather.data.remote

import com.osama.weather.util.ApiConfig
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * One shared OkHttp client and one shared JSON parser for the whole app.
 * Kept intentionally dependency-light (no Retrofit) — every endpoint here is
 * a plain HTTPS GET with query parameters, so a thin OkHttp + kotlinx.serialization
 * layer is simpler to reason about and easier to keep error-free than a full
 * retrofit + converter stack.
 */
object NetworkModule {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Lenient on purpose: Open-Meteo occasionally omits a field entirely for
     * an edge location/time rather than returning null in the array, and we
     * never want a single unexpected key to crash parsing of an otherwise
     * valid response.
     */
    val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            explicitNulls = false
        }
    }
}
