package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.R
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

    companion object {
        val LoadingLocation = R.string.loading_location
        val UnknownLocation = R.string.unknown_location
        val ErrorLoadingLocation = R.string.error_loading_location
    }

    private val _user = MutableStateFlow<UserResponse?>(null)
    val user = _user.asStateFlow()

    private val _branchLocationName = MutableStateFlow("")
    val branchLocationName = _branchLocationName.asStateFlow()

    init {
        getBranchLocation()
    }

    fun getUser() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val userResult = selfRepository.getMyUser()) {
                is NetworkResult.Success -> {
                    Log.d("UserScreenViewModel", "Success: ${userResult.data}")
                    _user.value = userResult.data
                }

                is NetworkResult.Error -> {
                    Log.d(
                        "UserScreenViewModel",
                        "Error: code=${userResult.code}, message=${userResult.message}"
                    )
                }

                is NetworkResult.Exception -> {
                    Log.d("UserScreenViewModel", "Error crítico: ${userResult.e.message}")
                }
            }
        }
    }

    private fun getBranchLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _branchLocationName.value = LoadingLocation.toString()
            when (val result = selfRepository.getMyLocation()) {
                is NetworkResult.Success -> {
                    _branchLocationName.value = result.data?.name ?: UnknownLocation.toString()
                    Log.d(
                        "UserScreenViewModel",
                        "Branch location loaded: ${_branchLocationName.value}"
                    )
                }

                is NetworkResult.Error -> {
                    _branchLocationName.value = ErrorLoadingLocation.toString()
                    Log.e("UserScreenViewModel", "Error loading branch location: ${result.message}")
                }

                is NetworkResult.Exception -> {
                    _branchLocationName.value = ErrorLoadingLocation.toString()
                    Log.e("UserScreenViewModel", "Exception loading branch location", result.e)
                }
            }
        }
    }

    fun doLogout() {
        tokenManager.clearSession()
    }
}