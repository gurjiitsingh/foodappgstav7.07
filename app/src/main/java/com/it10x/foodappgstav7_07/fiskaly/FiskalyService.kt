package com.it10x.foodappgstav7_07.fiskaly

interface FiskalyService {

    suspend fun startTransaction(
        sessionId: String,
        amount: Double
    ): String?

    suspend fun finishTransaction(
        transactionId: String,
        amount: Double
    )

    fun isEnabled(): Boolean
}