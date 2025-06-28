package ar.edu.utn.frba.inventario.screens.scan

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.composables.utils.ImageFromURL
import ar.edu.utn.frba.inventario.composables.utils.Spinner
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.ShipmentProductToScanList
import ar.edu.utn.frba.inventario.utils.ShipmentScanFlowState
import ar.edu.utn.frba.inventario.viewmodels.scan.ProductAmountViewModel


@Composable
fun ProductAmountScreen(
    navController: NavController,
    viewModel: ProductAmountViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val currentProductStock by viewModel.currentProductStock.collectAsState()

    val scannedProduct = ShipmentScanFlowState.scannedProduct
    val selectedShipment = ShipmentScanFlowState.selectedShipment
    val requiredAmount = selectedShipment?.products?.find { it.id == scannedProduct?.id }?.quantity

    if (isLoading) {
        Spinner(true)
        return
    }

    if (error != null || scannedProduct == null || requiredAmount == null || currentProductStock == null) {
        Text(
            text = error ?: stringResource(R.string.product_amount_unexpected_error),
            modifier = Modifier.padding(16.dp),
            color = Color.Red
        )
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(stringResource(R.string.go_back))
        }
        return
    }

    val enoughStock = currentProductStock!! >= requiredAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.product_amount_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = scannedProduct.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!scannedProduct.imageURL.isNullOrBlank()) {
            ImageFromURL(
                url = scannedProduct.imageURL,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .shadow(4.dp, CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = if (enoughStock)
                stringResource(R.string.product_amount_required_quantity, requiredAmount)
            else
                stringResource(R.string.product_amount_insufficient_stock, requiredAmount, scannedProduct.name),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (enoughStock) {
            Button(onClick = {
                // ToDo - Pegarle al endpoint necesario, para confirmar el ingreso del producto
                ShipmentProductToScanList.updateLoadedQuantity(productId = scannedProduct.id, loadedQuantity = requiredAmount)

                navController.navigate(Screen.ShipmentDetail.route + "/${selectedShipment.id}") {
                    popUpTo(Screen.ShipmentDetail.route) { inclusive = true }
                }
            }) {
                Text(stringResource(R.string.product_amount_confirm_button))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = {
                ShipmentScanFlowState.clear()
                navController.popBackStack(Screen.ShipmentDetail.route + "/${selectedShipment.id}", false)
            }) {
                Text(stringResource(R.string.cancel))
            }
        } else {
            Button(onClick = {
                navController.popBackStack(Screen.Scan.route, false)
            }) {
                Text(stringResource(R.string.go_back))
            }
        }
    }
}
