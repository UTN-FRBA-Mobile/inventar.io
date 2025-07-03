package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

abstract class BaseItemViewModel<T>(
    internal val savedStateHandle: SavedStateHandle,
    private val preferencesManager: PreferencesManager,
    private val statusFilterKey: String
) : ViewModel() {
    protected abstract val items: SnapshotStateList<T>

    private val _selectedStatusList = MutableStateFlow<Set<ItemStatus>>(emptySet())
    val selectedStatusList: StateFlow<Set<ItemStatus>> = _selectedStatusList.asStateFlow()

    init {
        _selectedStatusList.value = preferencesManager.getSelectedStatus(statusFilterKey)
        Log.d("VIEWMODEL_BASE_ITEM", "Filtros cargados desde Preferences: ${_selectedStatusList.value}")
    }

    fun getFilteredItems(): List<T> {
        return if (_selectedStatusList.value.isEmpty()) {
            getSortedItems(items.toList())
        } else {
            getSortedItems(items.filter { getStatus(it) in _selectedStatusList.value })
        }
    }

    fun updateSelectedStatusList(status: ItemStatus) {
        _selectedStatusList.update { currentSet ->
            val newSet = currentSet.toMutableSet().apply {
                if (!add(status)) remove(status)
            }
            preferencesManager.saveSelectedStatus(statusFilterKey, newSet.toSet())
            newSet
        }
    }

    fun clearFilters() {
        Log.d("VIEWMODEL_BASE_ITEM", "clearFilter antes : ${_selectedStatusList.value}")
        _selectedStatusList.value = emptySet()
        preferencesManager.clearSelectedStatus(statusFilterKey)
        Log.d("VIEWMODEL_BASE_ITEM", "clearFilter aplicado : ${preferencesManager.getSelectedStatus(statusFilterKey)}")
    }

    private fun getSortedItems(items: List<T>): List<T> {
        return items.sortedWith(
            compareBy<T> { getStatus(it).ordinal }
                .thenBy { getFilterDate(it) }
        )
    }

    abstract fun getStatus(item: T): ItemStatus

    abstract fun getFilterDate(item: T): LocalDateTime
}