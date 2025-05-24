package ar.edu.utn.frba.inventario.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import java.time.LocalDateTime

abstract class BaseItemViewModel<T>(
    private val savedStateHandle: SavedStateHandle,
    private val filterKey: String
) : ViewModel() {

    protected abstract val items: SnapshotStateList<T>

    private val _selectedStatusList = mutableStateOf<Set<ItemStatus>>(
        savedStateHandle.get<Array<ItemStatus>>(filterKey)?.toSet() ?: emptySet()
    )

    val selectedStatusList: State<Set<ItemStatus>> get() = _selectedStatusList

    abstract fun getStatus(item: T): ItemStatus
    abstract fun getFilterDate(item: T): LocalDateTime

    fun getFilteredItems(): List<T> {
        return if (_selectedStatusList.value.isEmpty()) {
            getSortedItems(items.toList())
        } else {
            getSortedItems(items.filter { getStatus(it) in _selectedStatusList.value })
        }
    }

    fun getSortedItems(items: List<T>): List<T> {
        return items.sortedWith(
            compareBy<T> { getStatus(it).ordinal }
                .thenBy { getFilterDate(it) }
        )
    }

    fun updateSelectedStatusList(status: ItemStatus) {
        _selectedStatusList.value = _selectedStatusList.value.toMutableSet().apply {
            if (contains(status)) remove(status) else add(status)
        }.also { newValue ->
            savedStateHandle[filterKey] = newValue.toTypedArray()
        }
    }


    fun clearFilters() {
        _selectedStatusList.value = emptySet()
        savedStateHandle.remove<Array<ItemStatus>>(filterKey)
    }
}