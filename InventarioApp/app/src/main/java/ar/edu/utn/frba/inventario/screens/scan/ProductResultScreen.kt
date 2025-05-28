package ar.edu.utn.frba.inventario.screens.scan

import android.util.Log
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
import ar.edu.utn.frba.inventario.screens.BottomNavigationBar

@Composable
fun ProductResultScreen(
    navController: NavController,
    code: String?,
    codeType: String?,
    errorMessage: String?,
    origin: String
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        ProductResultBodyContent(navController, innerPadding, code, codeType, errorMessage, origin)
    }
}
@Composable
fun ProductResultBodyContent(
    navController: NavController,
    innerPadding: PaddingValues,
    code: String?,
    codeType: String?,
    errorMessage: String?,
    origin: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isFromScan = origin == "scan"
        val isFromManual = origin == "manual"

        if (errorMessage != null) {
            Text(
                text = if (isFromScan) "Escaneo fallido" else "Búsqueda fallida",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(Modifier.height(8.dp))
            Text(
                errorMessage,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                if (isFromManual) {
                    navController.navigate("manual_code")
                } else {
                    navController.navigate("scan")
                }
            }) {
                Text("Reintentar")
            }
            return
        }

        val codeTypeText = when (codeType) {
            "ean-13" -> "Código de barras"
            "qr" -> "Código QR"
            else -> "ERROR - Código no soportado"
        }

        Text(
            text = if (isFromScan) "Escaneo exitoso" else "Producto encontrado correctamente",
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
            "Valor: $code",
            fontSize = 18.sp,
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            if (isFromManual) {
                navController.navigate("manual_input")
            } else {
                navController.navigate("scan")
            }
        }) {
            Text("Reintentar")
        }

        Button(onClick = {
            Log.d("[ProductResultScreen]", "#ToDo, navigate to next page with result: $code")
        }) {
            Text("Continuar")
        }
    }
}
