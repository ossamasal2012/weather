package com.osama.weather.update

import com.osama.weather.BuildConfig
import com.osama.weather.data.remote.NetworkModule
import com.osama.weather.update.model.UpdateCheckResult
import com.osama.weather.update.model.VersionInfo
import com.osama.weather.util.UpdateConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.Request

/**
 * Implements the update-check half of the brief: on every cold start, fetch
 * version.json from the GitHub "latest" release and compare its version_code
 * against BuildConfig.VERSION_CODE (the number Gradle burned into this build
 * from app/build.gradle.kts). A strictly-greater remote version means an
 * update is available.
 */
class UpdateManager {

    suspend fun checkForUpdate(): UpdateCheckResult = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(UpdateConfig.VERSION_MANIFEST_URL)
                // The "latest" release asset is overwritten on every publish, so
                // never trust any HTTP cache — always get the byte-for-byte
                // current file.
                .cacheControl(CacheControl.Builder().noCache().noStore().build())
                .get()
                .build()

            NetworkModule.okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext UpdateCheckResult.Error("HTTP ${response.code}")
                }
                val body = response.body?.string().orEmpty()
                val info: VersionInfo = NetworkModule.json.decodeFromString(VersionInfo.serializer(), body)

                if (info.versionCode > BuildConfig.VERSION_CODE) {
                    UpdateCheckResult.UpdateAvailable(info)
                } else {
                    UpdateCheckResult.UpToDate
                }
            }
        }.getOrElse { e -> UpdateCheckResult.Error(e.message ?: "Unknown error") }
    }
}
