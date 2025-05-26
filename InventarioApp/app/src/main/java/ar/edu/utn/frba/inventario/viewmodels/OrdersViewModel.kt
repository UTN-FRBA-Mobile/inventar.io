package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.model.shipment.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.time.LocalDateTime

@HiltViewModel
class OrdersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseItemViewModel<Order>(
     savedStateHandle,
     "orders_filter"
) {

    init {
        Log.d("ORDERS_VM", "Filtros iniciales: ${savedStateHandle.get<List<String>>("orders_filter")}")
    }

    // Lista de órdenes con datos de ejemplo
    override val items: SnapshotStateList<Order> = mutableStateListOf<Order>().apply {
        addAll(listOf(
            Order(
                id = "ORD-001",
                number = "P-9090",
                sender = "Cliente Premium",
                status = ItemStatus.PENDING,
                products = listOf(
                    Product("P-101", "Producto A", 2),
                    Product("P-102", "Producto B", 3)
                ),
                estimatedReceiptDate = LocalDateTime.now().plusDays(2)
            ),
            Order(
                id = "ORD-002",
                number = "P-9091",
                sender = "Cliente Regular",
                status = ItemStatus.COMPLETED,
                products = listOf(
                    Product("P-201", "Producto C", 5)
                ),
                confirmedReceiptDate = LocalDateTime.now().plusDays(1)
            ),
            Order(
                id = "ORD-003",
                number = "P-9092",
                sender = "Cliente VIP",
                status = ItemStatus.COMPLETED,
                products = listOf(
                    Product("P-301", "Producto D", 1),
                    Product("P-302", "Producto E", 2)
                ),
                confirmedReceiptDate = LocalDateTime.now().minusDays(1)
            ),
            Order(
                id = "ORD-004",
                number = "P-9095",
                sender = "Cliente Prueba",
                status = ItemStatus.CANCELLED,
                products = listOf(
                    Product("P-401", "Producto F", 4)
                ),
                cancellationDate = LocalDateTime.now().minusHours(3)
            ),
            Order(
                id = "ORD-005",
                number = "P-9099",
                sender = "Empresa X",
                status = ItemStatus.CANCELLED,
                products = List(15) { index ->
                    Product("P-50${index+1}", "Producto ${index+1}", index+1)
                },
                cancellationDate = LocalDateTime.now().plusDays(3)
            )
        ))
    }

    override fun getStatus(item: Order) = item.status
    override fun getFilterDate(item: Order) = item.creationDate //Uso creationDate pero deberíamos evaluar cómo ordenamos

}