import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.ui.theme.*


data class Shipment(
    val id: String, // Al principio del id podría tener el código de sucursal
    val number: String,
    val customerName: String,
    val status: ShipmentStatus,
    val products: List<Product> = listOf(
        //TODO remover una vez que se obtenga la pegada al back
        Product("P-001", "Producto Ejemplo 1", 1),
        Product("P-002", "Producto Ejemplo 2", 2)
    ),
    val creationDate: LocalDateTime = LocalDateTime.now(), // esto variaría según fecha de creación
    val userIdAssigned: String? = "Sin responsable"
)

data class Product(
    val id: String,
    val name: String,
    val quantity: Int
)

enum class ShipmentStatus(
    val color: Color,
    val displayName: String,
    val iconResourceId: Int
) {
    PENDING(
        color = GreyPending,
        displayName = "Pendiente",
        iconResourceId = R.drawable.pending
    ),
    IN_PROGRESS(
        color = YellowInProgress,
        displayName = "En Progreso",
        iconResourceId = R.drawable.in_progress
    ),
    COMPLETED(
        color = GreenCompleted,
        displayName = "Completado",
        iconResourceId = R.drawable.completed
    ),
    BLOCKED(
        color = RedBlocked,
        displayName = "Bloqueado",
        iconResourceId = R.drawable.blocked
    )
}