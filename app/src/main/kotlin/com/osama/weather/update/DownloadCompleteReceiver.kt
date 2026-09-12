package com.osama.weather.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Fires whenever ANY DownloadManager download finishes. Registered once for
 * the app process's whole lifetime (see WeatherApplication), so as long as
 * the process is alive — foreground or backgrounded — a completed update
 * download is installed immediately, matching the brief. If the process was
 * killed entirely, [UpdateDownloadManager.checkPendingDownloadOnLaunch] covers
 * the same case on the next app open instead.
 */
class DownloadCompleteReceiver(
    private val onDownloadComplete: (downloadId: Long) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (id != -1L) onDownloadComplete(id)
    }
}
