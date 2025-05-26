package ar.edu.utn.frba.inventario.utils

enum class Screen(val route: String) {
    Login("login"),
    Home("home"),
    Orders("orders"),
    User("user"),
    Scan("scan"),
    SessionChecker("session_checker"),
}