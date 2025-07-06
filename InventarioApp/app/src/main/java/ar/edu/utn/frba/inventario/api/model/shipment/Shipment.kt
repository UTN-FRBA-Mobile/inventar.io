package ar.edu.utn.frba.inventario.api.model.shipment

import android.content.Context
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.item.Item
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.product.ProductOperation
import ar.edu.utn.frba.inventario.utils.format
import java.time.LocalDateTime

data class Shipment(
    override val id: String,
    val number: String,
    override val customerName: String,
    override val status: ItemStatus,
    override val products: List<ProductOperation>,
    override val creationDate: LocalDateTime,
) : Item {
    override fun getRelevantDate() = creationDate

    override fun getDisplayName() = "$number • $customerName"

    override fun getCardDetail(context: Context): String {
        val formattedDate = getRelevantDate().format()
        val productsSize = products.size
        val productsString = context.resources.getQuantityString(
            R.plurals.products_and_date_template,
            productsSize,
            productsSize,
        )

        val statusAndDateString = when (status) {
            ItemStatus.PENDING -> context.getString(R.string.shipment_status_pending, formattedDate)
            ItemStatus.IN_PROGRESS -> context.getString(
                R.string.shipment_status_in_progress,
                formattedDate,
            )

            ItemStatus.COMPLETED -> context.getString(
                R.string.shipment_status_completed,
                formattedDate,
            )

            ItemStatus.BLOCKED -> context.getString(R.string.shipment_status_blocked, formattedDate)
            else -> context.getString(R.string.shipment_status_pending, formattedDate)
        }
        return "$productsString • $statusAndDateString"
    }
}
