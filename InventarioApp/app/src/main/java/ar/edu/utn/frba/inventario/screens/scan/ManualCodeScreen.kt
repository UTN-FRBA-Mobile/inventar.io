package ar.edu.utn.frba.inventario.screens.scan

import android.util.Log
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
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs


@Composable
fun ManualCodeScreen(navController: NavController) {
    ManualCodeBodyContent(navController)
}

@Composable
fun ManualCodeBodyContent(
    navController: NavController
) {
    var code by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.manual_code_insert_code_title), fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = code,
            onValueChange = {
                code = it.filter { char -> char.isDigit() }.take(13)
                isError = false
            },
            label = { Text(stringResource(R.string.manual_code_input)) },
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

                if (!isValidEAN13(code)) {
                    isError = true
                    return@Button
                }

                val destination = Screen.ProductResult.withNavArgs(
                    ProductResultArgs.Code to code,
                    ProductResultArgs.CodeType to "ean-13",
                    ProductResultArgs.Origin to "manual"
                )

                navController.navigate(destination)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.manual_code_confirm_button))
        }

        if (isError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(R.string.manual_code_error_invalid_code), color = Color.Red)
        }
    }
}

fun isValidEAN13(code: String): Boolean {
    if (code.length != 13 || code.any { !it.isDigit() })
        return false

    val digits = code.map { it.digitToInt() }
    val checkDigit = digits.last()

    val sum = digits.dropLast(1).mapIndexed { index, digit ->
        if (index % 2 == 0) digit else digit * 3
    }.sum()

    val computedCheckDigit = (10 - (sum % 10)) % 10

    // Print checkDigit and computedCheckDigit for debugging
    Log.d("ManuelCodeScreen", "Check Digit: $checkDigit, Computed Check Digit: $computedCheckDigit")

    return checkDigit == computedCheckDigit
}