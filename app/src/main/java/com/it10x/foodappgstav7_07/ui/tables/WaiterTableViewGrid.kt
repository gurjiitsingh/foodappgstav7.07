package com.it10x.foodappgstav7_07.ui.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant

import androidx.compose.material3.*

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.it10x.foodappgstav7_07.viewmodel.PosTableViewModel
import com.it10x.foodappgstav7_07.ui.pos.StatusBadge

@Composable
fun WaiterTableViewGrid(
    tables: List<PosTableViewModel.TableUiState>,
    selectedTable: String?,
    onTableClick: (String) -> Unit
) {

    val groupedByArea = tables
        .groupBy { it.table.area ?: "Waiter" }
        .mapValues { (_, list) ->
            list.sortedBy { it.table.sortOrder ?: Int.MAX_VALUE }
        }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 95.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        groupedByArea.forEach { (area, areaTables) ->

            // 🔹 AREA HEADER
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = area,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            items(
                items = areaTables,
                key = { it.table.id }
            ) { ui ->

                val table = ui.table
                val isSelected = selectedTable == table.id

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp,
                    border = if (isSelected)
                        BorderStroke(2.dp, Color(0xFFFF9800))
                    else null,
                    modifier = Modifier
                        .aspectRatio(0.9f)
                        .clickable {
                            onTableClick(table.id)
                        }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // 🔹 TABLE NAME + BILL AMOUNT
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.width(6.dp))

                                Text(
                                    text = table.tableName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            if (ui.billAmount > 0) {
                                Text(
                                    text = ui.billAmount.toInt().toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 🔹 ONLY BILL BADGE (🧾)
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            if (ui.billDoneCount > 0) {
                                StatusBadge(
                                    icon = "🧾",
                                    text = ui.billDoneCount.toString(),
                                    bgColor = Color(0xFF2E7D32).copy(alpha = 0.7f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}