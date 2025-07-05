package ar.edu.utn.frba.inventario.screens.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.composables.utils.BranchLocationBar
import ar.edu.utn.frba.inventario.composables.utils.CardItem
import ar.edu.utn.frba.inventario.composables.utils.EmptyResultsMessage
import ar.edu.utn.frba.inventario.composables.utils.Spinner
import ar.edu.utn.frba.inventario.composables.utils.StatusFilter
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.OrdersViewModel
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel


@Composable
fun OrdersScreen(
    navController: NavController,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    val selectedStatusList by ordersViewModel.selectedStatusList.collectAsStateWithLifecycle()
    val branchName by userScreenViewModel.branchLocationName.collectAsStateWithLifecycle()
    val loading by ordersViewModel.loading.collectAsStateWithLifecycle()
    val error by ordersViewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        ordersViewModel.getOrders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BranchLocationBar(
            branchName = branchName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
        StatusFilter(
            statusList = ItemStatus.entries,
            selectedStatusList = selectedStatusList,
            onStatusSelected = { ordersViewModel.updateSelectedStatusList(it) },
            onClearFilters = { ordersViewModel.clearFilters() },
            modifier = Modifier
                .fillMaxWidth()
        )
        OrderBodyContent(
            navController,
            ordersViewModel.getFilteredItems(),
            loading,
            error,
            Modifier.weight(1f)
        )
    }
}

@Composable
fun OrderBodyContent(
    navController: NavController,
    orders: List<Order>,
    loading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Pedidos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
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

                orders.isEmpty() -> {
                    EmptyResultsMessage(
                        message = stringResource(R.string.no_results_for_filters),
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(orders) { _, order ->
                            CardItem(navController, order, onItemClick = { clickedItem ->
                                navController.navigate(Screen.OrderDetail.route + "/${clickedItem.id}")
                            })
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate(Screen.Scan.route + "?origin=order") },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(18.dp)
                .width(180.dp)
                .height(40.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = stringResource(R.string.scan),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp)
            )
        }
    }
}