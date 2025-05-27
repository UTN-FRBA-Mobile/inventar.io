package ar.edu.utn.frba.inventario.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.screens.BottomNavigationBar
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs


@Composable
fun ManualCodeScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        ManualCodeBodyContent(innerPadding, navController)
    }
}

@Composable
fun ManualCodeBodyContent(
    innerPadding: PaddingValues,
    navController: NavController
) {
    var code by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ingrese el código EAN-13", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = code,
            onValueChange = {
                code = it.filter { char -> char.isDigit() }.take(13)
                isError = false
            },
            label = { Text("Código EAN-13") },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (code.length != 13) {
                    isError = true
                    return@Button
                }

                // TODO - Verificar digito de control, para ver q sea codigo valido
                val destination = Screen.ProductResult.withNavArgs(
                    ProductResultArgs.Code to code,
                    ProductResultArgs.CodeType to "ean-13",
                    ProductResultArgs.Origin to "manual"
                )

                navController.navigate(destination)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar")
        }

        if (isError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Debe ingresar un código EAN-13 válido.", color = Color.Red)
        }
    }
}
