package com.it10x.foodappgstav7_07.fiskaly

class NoOpFiskalyService : FiskalyService {

    override fun isEnabled(): Boolean = false

    override suspend fun startTransaction(
        sessionId: String,
        amount: Double
    ): String? = null

    override suspend fun finishTransaction(
        transactionId: String,
        amount: Double
    ) {
        // ✅ DO NOTHING
    }
}