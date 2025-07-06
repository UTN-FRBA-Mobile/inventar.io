package ar.edu.utn.frba.inventario.composables.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.inventario.R

@Composable
fun EmptyResultsMessage(message: String, modifier: Modifier = Modifier) {
    val iconIdByTheme = if (isSystemInDarkTheme()) {
        R.drawable.empty_results_dark
    } else {
        R.drawable.empty_results_light
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-48).dp),
        ) {
            Image(
                painter = painterResource(id = iconIdByTheme),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.padding(horizontal = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
