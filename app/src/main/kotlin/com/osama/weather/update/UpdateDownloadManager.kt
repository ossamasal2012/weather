package com.osama.weather.update

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.osama.weather.R
import com.osama.weather.data.local.PreferencesManager
import com.osama.weather.util.UpdateConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

enum class DownloadStatus { PENDING, RUNNING, PAUSED, SUCCESSFUL, FAILED, NOT_FOUND }

data class DownloadProgress(
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val percent: Int,
    val status: DownloadStatus
)

/**
 * Downloads the update APK through the system [DownloadManager] — which means
 * the OS itself owns the transfer, survives the app being backgrounded or
 * swiped away, shows the standard system progress notification, and keeps
 * going over the same Wi-Fi/mobile connection the rest of the device uses.
 * This class only adds: a polling bridge so the in-app progress bar shows the
 * *real* live percentage, and the automatic-install wiring.
 */
class UpdateDownloadManager(
    private val context: Context,
    private val preferences: PreferencesManager
) {
    private val downloadManager: DownloadManager
        get() = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var receiver: DownloadCompleteReceiver? = null

    /** Call once, for the whole app-process lifetime (see WeatherApplication.onCreate). */
    fun startListeningForCompletion() {
        if (receiver != null) return
        val r = DownloadCompleteReceiver { completedId ->
            scope.launch {
                val pendingId = preferences.getPendingDownloadId()
                if (pendingId != null && pendingId == completedId) {
                    triggerInstall(completedId)
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            r,
            android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        receiver = r
    }

    fun canInstallPackages(): Boolean = context.packageManager.canRequestPackageInstalls()

    fun installPermissionSettingsIntent(): Intent =
        Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            .setData("package:${context.packageName}".toUri())

    /** Starts (or resumes-by-restarting) the update download and persists it so it survives process death. */
    suspend fun enqueueDownload(apkUrl: String, targetVersionCode: Int): Long {
        val fileName = UpdateConfig.DOWNLOADED_APK_FILENAME
        // Clear out any stale file from a previous update attempt.
        runCatching {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?.resolve(fileName)
                ?.takeIf { it.exists() }
                ?.delete()
        }

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle(context.getString(R.string.app_name))
            .setDescription(context.getString(R.string.update_downloading))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType("application/vnd.android.package-archive")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val id = downloadManager.enqueue(request)
        preferences.savePendingDownload(id, targetVersionCode)
        return id
    }

    /** Polls DownloadManager every 250ms for a live, real percentage — completes once the download finishes or fails. */
    fun observeProgress(downloadId: Long): Flow<DownloadProgress> = flow {
        while (true) {
            val progress = queryProgress(downloadId)
            emit(progress)
            if (progress.status == DownloadStatus.SUCCESSFUL ||
                progress.status == DownloadStatus.FAILED ||
                progress.status == DownloadStatus.NOT_FOUND
            ) {
                break
            }
            delay(250)
        }
    }

    private fun queryProgress(downloadId: Long): DownloadProgress {
        val query = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(query).use { cursor ->
            if (!cursor.moveToFirst()) {
                return DownloadProgress(0, 0, 0, DownloadStatus.NOT_FOUND)
            }
            val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val totalBytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val statusCode = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

            val status = when (statusCode) {
                DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
                DownloadManager.STATUS_RUNNING -> DownloadStatus.RUNNING
                DownloadManager.STATUS_PAUSED -> DownloadStatus.PAUSED
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.SUCCESSFUL
                else -> DownloadStatus.FAILED
            }
            val percent = if (totalBytes > 0) ((bytesDownloaded * 100) / totalBytes).toInt() else 0
            return DownloadProgress(bytesDownloaded, totalBytes, percent.coerceIn(0, 100), status)
        }
    }

    suspend fun checkPendingDownloadOnLaunch(): DownloadStatus? {
        val pendingId = preferences.getPendingDownloadId() ?: return null
        val progress = queryProgress(pendingId)
        when (progress.status) {
            DownloadStatus.SUCCESSFUL -> triggerInstall(pendingId)
            DownloadStatus.FAILED, DownloadStatus.NOT_FOUND -> preferences.clearPendingDownload()
            else -> Unit // still running — DownloadManager keeps it going regardless; nothing to do
        }
        return progress.status
    }

    suspend fun triggerInstall(downloadId: Long) {
        val uri = runCatching { downloadManager.getUriForDownloadedFile(downloadId) }.getOrNull()
        if (uri == null) {
            preferences.clearPendingDownload()
            return
        }
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { context.startActivity(installIntent) }
        preferences.clearPendingDownload()
    }

    /** Fallback constant used only when a caller has no other URL to try (see UpdateConfig). */
    fun defaultApkUrl(): String = UpdateConfig.APK_DOWNLOAD_URL
}
