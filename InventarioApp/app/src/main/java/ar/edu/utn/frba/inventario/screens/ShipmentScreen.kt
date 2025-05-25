package ar.edu.utn.frba.inventario.screens

import android.annotation.SuppressLint
import android.text.Layout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.utn.frba.inventario.viewmodels.Product


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShipmentScreen(viewModel:ShipmentViewModel = hiltViewModel(), navController: NavController, id:Int){
    Scaffold(bottomBar = {ButtonBox()}){
        innerPadding ->
        ShipmentBodyContent(viewModel, id,innerPadding)
    }

}
@Composable
fun ShipmentBodyContent(viewModel:ShipmentViewModel, id:Int, innerPadding: PaddingValues){
    viewModel.loadShipment(id)
    val shipment by viewModel.shipment.collectAsState()


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        ) {
        Spacer(modifier = Modifier
            .height(50.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFE7ECFB))){
            Column (modifier = Modifier
                .padding(20.dp)){
                Text(
                    text = "Envio ${shipment.number}",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Comprador: ${shipment.customerName}", fontWeight = FontWeight.SemiBold)
                Text(text = "Total de productos unicos: ${shipment.products.size}", fontWeight = FontWeight.SemiBold)
            }
        }
        LazyColumn(modifier = Modifier
            .padding(15.dp)) {
            items(shipment.products){
                product ->
                ProductItem(viewModel,product)
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        Spacer(modifier = Modifier
            .height(10.dp))

    }
}

@Composable
fun ProductItem(viewModel:ShipmentViewModel,product:Product){
    ElevatedCard(modifier = Modifier
        .fillMaxSize()
        .padding(2.dp)){
        Column(modifier = Modifier
            .padding(15.dp)){
            Text(product.name)
            Spacer(modifier = Modifier
                .height(10.dp))
            Row {
                Text("${product.quantity} Requeridos")
                Spacer(modifier = Modifier.width(25.dp)
                    .weight(2f))
                Box(contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .background(color = Color.White)){
                    Text(text="${viewModel.getLoadedQuantityProduct(product.id)} Cargados")
                }
            }
        }
    }
}

@Composable
fun ButtonBox(){
    Column {
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp)){
            Row {

                Button(colors = ButtonDefaults.buttonColors(Color(0xFFE7ECFB)),
                    border = BorderStroke(1.dp, color = Color.Gray),onClick = {}, modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)){
                    Text(text = "Siguiente", color = Color.Black, fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                }
                Button(onClick = {}, modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                ){
                    Text(text = "Scan", color = Color.Black, fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier
            .height(20.dp))
    }

}

@Preview
@Composable
fun vistaProd(){
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .padding(2.dp)){
        Column (modifier = Modifier
            .padding(15.dp)){
            Text("Pendrive 32gb kingston")
            Spacer(modifier = Modifier
                .height(10.dp))
            Row {
                Text("x5")
                Spacer(modifier = Modifier.width(25.dp)
                    .weight(2f))
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(color = Color.White)){
                    Text(text="Cargado: 0")
                }


            }
        }
    }
    ButtonBox()
}

