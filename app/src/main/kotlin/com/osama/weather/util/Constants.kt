package com.osama.weather.util

/**
 * Central place for every URL and tunable constant in the app. The
 * Open-Meteo endpoints below are real, public, and keyless. The update
 * channel (see [UpdateConfig]) is proxied through a Cloudflare Worker rather
 * than pointing at GitHub directly.
 */
object ApiConfig {

    /** Weather forecast API — current + hourly + daily + 15-minute nowcast. */
    const val FORECAST_BASE_URL = "https://api.open-meteo.com/v1/forecast"

    /** Air quality API — pollutants + European & US AQI, hourly, 7-day. */
    const val AIR_QUALITY_BASE_URL = "https://air-quality-api.open-meteo.com/v1/air-quality"

    /** Geocoding API (also by Open-Meteo) — smart city/place search + localized names. */
    const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search"

    /** How many days of daily + hourly forecast to request. Open-Meteo supports up to 16. */
    const val FORECAST_DAYS = 16

    /** How many 15-minute steps to request for the short-term precipitation nowcast (24h). */
    const val MINUTELY_15_STEPS = 96

    /** How many days of hourly air-quality forecast to request. */
    const val AIR_QUALITY_FORECAST_DAYS = 7

    /** Network timeouts, in seconds. */
    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 20L
}

/**
 * Configuration for the update channel. On the developer's explicit
 * instruction, the app NEVER references github.com directly anywhere in its
 * code — every update check and every APK download goes through a
 * Cloudflare Worker that proxies the real GitHub Release server-side. The
 * worker's own address is the only thing baked into the compiled app; the
 * actual repository location it proxies to is configured entirely on the
 * Cloudflare side (see /server/cloudflare-worker.js in this project) and is
 * never visible to anyone inspecting the APK.
 */
object UpdateConfig {
    /** Base address of the Cloudflare Worker proxy. No trailing slash. */
    private const val WORKER_BASE = "https://weather-apk-download.ossamasal2012.workers.dev"

    /** The manifest the app polls on every cold start. */
    const val VERSION_MANIFEST_URL = "$WORKER_BASE/version.json"

    /** Fallback used only if version.json doesn't carry its own apk_url field. */
    const val APK_DOWNLOAD_URL = "$WORKER_BASE/download"

    const val DOWNLOADED_APK_FILENAME = "weather.apk"
}
