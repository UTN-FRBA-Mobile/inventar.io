package ar.edu.utn.frba.inventario.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.utils.Screen

import ar.edu.utn.frba.inventario.viewmodels.ShipmentViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentScreen(
    viewModel: ShipmentViewModel = hiltViewModel(),
    navController: NavController,
    id: String
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { ButtonBox(viewModel, navController) }
    ) { innerPadding ->
        ShipmentBodyContent(viewModel, navController, id, innerPadding)
    }
}

@Composable
fun ShipmentBodyContent(
    viewModel: ShipmentViewModel,
    navController: NavController,
    id: String,
    innerPadding: PaddingValues
) {
    viewModel.loadShipment(id)
    val shipment by viewModel.shipment.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Button(onClick = { navController.navigate(Screen.Home.route) }) {
            Text(text = "Atras")
        }
        Spacer(modifier = Modifier
            .height(10.dp))
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
                ProductItem(viewModel,product,
                    onProductClick = { clickedProduct ->
                        navController.navigate(Screen.ProductDetail.route + "/${clickedProduct.id}")
                    })
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        Spacer(modifier = Modifier
            .height(10.dp))

    }
}

@Composable
fun ProductItem(viewModel:ShipmentViewModel,product:Product,
                onProductClick: (Product) -> Unit ){
    val isComplete = viewModel.isProductCompleted(product.id)
    val backgroundColor = if (isComplete) Color(0xFFB2FF59) else {CardDefaults.elevatedCardColors().containerColor}
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor)
        ,modifier = Modifier
        .fillMaxSize()
        .padding(2.dp)
            .clickable { onProductClick(product)}
    ){
        Column(modifier = Modifier
            .padding(15.dp)){
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp,)
            Spacer(modifier = Modifier
                .height(10.dp))
            Row {
                Text(text = "${product.quantity} Requeridos")
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
fun ButtonBox(viewModel:ShipmentViewModel, navController: NavController){
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
                Button(onClick = { }, modifier = Modifier
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
fun vistaFinal(){
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



