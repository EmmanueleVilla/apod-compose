package com.shadowings.apodcompose.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shadowings.apodcompose.MainActivity

/**
 * Image Downloader object
 */
class ImageDownloader {
    /**
     * Request to download an image.
     * Opens the permission popups instead if the permission is not granted
     */
    fun requestImageDownload(date: String, url: String, context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                123
            )
        } else {
            executeDownload(date, url, context)
        }
    }

    private fun executeDownload(date: String, url: String, context: Context) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle("APOD $date")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    date
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(
            context,
            "Downloading.. Check the notifications",
            Toast.LENGTH_LONG
        ).show()
    }
}