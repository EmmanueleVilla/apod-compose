package com.shadowings.apodcompose.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shadowings.apodcompose.StoreInterface
import com.shadowings.apodcompose.home.ApodModel
import com.shadowings.apodcompose.redux.AppState

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
                    explanation = "It is difficult to hide a galaxy behind a cluster of galaxies." +
                            "  The closer cluster's gravity will act like a huge lens, pulling" +
                            " images of the distant galaxy around the sides and greatly distorting" +
                            " them.  This is just the case observed in the featured image recently" +
                            " re-processed image from the Hubble Space Telescope." +
                            "  The cluster GAL-CLUS-022058c is composed of many galaxies and is" +
                            "  lensing the image of a yellow-red background galaxy into arcs seen" +
                            " around the image center.  Dubbed a molten Einstein ring for its" +
                            " unusual shape, four images of the same background galaxy have been" +
                            " identified. Typically, a foreground galaxy cluster can only create" +
                            " such smooth arcs if most of its mass is smoothly distributed -- and" +
                            " therefore not concentrated in the cluster galaxies visible." +
                            "  Analyzing the positions of these gravitational arcs gives" +
                            " astronomers a method to estimate the dark matter distribution in" +
                            " galaxy clusters, as well as infer when the stars in these early" +
                            " galaxies began to form.   New APOD Social Mirrors in Arabic: " +
                            "On Facebook, Instagram, and Twitter",
                    hdUrl = "https://apod.nasa.gov/apod/image/2207/MoltenEinsteinRing_HubbleLodge_2972.jpg",
                    mediaType = "image",
                    title = "A Molten Galaxy Einstein Ring",
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
            .verticalScroll(rememberScrollState())) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(apod.thumbnail())
                .crossfade(true)
                .crossfade(500)
                .build(),
            contentDescription = apod.date,
            contentScale = ContentScale.FillWidth,
        )
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
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Download hi-res image")
        }
    }
}