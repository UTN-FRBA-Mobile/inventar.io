package ar.edu.utn.frba.inventario.api.model.item

import android.content.Context
import ar.edu.utn.frba.inventario.api.model.product.ProductOperation
import java.time.LocalDateTime

interface Item {
    val id: String
    val customerName: String? get() = null
    val sender: String? get() = null
    val products: List<ProductOperation>
    val status: ItemStatus
    val creationDate: LocalDateTime

    fun getRelevantDate(): LocalDateTime?
    fun getDisplayName(): String
    fun getCardDetail(context: Context): String
}