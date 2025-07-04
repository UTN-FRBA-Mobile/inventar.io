package ar.edu.utn.frba.inventario.screens.shipment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.item.Item
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.composables.utils.BranchLocationBar
import ar.edu.utn.frba.inventario.composables.utils.CardItem
import ar.edu.utn.frba.inventario.composables.utils.EmptyResultsMessage
import ar.edu.utn.frba.inventario.composables.utils.Spinner
import ar.edu.utn.frba.inventario.composables.utils.StatusFilter
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.ShipmentsViewModel
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

@Composable
fun ShipmentsScreen(
    navController: NavController,
    shipmentsViewModel: ShipmentsViewModel = hiltViewModel(),
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    val selectedStatusListVM by shipmentsViewModel.selectedStatusList.collectAsStateWithLifecycle()
    val branchName by userScreenViewModel.branchLocationName.collectAsStateWithLifecycle()
    val loading by shipmentsViewModel.loading.collectAsStateWithLifecycle()
    val error by shipmentsViewModel.error.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        BranchLocationBar(
            branchName = branchName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
        StatusFilter(
            statusList = ItemStatus.entries,
            selectedStatusList = selectedStatusListVM,
            onStatusSelected = { shipmentsViewModel.updateSelectedStatusList(it) },
            onClearFilters = { shipmentsViewModel.clearFilters() },
            modifier = Modifier
                .fillMaxWidth()
        )

        LaunchedEffect(Unit) {
            shipmentsViewModel.getShipments()
        }
        ShipmentsBodyContent(navController, shipmentsViewModel.getFilteredItems(), loading, error)
    }
}

@Composable
fun ShipmentsBodyContent(
    navController: NavController,
    shipments: List<Shipment>,
    loading: Boolean,
    error: String?
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
        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Spinner(true)
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            shipments.isEmpty() -> {
                EmptyResultsMessage(
                    message = stringResource(R.string.no_results_for_filters),
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(shipments) { _, shipment ->
                        CardItem(navController, shipment, onItemClick = { clickedItem: Item ->
                            navController.navigate(Screen.ShipmentDetail.route + "/${clickedItem.id}")
                        })
                    }
                }
            }
        }
    }
}