package com.it10x.foodappgstav7_07.network.model


data class StartTransactionRequest(
    val client_id: String,
    val process_type: String = "Kassenbeleg-V1",
    val process_data: String,
    val state: String = "ACTIVE"
)

//data class StartTransactionRequest(
//    val tse_id: String,
//    val client_id: String
//)