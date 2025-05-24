package ar.edu.utn.frba.inventario.api.model.item

import androidx.compose.ui.graphics.Color
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.ui.theme.GreenCompleted
import ar.edu.utn.frba.inventario.ui.theme.GreyPending
import ar.edu.utn.frba.inventario.ui.theme.RedBlocked
import ar.edu.utn.frba.inventario.ui.theme.YellowInProgress

enum class ItemStatus(
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
    ),
    CANCELLED(//Evaluar si se unifica con blocked
        color = RedBlocked,
        displayName = "Cancelado",
        iconResourceId = R.drawable.cancelled
    )
}