package com.it10x.foodappgstav7_07.network.model

data class TransactionResponse(
    val transaction_id: String?,
    val transaction_number: Long?,
    val signature_counter: Long?,
    val signature: String?,
    val log_time_start: String?,   // ✅ nullable
    val log_time_end: String?      // ✅ nullable
)