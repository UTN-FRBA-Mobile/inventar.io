package ar.edu.utn.frba.inventario.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.model.shipment.Product
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.time.LocalDateTime

@HiltViewModel
class OrdersViewModel @Inject constructor() : ViewModel() {

    // Lista de órdenes con datos de ejemplo
    val orders: SnapshotStateList<Order> = mutableStateListOf<Order>().apply {
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

    private val _selectedStatuses = mutableStateOf<Set<ItemStatus>>(emptySet())
    val selectedStatuses: androidx.compose.runtime.State<Set<ItemStatus>>
        get() = _selectedStatuses

    fun getFilteredOrders(): List<Order> {
        return if (_selectedStatuses.value.isEmpty()) {
            orders.sortedWith(
                compareBy<Order> { it.status.ordinal }
                    .thenBy { it.creationDate }
            )
        } else {
            getSortedOrders(orders
                .filter { it.status in _selectedStatuses.value})
        }
    }

    fun updateSelectedStatuses(status: ItemStatus) {
        _selectedStatuses.value = _selectedStatuses.value.toMutableSet().apply {
            if (contains(status)) remove(status) else add(status)
        }
    }

    fun clearFilters() {
        _selectedStatuses.value = emptySet()
    }


    fun getSortedOrders(orders: List<Order>): List<Order> {
        return orders.sortedWith(
            compareBy<Order> { it.status.ordinal }
                .thenBy { it.creationDate }  // se ordena desde el más antiguo al más nuevo. Tiene sentido para los completados?
        )
    }
}