package ar.edu.utn.frba.inventario.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.components.BranchLocationBar
import ar.edu.utn.frba.inventario.components.CardItem
import ar.edu.utn.frba.inventario.components.StatusFilter
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.OrdersViewModel


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val selectedStatusList by viewModel.selectedStatusList.collectAsStateWithLifecycle()

    Scaffold(bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            BranchLocationBar(
                branchName = "Centro", //TODO Harcodeado, luego deberíamos obtenerlo a partir del dato del usuario
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            StatusFilter(
                statusList = ItemStatus.entries,
                selectedStatusList = selectedStatusList,
                onStatusSelected = { viewModel.updateSelectedStatusList(it) },
                onClearFilters = { viewModel.clearFilters() },
                modifier = Modifier
                    .fillMaxWidth()
            )
            OrderBodyContent(
                navController,
                viewModel.getFilteredItems(),
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OrderBodyContent(
    navController: NavController,
    orders: List<Order>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
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

        FloatingActionButton(
            onClick = { navController.navigate(Screen.Scan.route) },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(40.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "Escanear",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp)
            )
        }

    }
}
