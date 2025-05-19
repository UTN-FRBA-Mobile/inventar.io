package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
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
        Log.d("LoginViewModel","Llegaron User: $user, Pass: $pass")
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