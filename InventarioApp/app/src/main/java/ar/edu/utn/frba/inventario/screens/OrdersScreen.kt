package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.components.BranchLocationBar
import ar.edu.utn.frba.inventario.components.CardItem
import ar.edu.utn.frba.inventario.components.StatusFilter
import ar.edu.utn.frba.inventario.viewmodels.OrdersViewModel


@Composable
fun OrdersScreen(viewModel: OrdersViewModel = hiltViewModel(), navController: NavController) {
    Scaffold(bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            BranchLocationBar(
                branchName = "Centro", //Harcodeado, luego deberíamos obtenerlo a partir del dato del usuario
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            StatusFilter(
                statusList = ItemStatus.values().toList(),
                selectedStatusList = viewModel.selectedStatuses.value,
                onStatusSelected = { viewModel.updateSelectedStatuses(it) },
                onClearFilters = { viewModel.clearFilters() },
                modifier = Modifier
                    .fillMaxWidth()
            )
            HomeBodyContent(
                orders = viewModel.getFilteredOrders(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HomeBodyContent(
    orders: List<Order>,
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
            itemsIndexed(orders) { _, order ->
                CardItem(item = order)
            }
        }
    }
}

/*
HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    Scaffold(bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            BranchLocationBar(
                branchName = "Centro", //Harcodeado, luego deberíamos obtenerlo a partir del dato del usuario
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            StatusFilter(
                statusList = ShipmentStatus.values().toList(),
                selectedStatusList = viewModel.selectedStatuses.value,
                onStatusSelected = { viewModel.updateSelectedStatuses(it) },
                onClearFilters = { viewModel.clearFilters() },
                modifier = Modifier
                    .fillMaxWidth()
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
                CardItem(item = shipment)
            }
        }
    }
}
 */