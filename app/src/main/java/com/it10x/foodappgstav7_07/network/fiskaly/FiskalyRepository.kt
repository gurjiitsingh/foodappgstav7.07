package com.it10x.foodappgstav7_07.network.fiskaly

import android.content.Context
import android.util.Log
import com.it10x.foodappgstav7_07.network.model.*
import com.it10x.foodappgstav7_07.storage.TssStorage
import kotlinx.coroutines.delay
import java.util.UUID

class FiskalyRepository(
    private val context: Context,
    private val api: FiskalyApi
) {

    suspend fun startTransaction(): Pair<String, String> {
        val tssId = TssStorage.getTssId(context)
            ?: throw Exception("TSS not initialized")

        val clientId = UUID.randomUUID().toString()
        val txId = UUID.randomUUID().toString()

        // ✅ Create client
        api.createClient(
            tssId,
            clientId,
            ClientRequest(serial_number = clientId)
        )

        delay(300)

        // ✅ Start TX
        api.startTransaction(
            tssId = tssId,
            txId = txId,
            txRevision = 1,
            request = StartTransactionRequest(client_id = clientId)
        )

        Log.d("FISKALY", "TX STARTED → $txId")

        return Pair(txId, clientId)
    }

    suspend fun finishTransaction(
        txId: String,
        clientId: String,
        totalAmount: Double,
        paymentType: String
    ) {
        val tssId = TssStorage.getTssId(context)
            ?: throw Exception("TSS not initialized")

        val finishRequest = FinishTransactionRequest(
            client_id = clientId,
            schema = Schema(
                standard_v1 = StandardV1(
                    receipt = Receipt(
                        amounts_per_vat_rate = listOf(
                            VatAmount(
                                vat_rate = "NORMAL",
                                amount = String.format("%.2f", totalAmount)
                            )
                        ),
                        amounts_per_payment_type = listOf(
                            PaymentAmount(
                                payment_type = paymentType,
                                amount = String.format("%.2f", totalAmount)
                            )
                        )
                    )
                )
            )
        )

        val response = api.finishTransaction(
            tssId = tssId,
            txId = txId,
            txRevision = 2,
            request = finishRequest
        )

        Log.d("FISKALY", "TX FINISHED → $response")
    }

    suspend fun cancelTransaction(
        txId: String,
        clientId: String
    ) {
        val tssId = TssStorage.getTssId(context)
            ?: throw Exception("TSS not initialized")

        val request = FinishTransactionRequest(
            client_id = clientId,
            state = "CANCELLED",
            schema = Schema(
                standard_v1 = StandardV1(
                    receipt = Receipt(
                        amounts_per_vat_rate = listOf(
                            VatAmount("NORMAL", "0.00")
                        ),
                        amounts_per_payment_type = listOf(
                            PaymentAmount("CASH", "0.00")
                        )
                    )
                )
            )
        )

        api.finishTransaction(
            tssId = tssId,
            txId = txId,
            txRevision = 2,
            request = request
        )

        Log.d("FISKALY", "TX CANCELLED → $txId")
    }
}