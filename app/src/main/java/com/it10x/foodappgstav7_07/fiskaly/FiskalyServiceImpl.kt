package com.it10x.foodappgstav7_07.fiskaly

import android.content.Context
import android.util.Log
import com.it10x.foodappgstav7_07.network.fiskaly.FiskalyClient
import com.it10x.foodappgstav7_07.network.model.StartTransactionRequest
import com.it10x.foodappgstav7_07.storage.ClientStorage
import com.it10x.foodappgstav7_07.storage.TransactionStorage

class FiskalyServiceImpl(
    private val context: Context,
    private val enabled: Boolean
) : FiskalyService {

    override fun isEnabled(): Boolean = enabled

    override suspend fun startTransaction(
        sessionId: String,
        amount: Double
    ): String? {
        return try {
            val api = FiskalyClient.api

            val clientId = ClientStorage.getClientId(context)
                ?: throw Exception("Client ID missing")

            val response = api.startTransaction(
                StartTransactionRequest(
                    client_id = clientId,
                    process_data = api.buildProcessData(amount)
                )
            )

            val txnId = response.transaction_number.toString()

            // ✅ FIXED HERE
            TransactionStorage.saveTransactionId(context, sessionId, txnId)

            Log.d("FISKALY", "START OK: $txnId")

            txnId

        } catch (e: Exception) {
            Log.e("FISKALY", "START FAIL", e)
            null
        }
    }

    override suspend fun finishTransaction(
        transactionId: String,
        amount: Double
    ) {
        try {
            val api = FiskalyClient.api

            api.finishTransaction(
                transactionId,
                api.buildFinishRequest(amount)
            )

            Log.d("FISKALY", "FINISH OK: $transactionId")

        } catch (e: Exception) {
            Log.e("FISKALY", "FINISH FAIL", e)
        }
    }
}