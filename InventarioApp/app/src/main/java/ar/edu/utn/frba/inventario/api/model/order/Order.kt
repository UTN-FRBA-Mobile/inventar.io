package ar.edu.utn.frba.inventario.api.model.order

import android.content.Context
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.item.Item
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.shipment.Product
import ar.edu.utn.frba.inventario.utils.format
import java.time.LocalDateTime

data class Order(
    override val id: String,
    val number: String,//Todos estos nombres están muy así nomás. Una vez que los definamos los actualizamos
    override val sender: String,
    override val status: ItemStatus,
    override val products : List<Product>,
    override val confirmedReceiptDate: LocalDateTime? = null,
    override val estimatedReceiptDate: LocalDateTime? = null,
    override val cancellationDate: LocalDateTime? = null,
    override val creationDate: LocalDateTime = LocalDateTime.now()
) : Item {
    override fun getRelevantDate() = when(status) {
        ItemStatus.COMPLETED -> confirmedReceiptDate
        ItemStatus.CANCELLED -> cancellationDate
        ItemStatus.BLOCKED -> estimatedReceiptDate
        ItemStatus.PENDING,
        ItemStatus.IN_PROGRESS -> creationDate
    }

    override fun getDisplayName() = "$number • $sender"

    override fun getCardDetail(context: Context): String {
        val formattedDate = getRelevantDate()?.format() ?: context.getString(R.string.unknown_date)
        return when(status) {
            ItemStatus.COMPLETED -> context.getString(R.string.received_on, formattedDate)
            ItemStatus.CANCELLED -> context.getString(R.string.cancelled_on, formattedDate)
            ItemStatus.PENDING -> context.getString(R.string.pending_reception, formattedDate)
            ItemStatus.BLOCKED,
            ItemStatus.IN_PROGRESS -> context.getString(R.string.created_date, formattedDate)
        }
    }
}
