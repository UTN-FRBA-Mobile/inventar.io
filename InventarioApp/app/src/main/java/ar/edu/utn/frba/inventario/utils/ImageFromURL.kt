package ar.edu.utn.frba.inventario.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageFromURL(url: String?, modifier: Modifier = Modifier) {
    if (url.isNullOrBlank()) {
        Text(text = "Sin imagen")
        return
    }

    val painter = rememberAsyncImagePainter(url)

    if (painter.state is AsyncImagePainter.State.Error) {
        Text(text = "Error al cargar la imagen")
        return
    }

    AsyncImage(
        model = url,
        contentDescription = "Imagen desde URL",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}