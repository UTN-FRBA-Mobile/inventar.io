package ar.edu.utn.frba.inventario.composables.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.inventario.R
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun AnimatedBuildingGif() {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(R.drawable.building)
            .crossfade(true)
            .build(),
        contentDescription = "GIF animado",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(300.dp)
            .clip(CircleShape),
    )
}