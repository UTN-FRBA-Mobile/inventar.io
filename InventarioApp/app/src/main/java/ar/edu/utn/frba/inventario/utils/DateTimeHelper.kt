package ar.edu.utn.frba.inventario.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.format(): String {
    val formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_COMPLETE, Locale(Constants.LOCALE_ES))
    return this.format(formatter)
}