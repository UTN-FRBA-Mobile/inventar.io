package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ScanResultScreen(
    navController: NavController,
    result: String?,
    errorMessage: String?,
    codeType: String?
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        ScanResultBodyContent(navController, innerPadding, result, errorMessage, codeType)
    }
}

@Composable
fun ScanResultBodyContent(
    navController: NavController,
    innerPadding: PaddingValues,
    result: String?,
    errorMessage: String?,
    codeType: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage != null) {
            Text(
                "Escaneo fallido",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(Modifier.height(8.dp))
            Text(
                errorMessage,
                fontSize = 18.sp,
                color = Color.Red
            )
        } else {
            val codeTypeText = when (codeType) {
                "ean-13" -> "Código de barras"
                "qr" -> "Código QR"
                else -> "ERROR - Código no soportado"
            }

            Text(
                "Escaneo exitoso",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Tipo: $codeTypeText",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Valor: $result",
                fontSize = 18.sp,
                color = Color(0xFF388E3C)
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Reintentar")
        }
    }
}

