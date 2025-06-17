package ar.edu.utn.frba.inventario.screens.scan

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import ar.edu.utn.frba.inventario.utils.ImageFromURL
import ar.edu.utn.frba.inventario.utils.Spinner
import ar.edu.utn.frba.inventario.viewmodels.ProductResultViewModel


@Composable
fun ProductResultScreen(
    navController: NavController,
    code: String?,
    codeType: String?,
    errorMessage: String?,
    origin: String,
    viewModel: ProductResultViewModel = hiltViewModel()
) {
    if (errorMessage != null) {
        ProductResultBodyContent(
            navController = navController,
            code = code,
            codeType = codeType,
            errorMessage = errorMessage,
            origin = origin,
            product = null
        )
        return
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val product by viewModel.product.collectAsState()

    LaunchedEffect(code) {
        viewModel.loadProductByCode(code, codeType)
    }

    if (isLoading) {
        Spinner(true)
        return
    }

    ProductResultBodyContent(
        navController = navController,
        code = code,
        codeType = codeType,
        errorMessage = error,
        origin = origin,
        product = product
    )
}

@Composable
fun ProductResultBodyContent(
    navController: NavController,
    code: String?,
    codeType: String?,
    errorMessage: String?,
    origin: String,
    product: ProductResponse?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage != null) {
            Text(
                text = stringResource(R.string.product_result_search_failed),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(Modifier.height(8.dp))
            Text(
                errorMessage,
                fontSize = 18.sp,
            )
        } else if (product != null && codeType == "ean-13") {
            // Éxito con producto
            Text(
                text = stringResource(R.string.product_result_search_success),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = product.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = product.description,
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(24.dp))

            if (product.imageURL.isNotBlank()) {
                ImageFromURL(
                    url = product.imageURL,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .shadow(4.dp, CircleShape)
                )
                Spacer(Modifier.height(24.dp))
            }

            Text(
                text = "Código EAN-13:\n$code",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Button(onClick = {
                Log.d("[ProductResultScreen]", "#ToDo, navigate to next page with result: $code")
            }) {
                Text(stringResource(R.string.product_result_continue_button))
            }

        } else {
            // Fallback sin producto
            val codeTypeText = when (codeType) {
                "ean-13" -> stringResource(R.string.product_result_barcode)
                "qr" -> stringResource(R.string.product_result_qr_code)
                else -> stringResource(R.string.product_result_error_invalid_code_type)
            }

            Text(
                text = stringResource(R.string.product_result_search_success),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(Modifier.height(8.dp))
            Text("Tipo: $codeTypeText", fontSize = 16.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("Valor: $code", fontSize = 18.sp)
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(stringResource(R.string.try_again))
        }
    }
}



