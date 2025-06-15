package ar.edu.utn.frba.inventario.utils

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Base64Image(base64String: String?, modifier: Modifier = Modifier) {
    val imageBitmap = remember(base64String) {
        try {
            val base64Clean = base64String?.substringAfter("base64,", base64String)
            val imageBytes = Base64.decode(base64Clean, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Imagen desde Base64",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Text(text = "Error al cargar la imagen")
    }
}