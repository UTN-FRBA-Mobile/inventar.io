package ar.edu.utn.frba.inventario.utils
import java.time.ZoneId

object Constants {
    const val DATETIME_COMPLETE = "EEE d 'de' MMM HH:mm"
    const val LOCALE_ES = "es"
    val ZONE_ID_GMT_MINUS_3: ZoneId = ZoneId.of("America/Argentina/Buenos_Aires")
}
