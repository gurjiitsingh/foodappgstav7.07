package com.it10x.foodappgstav7_07.data.online.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.it10x.foodappgstav7_07.data.online.models.CategorySaleData
import com.it10x.foodappgstav7_07.data.online.models.OrderProductData
import kotlinx.coroutines.tasks.await
import java.util.Date
import com.google.firebase.Timestamp
import com.it10x.foodappgstav7_07.data.online.models.OrderMasterData
import com.it10x.foodappgstav7_07.data.online.models.TotalSalesReportResult
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.get

class ReportsRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getCategorySalesByDate(
        category: String,
        startMillis: Long,
        endMillis: Long
    ): CategorySaleData? {



        val startTimestamp = com.google.firebase.Timestamp(Date(startMillis))
        val endTimestamp = com.google.firebase.Timestamp(Date(endMillis))

        val snapshot = db.collection("orderProducts")
            .whereEqualTo("categoryName", category)
            .whereGreaterThanOrEqualTo("createdAt", startTimestamp)
            .whereLessThanOrEqualTo("createdAt", endTimestamp)
            .get()
            .await()

        val items = snapshot.documents.mapNotNull {
            it.toObject(OrderProductData::class.java)
        }

        val qty = items.sumOf { it.quantity }
        val sales = items.sumOf { it.finalTotalDouble() }

        return CategorySaleData(
            categoryName = category,
            totalQty = qty,
            totalSales = sales
        )
    }

    suspend fun getCategorySales(): List<CategorySaleData> {

        return try {

            val snapshot = db.collection("orderProducts")
                .get()
                .await()

            val items = snapshot.documents.mapNotNull {
                it.toObject(OrderProductData::class.java)
            }

            val grouped = items.groupBy { it.categoryName }

            grouped.map { (category, products) ->

                val qty = products.sumOf { it.quantity }
                val sales = products.sumOf { it.finalTotalDouble() }

                CategorySaleData(
                    categoryName = category,
                    totalQty = qty,
                    totalSales = sales
                )
            }

        } catch (e: Exception) {

            Log.e("REPORTS", "Category sales failed", e)
            emptyList()
        }
    }

    suspend fun getTotalSalesReport_(
        startMillis: Long,
        endMillis: Long
    ): TotalSalesReportResult {

        return try {

            val startTimestamp = Timestamp(Date(startMillis))
            val endTimestamp = Timestamp(Date(endMillis))

            val snapshot = db.collection("orderMaster") // 🔥 CHANGE if needed
                .whereGreaterThanOrEqualTo("createdAt", startTimestamp)
                .whereLessThanOrEqualTo("createdAt", endTimestamp)
                .whereEqualTo("status", "PAID") // optional but recommended
                .get()
                .await()

            var totalSales = 0.0
            var totalDiscount = 0.0
            var totalTax = 0.0

            for (doc in snapshot.documents) {

                val grandTotal = doc.getDouble("grandTotal") ?: 0.0
                val discount = doc.getDouble("discountTotal") ?: 0.0
                val tax = doc.getDouble("taxTotal") ?: 0.0

                totalSales += grandTotal
                totalDiscount += discount
                totalTax += tax
            }

            TotalSalesReportResult(
                totalSales = totalSales,
                totalDiscount = totalDiscount,
                totalTax = totalTax
            )

        } catch (e: Exception) {

            Log.e("REPORTS", "Total sales report failed", e)

            TotalSalesReportResult()
        }
    }

    suspend fun getTotalSalesReport(
        startMillis: Long,
        endMillis: Long
    ): TotalSalesReportResult {


        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val startDate = sdf.format(Date(startMillis))
        val endDate = sdf.format(Date(endMillis))

        android.util.Log.d("REPORT_DEBUG", "START = $startDate | END = $endDate")

        val snapshot = db.collection("orderMaster")
            .whereGreaterThanOrEqualTo("orderDate", startDate)
            .whereLessThanOrEqualTo("orderDate", endDate)
            .get()
            .await()

        val orders = snapshot.documents.mapNotNull {
            it.toObject(OrderMasterData::class.java)
        }

        android.util.Log.d("REPORT_DEBUG", "FILTERED SIZE = ${orders.size}")

        val totalSales = orders.sumOf { it.grandTotal ?: 0.0 }
        val totalDiscount = orders.sumOf { it.discountTotal ?: 0.0 }
        val totalTax = orders.sumOf { it.taxTotal ?: 0.0 }

        return TotalSalesReportResult(
            totalSales = totalSales,
            totalDiscount = totalDiscount,
            totalTax = totalTax
        )
    }

}