package ar.edu.utn.frba.inventario.api.model.shipment

import android.content.Context
import java.time.LocalDateTime
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.item.Item
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.utils.format

data class Shipment(
    override val id: String,
    val number: String,
    override val customerName: String,
    override val status: ItemStatus,
    override val products: List<Product>,
    override val creationDate: LocalDateTime = LocalDateTime.now(), // TODO esto variaría según fecha de creación que venga del back
) : Item {
    override fun getRelevantDate() = creationDate

    override fun getDisplayName() = "$number • $customerName"

    override fun getCardDetail(context: Context): String {
        return context.resources.getQuantityString(
            R.plurals.products_and_date_template,
            products.size,
            products.size,
            creationDate.format()
        )
    }
}