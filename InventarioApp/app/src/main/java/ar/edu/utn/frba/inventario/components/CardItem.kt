package ar.edu.utn.frba.inventario.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.model.item.Item
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.utils.Screen

@Composable
fun CardItem(navController: NavController, item: Item) {
    Card(onClick = {navController.navigate(Screen.Shipment.route + "/${item.id}")},
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        CardContent(item)
    }
}

@Composable
private fun CardContent(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusIcon(item.status)
        CardText(item)
    }
}

@Composable
private fun CardText(item: Item) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(8.dp)) {
        StatusTitle(item.status.displayName, item.status.color)
        CardItemTitle(item.getDisplayName())
        CardItemDetail(item.getCardDetail(context))
    }
}

@Composable
private fun StatusIcon(status: ItemStatus) {
    Box(
        modifier = Modifier
            .width(56.dp)
            .padding(start = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = status.iconResourceId),
            contentDescription = "Ícono de estado",
            modifier = Modifier.fillMaxSize(0.9f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun StatusTitle(text: String, color: Color) { //ok
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun CardItemTitle(displayName: String) {
    Text(
        text = displayName,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun CardItemDetail(detail: String) {
    Text(
        text = detail,
        style = MaterialTheme.typography.bodySmall
    )
}