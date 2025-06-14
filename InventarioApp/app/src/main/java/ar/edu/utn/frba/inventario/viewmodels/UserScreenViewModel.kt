package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.self.UserResponse
import ar.edu.utn.frba.inventario.api.repository.SelfRepository
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserScreenViewModel @Inject constructor(
    private val selfRepository: SelfRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _user = MutableStateFlow<UserResponse?>(null)
    val user = _user.asStateFlow()

    fun getUser() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val userResult = selfRepository.getMyUser()) {
                is NetworkResult.Success -> {
                    Log.d("UserViewModel", "Success: ${userResult.data}")
                    _user.value = userResult.data
                }
                is NetworkResult.Error -> {
                    Log.d("UserViewModel", "Error: code=${userResult.code}, message=${userResult.message}")
                }
                is NetworkResult.Exception -> {
                    Log.d("UserViewModel", "Error crítico: ${userResult.e.message}")
                }
            }
        }
    }

    fun doLogout() {
        tokenManager.clearSession()
    }
}