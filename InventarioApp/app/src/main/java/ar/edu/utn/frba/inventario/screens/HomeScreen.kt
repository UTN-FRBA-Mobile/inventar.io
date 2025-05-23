package ar.edu.utn.frba.inventario.screens

import Shipment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.components.ShipmentItem
import ar.edu.utn.frba.inventario.components.StatusFilter
import ar.edu.utn.frba.inventario.viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    Scaffold(bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            StatusFilter(
                statuses = ShipmentStatus.values().toList(),
                selectedStatuses = viewModel.selectedStatuses.value,
                onStatusSelected = { viewModel.updateSelectedStatuses(it) },
                onClearFilters = { viewModel.clearFilters() }
            )
            HomeBodyContent(
                shipments = viewModel.getFilteredShipments(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HomeBodyContent(
    shipments: List<Shipment>,
    modifier: Modifier = Modifier
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
