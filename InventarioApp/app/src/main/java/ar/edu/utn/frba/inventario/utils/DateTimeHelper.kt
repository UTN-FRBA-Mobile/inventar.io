package ar.edu.utn.frba.inventario.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.format(): String {
    val formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_COMPLETE, Locale(Constants.LOCALE_ES))
    return this.format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime {
    return Instant.parse(this)          // Parsea el string a Instant (UTC)
        .atZone(ZoneId.systemDefault()) // Convierte a zona horaria local
        .toLocalDateTime()              // Obtiene LocalDateTime
}