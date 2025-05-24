package ar.edu.utn.frba.inventario.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.viewmodels.ShipmentViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.utn.frba.inventario.viewmodels.Product

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShipmentScreen(viewModel:ShipmentViewModel = hiltViewModel(), navController: NavController, id:Int){
    Scaffold{
        ShipmentBodyContent(viewModel, id)
    }

}
@Composable
fun ShipmentBodyContent(viewModel:ShipmentViewModel, id:Int){
    viewModel.loadShipment(id)
    val shipment by viewModel.shipment.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)) {
        Text(
            text = "Envio ${shipment.number}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Comprador: ${shipment.customerName}")
        Text(text = "Total de productos: ${shipment.products.size}")
        LazyColumn(modifier = Modifier
            .padding(6.dp)) {
            items(shipment.products){
                product ->
                ProductItem(viewModel,product)
            }
        }

    }
}

@Composable
fun ProductItem(viewModel:ShipmentViewModel,product:Product){
    Card (modifier = Modifier
        .fillMaxSize()
        .padding(6.dp)){
        Column {
            Text(product.name)
            Row {
                Text("x${product.quantity}")
                Spacer(modifier = Modifier.width(20.dp))
                Text("Cargado: ${viewModel.getLoadedQuantityProduct(product.id)}")

            }
        }


    }


}