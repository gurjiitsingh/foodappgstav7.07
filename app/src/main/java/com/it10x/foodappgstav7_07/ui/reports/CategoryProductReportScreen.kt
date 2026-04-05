package com.it10x.foodappgstav7_07.ui.reports

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.it10x.foodappgstav7_07.ui.components.CategoryPickerDialog
import com.it10x.foodappgstav7_07.viewmodel.OnlineReportsViewModel
import com.it10x.foodappgstav7_07.ui.reports.model.ProductReportItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategoryProductReportScreen(
    navController: NavController,
    viewModel: OnlineReportsViewModel
) {

    val context = LocalContext.current

    // ---------------- DATE ----------------
    val startCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val endCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    var startDate by remember { mutableStateOf(startCalendar.timeInMillis) }
    var endDate by remember { mutableStateOf(endCalendar.timeInMillis) }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ---------------- CATEGORY ----------------
    val categories by viewModel.categories.collectAsState()
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf("Select Category") }
    var showCategoryDialog by remember { mutableStateOf(false) }

    // ---------------- RESULT ----------------
    val reportList by viewModel.categoryProductReport.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // ================= TOP BAR =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // BACK
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                // CATEGORY
                OutlinedButton(onClick = { showCategoryDialog = true }) {
                    Text(selectedCategoryName, fontWeight = FontWeight.Bold)
                }

                // START DATE
                OutlinedButton(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val c = Calendar.getInstance()
                            c.set(y, m, d, 0, 0, 0)
                            c.set(Calendar.MILLISECOND, 0)
                            startDate = c.timeInMillis
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(dateFormatter.format(Date(startDate)))
                }

                // END DATE
                OutlinedButton(onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val c = Calendar.getInstance()
                            c.set(y, m, d, 23, 59, 59)
                            c.set(Calendar.MILLISECOND, 999)
                            endDate = c.timeInMillis
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(dateFormatter.format(Date(endDate)))
                }

                // LOAD
                Button(onClick = {

                    if (selectedCategoryId == null) return@Button

                    viewModel.loadCategoryProductReport(
                        categoryId = selectedCategoryId!!,
                        startMillis = startDate,
                        endMillis = endDate
                    )

                }) {
                    Text("Load")
                }
            }

            Spacer(Modifier.height(20.dp))

            // ================= RESULT =================
            when {

                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                reportList.isEmpty() -> {
                    Text("No data found", color = Color.Gray)
                }

                else -> {

                    CategoryProductHeader()

                    LazyColumn {
                        items(reportList) { item ->
                            CategoryProductRow(item)
                        }
                    }
                }
            }

            // ================= CATEGORY DIALOG =================
            if (showCategoryDialog) {
                CategoryPickerDialog(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = {
                        selectedCategoryId = it.id
                        selectedCategoryName = it.name
                    },
                    onDismiss = { showCategoryDialog = false }
                )
            }
        }
    }
}

