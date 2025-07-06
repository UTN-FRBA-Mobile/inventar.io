package ar.edu.utn.frba.inventario.composables.utils

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus

@Composable
fun StatusFilter(
    statusList: List<ItemStatus>,
    selectedStatusList: Set<ItemStatus>,
    onStatusSelected: (ItemStatus) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filtro "Todos"
        FilterChip(
            selected = selectedStatusList.isEmpty(),
            onClick = onClearFilters,
            label = { Text(stringResource(R.string.all_status_selected)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        statusList.forEach { status: ItemStatus ->
            val isSelected = status in selectedStatusList
            FilterChip(
                selected = isSelected,
                onClick = { onStatusSelected(status) },
                label = { Text(status.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = status.color.copy(alpha = 0.2f),
                )
            )
        }
    }
}
