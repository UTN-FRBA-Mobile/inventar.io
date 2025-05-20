package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.viewmodels.HomeViewModel
import ar.edu.utn.frba.inventario.viewmodels.LoginViewModel

@Composable
fun Home(viewModel: HomeViewModel = hiltViewModel(), navController: NavController){

    Scaffold(
        topBar = {TopBar("Central")},
        bottomBar ={ BottomNavigationBar(navController) }
    ) { innerPadding ->
        HomeBodyContent(
            innerPadding = innerPadding,
            shipments = viewModel.getSortedShipments()
        )

    }

}

@Composable
fun HomeBodyContent(innerPadding: PaddingValues, shipments: List<Shipment>) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Text(
            text = "Envíos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(shipments) { _, shipment ->
                ShipmentItem(shipment = shipment)
            }
            }
        }
    }


@Composable
fun ShipmentItem(shipment: Shipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = shipment.status.color // Fondo
        ),
        border = BorderStroke(2.dp, shipment.status.color) // Borde
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = shipment.number, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Estado: ${shipment.status.name.replace("_", " ")}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}