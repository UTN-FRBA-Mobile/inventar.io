package ar.edu.utn.frba.inventario.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class LoginViewModel:ViewModel() {
    var user by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set


    fun changeUser(newUser:String){
        user = newUser
    }

    fun changePassword(newPass:String){
        password = newPass
    }

    fun login(user:String, pass:String, navController:NavController){
        println("{$user} - {$pass}")
        if(existLoginUser(user, pass)){
            navController.navigate(route = "home")
        }else{
            navController.navigate(route = "login")
        }

    }

    private fun existLoginUser(user:String, pass:String):Boolean{
        val userMock = "inventario"
        val passMock = "abc123"

        val result = (user == userMock) && (pass==passMock)

        return result

    }
}