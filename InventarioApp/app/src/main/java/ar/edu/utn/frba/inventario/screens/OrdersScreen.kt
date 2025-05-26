package ar.edu.utn.frba.inventario.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.components.BranchLocationBar
import ar.edu.utn.frba.inventario.components.CardItem
import ar.edu.utn.frba.inventario.components.StatusFilter
import ar.edu.utn.frba.inventario.viewmodels.OrdersViewModel


@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    navController: NavController
) {
    val selectedStatusList by viewModel.selectedStatusList.collectAsStateWithLifecycle()

    // Usa rememberUpdatedState para el estado que podría cambiar durante la recomposición
    val currentStatusList by rememberUpdatedState(selectedStatusList)

    LaunchedEffect(currentStatusList) {
        Log.d("FILTER_DEBUG", "Filtros actualizados: ${viewModel.savedStateHandle.get<List<String>>("orders_filter")}")
    }
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
                modifier = Modifier //Posiblemente varíe la forma de usar el componente también
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )



            val filteredOrders by viewModel.selectedStatusList.collectAsStateWithLifecycle().let { state ->
                derivedStateOf { viewModel.getFilteredItems() }
            }

            StatusFilter(
                statusList = ItemStatus.values().toList(),
                selectedStatusList = selectedStatusList,
                onStatusSelected = { viewModel.updateSelectedStatusList(it) },
                onClearFilters = { viewModel.clearFilters() },
                modifier = Modifier
                    .fillMaxWidth()
            )



            OrderBodyContent(
                orders = filteredOrders,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OrderBodyContent(
    orders: List<Order>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Pedidos",
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