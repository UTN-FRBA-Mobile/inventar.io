package ar.edu.utn.frba.inventario.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.composables.utils.Spinner
import ar.edu.utn.frba.inventario.utils.OrderProductsListArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs
import ar.edu.utn.frba.inventario.viewmodels.OrderResultViewModel

@Composable
fun OrderResultScreen(
    navController: NavController,
    code: String?,
    codeType: String,
    errorMessage: String?,
    origin: String,
    viewModel: OrderResultViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val apiError by viewModel.errorMessage.collectAsState()
    val foundOrder by viewModel.foundOrder.collectAsState()

    LaunchedEffect(code, errorMessage) {
        if (errorMessage != null) {
            viewModel._errorMessage.value = errorMessage
            viewModel._isLoading.value = false
        } else {
            viewModel.loadOrderById(code, codeType)
        }
    }

    if (isLoading) {
        Spinner(true)
        return
    }

    OrderResultBodyContent(
        navController = navController,
        foundOrder = foundOrder,
        apiError = apiError,
        codeType = codeType
    )
}

@Composable
fun OrderResultBodyContent(
    navController: NavController,
    foundOrder: OrderResponse?,
    apiError: String?,
    codeType: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (apiError != null) {
            Text(
                text = stringResource(R.string.search_failed),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(Modifier.height(8.dp))
            Text(
                apiError,
                fontSize = 18.sp,
            )
        }
        else if (foundOrder != null) {
            Text(
                text = stringResource(R.string.order_result_search_success),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.order_id, foundOrder.id.toString()),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = foundOrder.sender,
                fontSize = 22.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.unique_total_products, foundOrder.productAmount.size),
                fontSize = 22.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Button(onClick = {
                foundOrder.id?.let { id ->
                    val destination = Screen.OrderProductsList.withNavArgs(
                        OrderProductsListArgs.OrderId to id.toString()
                    )
                    navController.navigate(destination)
                }
            }) {
                Text(stringResource(R.string.continue_button))
            }

        } else {
            Text("Error desconocido o datos no disponibles", fontSize = 16.sp, color = Color.Gray)
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            navController.popBackStack() // Para volver a la pantalla de escaneo
        }) {
            Text(stringResource(R.string.try_again))
        }
    }
}