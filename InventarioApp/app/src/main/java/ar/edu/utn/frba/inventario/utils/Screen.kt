package ar.edu.utn.frba.inventario.utils

import android.net.Uri

enum class Screen(val route: String) {
    Login("login"),
    Welcome("welcome"),
    Shipments("shipments"),
    Orders("orders"),
    User("user"),
    ShipmentDetail("shipment_detail"),
    OrderDetail("order_detail"),
    Scan("scan"),
    ProductResult("product_result"),
    ManualCode("manual_code"),
    ProductDetail("product_detail");
}

enum class ProductResultArgs(override val code: String) : HasCode {
    Code("result"),
    ErrorMessage("errorMessage"),
    CodeType("codeType"),
    Origin("origin"),
}

enum class ScanArgs(override val code: String) : HasCode {
    Origin("origin"),
}

fun Screen.withArgsDefinition(args: Array<out HasCode>): String {
    val query = args.joinToString("&") { "${it.code}={${it.code}}" }
    return if (query.isNotEmpty()) "$route?$query" else route
}

fun Screen.withNavArgs(vararg args: Pair<HasCode, String?>): String {
    val queryString = args
        .filter { it.second != null }
        .joinToString("&") { (arg, value) -> "${arg.code}=${Uri.encode(value)}" }

    return if (queryString.isNotEmpty()) "$route?$queryString" else route
}

interface HasCode {
    val code: String
}