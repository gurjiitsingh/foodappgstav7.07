package com.it10x.foodappgstav7_07.network.model

data class FinishTransactionRequest(
    val process_type: String = "Kassenbeleg-V1",
    val process_data: String,
    val state: String = "FINISHED"
)

//data class FinishTransactionRequest(
//    val state: String = "FINISHED"
//)