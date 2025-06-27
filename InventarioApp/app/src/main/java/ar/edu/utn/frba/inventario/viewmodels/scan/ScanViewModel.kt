package ar.edu.utn.frba.inventario.viewmodels.scan

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.utils.OrderProductsListArgs
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {
    val scannedCode = mutableStateOf(false)

    fun resetScannedCode() {
        scannedCode.value = false
    }

    fun handleScanSuccess(
        scannedCodes: List<Barcode>,
        navController: NavController,
        context: Context,
        origin: String
    ) {
        val validCode = getValidBarcode(scannedCodes, origin)

        val codeType = when (validCode?.format) {
            Barcode.FORMAT_EAN_13 -> "ean-13"
            Barcode.FORMAT_QR_CODE -> "qr"
            else -> ""
        }

        if (validCode == null) {
            val errorMsg = context.getString(getErrorMessageResId(origin))
            val destination = Screen.ProductResult.withNavArgs(
                ProductResultArgs.ErrorMessage to errorMsg,
                ProductResultArgs.Origin to origin
            )
            navController.navigate(destination)
            return
        }

        // Add to withNavArgs the arg codeType
        //  "ean-13" ->
        //    "qr" ->

        val scannedValue = validCode.rawValue.orEmpty()
        Log.d("ScanSuccess", "Código escaneado: $scannedValue (Tipo: $codeType, Origen: $origin)")

        if (origin == "order" && codeType == "qr") {
            val orderId = extractOrderIdFromQrCode(scannedValue)

            if (orderId.isBlank()) {
                val errorMsg = ("Pedido no identificado")
                val destination = Screen.ProductResult.withNavArgs(
                    ProductResultArgs.ErrorMessage to errorMsg,
                    ProductResultArgs.Code to scannedValue,
                    ProductResultArgs.CodeType to codeType,
                    ProductResultArgs.Origin to origin
                )
                navController.navigate(destination)
                return
            }

            val destination = Screen.OrderProductsList.withNavArgs(
                OrderProductsListArgs.OrderId to orderId
            )
            navController.navigate(destination)
            return
        }

        val destination = Screen.ProductResult.withNavArgs(
            ProductResultArgs.CodeType to codeType,
            ProductResultArgs.Code to scannedValue,
            ProductResultArgs.Origin to origin
        )
        navController.navigate(destination)
    }

    fun getValidBarcode(scannedCodes: List<Barcode>, origin: String): Barcode? {
        return scannedCodes.firstOrNull { barcode ->
            when (origin) {
                "shipment" -> barcode.format == Barcode.FORMAT_EAN_13
                "order" -> barcode.format == Barcode.FORMAT_QR_CODE
                else -> false
            }
        }
    }

    private fun extractOrderIdFromQrCode(qrCode: String): String {
        val parts = qrCode.split('_')
        return if (parts.size >= 2 && parts.last().any { it.isDigit() }) {
            parts.last().filter { it.isDigit() }
        } else {
            ""
        }
    }

    fun getErrorMessageResId(origin: String): Int {
        return when (origin) {
            "shipment" -> R.string.scan_error_expected_ean13
            "order" -> R.string.scan_error_expected_qr
            else -> R.string.scan_error_unsupported_code_format
        }
    }
}