package ar.edu.utn.frba.inventario.utils

fun String.removeRouteParams(): String {
    return this.substringBefore("?")
}