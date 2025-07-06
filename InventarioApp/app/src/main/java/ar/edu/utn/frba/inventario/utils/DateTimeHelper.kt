package ar.edu.utn.frba.inventario.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.format(): String {
    val zonedDateTimeInUtc = this.atZone(ZoneOffset.UTC)
    val zonedDateTimeInBuenosAires = zonedDateTimeInUtc.withZoneSameInstant(
        Constants.ZONE_ID_GMT_MINUS_3,
    )
    val formatter = DateTimeFormatter.ofPattern(
        Constants.DATETIME_COMPLETE,
        Locale(Constants.LOCALE_ES),
    )

    return zonedDateTimeInBuenosAires.format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
