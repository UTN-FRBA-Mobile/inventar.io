package ar.edu.utn.frba.inventario.events

sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
}
