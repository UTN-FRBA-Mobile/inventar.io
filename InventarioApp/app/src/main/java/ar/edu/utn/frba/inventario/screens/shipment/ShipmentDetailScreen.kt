package ar.edu.utn.frba.inventario.screens.shipment


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.product.ProductOperation
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.ShipmentScanFlowState
import ar.edu.utn.frba.inventario.viewmodels.ShipmentDetailViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentDetailScreen(
    viewModel: ShipmentDetailViewModel = hiltViewModel(),
    navController: NavController,
    id: String
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { ButtonBox(viewModel, navController) },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {navController.navigate(Screen.Shipments.route)},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = stringResource(R.string.shipment_detail_screen_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    ) { innerPadding ->
        ShipmentDetailBodyContent(viewModel, navController, id, innerPadding)
    }
}

@Composable
fun ShipmentDetailBodyContent(
    viewModel: ShipmentDetailViewModel,
    navController: NavController,
    id: String,
    innerPadding: PaddingValues
) {
    LaunchedEffect(id) {
        viewModel.loadShipment(id)
    }

    val selectedShipment: Shipment by viewModel.selectedShipment.collectAsState()

    if (selectedShipment.id == "0") {
        CircularProgressIndicator()
    } else {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.shipment_detail_screen_shipment,
                        selectedShipment.number
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        R.string.shipment_detail_screen_customer,
                        selectedShipment.customerName
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(
                        R.string.shipment_detail_screen_total,
                        selectedShipment.products.size
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(15.dp)
        ) {
            items(selectedShipment.products) { product ->
                ProductItem(
                    viewModel, product,
                    onProductClick = { clickedProduct ->
                        navController.navigate(Screen.ProductDetail.route + "/${clickedProduct.id}")
                    })
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
    }
}
}

@Composable
fun ProductItem(viewModel:ShipmentDetailViewModel, product: ProductOperation,
                onProductClick: (ProductOperation) -> Unit){
    val  statusProd = viewModel.getProductStatus(product.id)

    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ,modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .clickable { onProductClick(product) }){
        Row(modifier = Modifier
            .fillMaxSize()){
            Column(modifier = Modifier
                .weight(1f)
                .padding(15.dp)){
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier
                    .height(10.dp))
                Row {
                    Text(text = stringResource(R.string.shipment_detail_screen_quantity_required, product.quantity), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(60.dp))
                    Box(contentAlignment = Alignment.Center
                            ){
                        Text(text= stringResource(R.string.shipment_detail_screen_quantity_loaded, viewModel.getLoadedQuantityProduct(product.id)), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Box(modifier = Modifier
                .padding(end = 12.dp)
                .size(40.dp)
                .align(alignment = Alignment.CenterVertically),
                contentAlignment = Alignment.Center){
                Image(painter = painterResource(id = statusProd.iconResourceId),
                    contentDescription = stringResource(R.string.product_state_icon),
                    modifier = Modifier.fillMaxSize(0.9f),
                    contentScale = ContentScale.Fit)

            }
        }
    }
}

@Composable
fun ButtonBox(viewModel: ShipmentDetailViewModel, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    if (viewModel.showButtonBox()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceTint),
                        enabled = viewModel.isStateCompleteShipment.value,
                        onClick = {
                            viewModel.completedShipment(viewModel.selectedShipment.value.id)
                            navController.navigate(Screen.Shipments.route)
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.shipment_detail_screen_next_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        onClick = {

                            coroutineScope.launch {

                                val shipmentId = viewModel.selectedShipment.value.id

                                val hasEnoughStock = viewModel.enoughStockProducts(shipmentId)

                                if (hasEnoughStock) {
                                    ShipmentScanFlowState.clear()
                                    ShipmentScanFlowState.selectedShipment =
                                        viewModel.selectedShipment.value

                                    Log.d(
                                        "ShipmentDetailScreen",
                                        "Selected Shipment: ${ShipmentScanFlowState.selectedShipment}"
                                    )

                                    navController.navigate(Screen.Scan.route + "?origin=shipment")
                                } else {
                                    Log.d(
                                        "ShipmentDetailScreen",
                                        "No hay stock suficiente, se redirecciona a la pantalla de Shipments"
                                    )
                                    navController.navigate(Screen.Shipments.route)
                                }
                            }

                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.shipment_detail_screen_scan_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun vistaFinal(){
    ShipmentDetailScreen(navController = rememberNavController(), id = "S01-3")
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun vistaFinalDark(){
    ShipmentDetailScreen(navController = rememberNavController(), id = "S01-3")
}



