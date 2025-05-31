package ar.edu.utn.frba.inventario.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.Product
import ar.edu.utn.frba.inventario.viewmodels.ShipmentViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShipmentScreen(viewModel:ShipmentViewModel = hiltViewModel(), navController: NavController, id:String){
    Scaffold(bottomBar = {ButtonBox(viewModel,navController)}){
        innerPadding ->
        ShipmentBodyContent(viewModel, navController, id,innerPadding)
    }

}
@Composable
fun ShipmentBodyContent(viewModel:ShipmentViewModel, navController: NavController, id:String, innerPadding: PaddingValues){
    viewModel.loadShipment(id)
    val shipment by viewModel.shipment.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .padding(innerPadding)
        ) {
        Button(onClick = {navController.navigate(Screen.Home.route)}
        ){
            Text(text = "Atras", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier
            .height(10.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer)){
            Column (modifier = Modifier
                .padding(20.dp)){
                Text(
                    text = "Envio ${shipment.number}",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Comprador: ${shipment.customerName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Total de productos unicos: ${shipment.products.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
    val  statusProd = viewModel.isProductCompleted(product.id)

    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ,modifier = Modifier
        .fillMaxSize()
        .padding(2.dp)){
        Row(modifier = Modifier
            .fillMaxSize()){
            Column(modifier = Modifier
                .weight(1f)
                .padding(15.dp)){
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier
                    .height(10.dp))
                Row {
                    Text(text = "${product.quantity} Requeridos", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(60.dp))
                    Box(contentAlignment = Alignment.Center
                            ){
                        Text(text="${viewModel.getLoadedQuantityProduct(product.id)} Cargados", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Box(modifier = Modifier
                .padding(end = 12.dp)
                .size(40.dp)
                .align(alignment = Alignment.CenterVertically),
                contentAlignment = Alignment.Center){
                Image(painter = painterResource(id = statusProd.iconResourceId),
                    contentDescription = "icono del estado de la carga de productos",
                    modifier = Modifier.fillMaxSize(0.9f),
                    contentScale = ContentScale.Fit)

            }
        }
    }
}

@Composable
fun ButtonBox(viewModel:ShipmentViewModel, navController: NavController){
    Column {
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp)){
            Row {
                Button(colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceTint),
                    enabled = viewModel.isStateCompleteShipment.value,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)){
                    Text(text = "Siguiente", style = MaterialTheme.typography.titleMedium, fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                }
                Button(colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSecondaryContainer),
                    onClick = { },
                    modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                ){
                    Text(text = "Scan", style = MaterialTheme.typography.titleMedium, fontSize = 25.sp,
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
fun vistaFinal(){
    ShipmentScreen(navController = rememberNavController(), id = "S01-3")
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun vistaFinalDark(){
    ShipmentScreen(navController = rememberNavController(), id = "S01-3")
}

//@Preview
//@Composable
//fun vistaProd(){
//    ElevatedCard(modifier = Modifier
//        .fillMaxWidth()
//        .padding(2.dp)){
//        Column (modifier = Modifier
//            .padding(15.dp)){
//            Text("Pendrive 32gb kingston")
//            Spacer(modifier = Modifier
//                .height(10.dp))
//            Row {
//                Text("x5")
//                Spacer(modifier = Modifier.width(25.dp)
//                    .weight(2f))
//                Box(contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .background(color = Color.White)){
//                    Text(text="Cargado: 0")
//                }
//
//
//            }
//        }
//    }
//
//}



