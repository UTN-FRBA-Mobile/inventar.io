package ar.edu.utn.frba.inventario.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.utils.OrderResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs

@Composable
fun ManualOrderScreen(navController: NavController) {
    ManualOrderIdBodyContent(navController)
}

@Composable
fun ManualOrderIdBodyContent(
    navController: NavController,
) {
    var orderId by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.order_id_input), fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = orderId,
            onValueChange = {
                orderId = it.filter { char -> char.isDigit() }.take(10)
                isError = false
            },
            label = { Text(stringResource(R.string.order_id_placeholder)) },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (orderId.isEmpty()) {
                    isError = true
                    return@Button
                }
                val destination = Screen.OrderResult.withNavArgs(
                    OrderResultArgs.OrderId to orderId,
                )
                navController.navigate(destination)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.confirm))
        }

        if (isError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(R.string.invalid_order_id), color = Color.Red)
        }
    }
}
