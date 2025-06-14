package ar.edu.utn.frba.inventario.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.OrderDetailViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderDetailScreen(viewModel:OrderDetailViewModel = hiltViewModel(), navController: NavController, id:String){
    Scaffold(
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
                        onClick = {navController.navigate(Screen.Orders.route)},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Detalle del Pedido",
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
    ) {
            innerPadding ->
        OrderDetailBodyContent(viewModel, navController, id,innerPadding)
    }

}
@Composable
fun OrderDetailBodyContent(viewModel:OrderDetailViewModel, navController: NavController, id:String, innerPadding: PaddingValues){
    viewModel.loadOrder(id)
    val order by viewModel.order.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .padding(innerPadding)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer)){
            Column (modifier = Modifier
                .padding(20.dp)){
                Text(
                    text = "Pedido ${order.number}",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Remitente: ${order.sender}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "Total de productos unicos: ${order.products.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
        LazyColumn(modifier = Modifier
            .padding(15.dp)) {
            items(order.products){
                    product ->
                ProductItem(product)
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        Spacer(modifier = Modifier
            .height(10.dp))
    }
}

@Composable
fun ProductItem(product:Product){
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ,modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)){
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp)){
            Box(modifier = Modifier
                .weight(2f)
                .padding(15.dp)){
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier
                .height(10.dp))
            Box(modifier = Modifier
                .weight(1f)
                .padding(15.dp)) {
                Text(text = "Cantidad ${product.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview
@Composable
fun vistaPedidoFinal(){
    OrderDetailScreen(navController = rememberNavController(), id = "ORD-003")
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun vistaPedidoFinalDark(){
    OrderDetailScreen(navController = rememberNavController(), id = "ORD-003")
}