package ar.edu.utn.frba.inventario.utils

fun String.removeRouteParams(): String = this.substringBefore("?")
