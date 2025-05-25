package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject


@HiltViewModel
class UiStateViewModel @Inject constructor() : ViewModel() {

    private val _loadingCount = mutableStateOf(0)
    val isLoading: State<Boolean> = derivedStateOf { _loadingCount.value > 0 }

    fun startLoading() {
        Log.d("UiStateViewModel", "startLoading called. Count before: ${_loadingCount.value}")
        _loadingCount.value++
        Log.d("UiStateViewModel", "Count after: ${_loadingCount.value}")
    }

    fun stopLoading() {
        Log.d("UiStateViewModel", "stopLoading called. Count before: ${_loadingCount.value}")
        if (_loadingCount.value > 0) {
            _loadingCount.value--
        }
        Log.d("UiStateViewModel", "Count after: ${_loadingCount.value}")
    }
}