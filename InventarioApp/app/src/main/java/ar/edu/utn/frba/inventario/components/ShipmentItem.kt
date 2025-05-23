package ar.edu.utn.frba.inventario.components

import Shipment
import ShipmentStatus
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import ar.edu.utn.frba.inventario.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.inventario.utils.format
import java.time.LocalDateTime


@Composable
fun ShipmentItem(shipment: Shipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        ShipmentContent(shipment)
    }
}

@Composable
private fun ShipmentContent(shipment: Shipment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusIcon(shipment.status)
        ShipmentTextContent(shipment)
    }
}

@Composable
private fun StatusIcon(status: ShipmentStatus) {
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
private fun ShipmentTextContent(shipment: Shipment) {
    Column(modifier = Modifier.padding(8.dp)) {
        StatusTitle(shipment.status.displayName, shipment.status.color)
        ShipmentTitle(shipment.number, shipment.customerName)
        ProductsAndDate(shipment.products.size, shipment.creationDate)
    }
}

@Composable
private fun StatusTitle(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun ShipmentTitle(number: String, customer: String) {
    Text(
        text = "$number • $customer",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ProductsAndDate(count: Int, date: LocalDateTime) {
    Text(
        text = pluralStringResource(
            id = R.plurals.products_and_date_template,
            count = count,
            count,
            date.format()
        ),
        style = MaterialTheme.typography.bodySmall
    )
}