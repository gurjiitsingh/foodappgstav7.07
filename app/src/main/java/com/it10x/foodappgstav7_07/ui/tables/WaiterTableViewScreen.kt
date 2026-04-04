package com.it10x.foodappgstav7_07.ui.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.it10x.foodappgstav7_07.data.online.repository.WaiterTableRepository
import com.it10x.foodappgstav7_07.viewmodel.PosTableViewModel
import com.it10x.foodappgstav7_07.ui.pos.StatusBadge
import com.it10x.foodappgstav7_07.ui.cart.CartViewModel
import com.it10x.foodappgstav7_07.ui.pos.PosSessionViewModel

@Composable
fun WaiterTableViewScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    posSessionViewModel: PosSessionViewModel
) {

    val selectedTableId by posSessionViewModel.tableId.collectAsState()

    var tables by remember {
        mutableStateOf<List<PosTableViewModel.TableUiState>>(emptyList())
    }

    val repository = remember {
        WaiterTableRepository(FirebaseFirestore.getInstance())
    }

    // 🔥 Start listening ONLY when screen opens
    LaunchedEffect(Unit) {
        repository.startListening { list ->
            tables = list
        }
    }

    // 🔥 Stop when leaving
    DisposableEffect(Unit) {
        onDispose {
            repository.stopListening()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        WaiterTableViewGrid(
            tables = tables,
            selectedTable = selectedTableId,
            onTableClick = { tableId ->

                val table = tables.first { it.table.id == tableId }.table

                posSessionViewModel.setTable(
                    tableId = table.id,
                    tableName = table.tableName
                )

                cartViewModel.initSession("DINE_IN", table.id)

                navController.navigate("pos") {
                    launchSingleTop = true
                }
            }
        )
    }
}