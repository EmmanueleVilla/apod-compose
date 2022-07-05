package com.shadowings.apodcompose.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shadowings.apodcompose.R
import com.shadowings.apodcompose.StoreInterface
import com.shadowings.apodcompose.redux.AppState

@Preview
@Composable
private fun HomeComposablePreview() {
    HomeComposable(
        appState = AppState(),
        lifecycleOwner = LocalLifecycleOwner.current
    )
}

/**
 * Composable of the home page
 */
@Composable
fun HomeComposable(
    appState: AppState,
    lifecycleOwner: LifecycleOwner,
    navController: NavHostController = rememberNavController()
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                StoreInterface().dispatchAction(HomeActions.Init())
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val items = appState.home.apods

    if (items.isEmpty()) {
        ApodsGrid(apods = (1..30).map { ApodModel() }, navController)
    } else {
        ApodsGrid(apods = items, navController)
    }
}

/**
 * Composable that shows a grid of the given apods
 */
@Composable
fun ApodsGrid(apods: List<ApodModel>, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    LazyVerticalGrid(
        columns = GridCells.Adaptive(screenWidth / 2),
        horizontalArrangement = Arrangement.Center,
        content = {
            items(apods.size) { index ->
                val apod = apods[index]
                if (apod.toThumbnail().isBlank()) {
                    Box(
                        modifier = Modifier
                            .width(screenWidth / 2)
                            .height(screenWidth / 2)
                    ) {
                        Image(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize(),
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = "placeholder"
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .width(screenWidth / 2)
                            .height(screenWidth / 2)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .width(screenWidth / 2)
                                .height(screenWidth / 2)
                                .align(Alignment.Center)
                                .clickable {
                                    navController.navigate("detail/" + apod.date)
                                },
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(apod.toThumbnail())
                                .crossfade(true)
                                .crossfade(500)
                                .build(),
                            contentDescription = apod.date,
                            placeholder = painterResource(id = R.drawable.placeholder),
                            contentScale = ContentScale.Crop,
                        )
                        if (apod.mediaType == "video") {
                            Image(
                                modifier = Modifier
                                    .width(screenWidth / 10)
                                    .height(screenWidth / 10)
                                    .align(Alignment.BottomEnd),
                                imageVector = Icons.Default.PlayArrow,
                                colorFilter = ColorFilter.tint(Color.White),
                                contentDescription = "Play"
                            )
                        }
                    }
                }
            }
        }
    )
}