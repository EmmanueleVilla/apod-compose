package com.shadowings.apodcompose.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shadowings.apodcompose.MainActivity
import com.shadowings.apodcompose.StoreInterface
import com.shadowings.apodcompose.home.ApodModel
import com.shadowings.apodcompose.redux.AppState
import java.io.File

@Preview
@Composable
fun DetailComposableImagePreview() {
    DetailComposable(
        date = "stub",
        appState = AppState(
            detail = DetailState(
                apod =
                ApodModel(
                    date = "stub",
                    explanation = LoremIpsum(100).values.joinToString(""),
                    hdUrl = "https://apod.nasa.gov/apod/image/2207/MoltenEinsteinRing_HubbleLodge_2972.jpg",
                    mediaType = "image",
                    title = LoremIpsum(5).values.joinToString(""),
                    url = "https://apod.nasa.gov/apod/image/2207/MoltenEinsteinRing_HubbleLodge_960.jpg"
                )
            )
        ),
        lifecycleOwner = LocalLifecycleOwner.current
    )
}

@Preview
@Composable
fun DetailComposableVideoPreview() {
    DetailComposable(
        date = "stub",
        appState = AppState(
            detail = DetailState(
                apod =
                ApodModel(
                    date = "stub",
                    explanation = LoremIpsum(100).values.joinToString(""),
                    hdUrl = "https://apod.nasa.gov/apod/image/2207/MoltenEinsteinRing_HubbleLodge_2972.jpg",
                    mediaType = "video",
                    title = LoremIpsum(5).values.joinToString(""),
                    url = "https://apod.nasa.gov/apod/image/2207/MoltenEinsteinRing_HubbleLodge_960.jpg"
                )
            )
        ),
        lifecycleOwner = LocalLifecycleOwner.current
    )
}

@Composable
fun DetailComposable(date: String, appState: AppState, lifecycleOwner: LifecycleOwner) {
    DisposableEffect(lifecycleOwner)
    {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                StoreInterface().dispatchAction(DetailActions.Init(date))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val apod = if (appState.detail.apod.date == date) appState.detail.apod else ApodModel()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (apod.mediaType != "video") {
            DetailImage(url = apod.thumbnail())
        } else {
            Webview(url = apod.url)
        }
        Text(
            modifier = Modifier.padding(8.dp),
            text = apod.title,
            color = Color.White,
            fontSize = 24.sp
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = apod.explanation,
            color = Color.White,
            fontSize = 18.sp
        )
        val context = LocalContext.current
        if (apod.title != "" && apod.hdUrl != "" && apod.mediaType != "video") {
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp),
                onClick = {
                    requestImageDownload(apod.date, apod.hdUrl, context)
                }
            ) {
                Text(text = "Download hi-res image")
            }
        }
    }
}

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

fun executeDownload(date: String, url: String, context: Context) {
    val fileName = date
    val dirName = "apods"

    val direct = File(
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .absolutePath + "/" + dirName + "/"
    )

    if (!direct.exists()) {
        direct.mkdir()
    }
    val request =
        DownloadManager.Request(Uri.parse(url))
            .setTitle("APOD $date")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                File.separator + dirName + File.separator + fileName
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

    val downloadManager =
        context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
    downloadManager!!.enqueue(request)
    Toast.makeText(
        context,
        "Downloading.. Check the notifications",
        Toast.LENGTH_LONG
    ).show()
}

@Composable
fun DetailImage(url: String) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth(),
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .crossfade(500)
            .build(),
        contentDescription = "apod detail image",
        contentScale = ContentScale.FillWidth,
    )
}

@Composable
fun Webview(url: String) {
    val context = LocalContext.current
    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16.0f / 9), factory = {
        WebView(context).apply {
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}