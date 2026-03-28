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


    private suspend fun ensureAuthenticated() {

        var token = TokenManager.getToken()

        if (token.isNullOrEmpty()) {

            Log.d("FISKALY", "🔑 Token missing → authenticating")

            val newToken = FiskalyAuthService.authenticate(api)

            TokenManager.saveToken(newToken)

            Log.d("FISKALY", "✅ Token saved")
        }
    }

    suspend fun startTransaction(): Pair<String, String> {

        ensureAuthenticated()
        val tssId = TssStorage.getTssId(context)
            ?: throw Exception("TSS not initialized")

        val txId = UUID.randomUUID().toString()
//        delay(300)

        var clientId = TssStorage.getClientId(context)

        if (clientId == null) {

            clientId = UUID.randomUUID().toString()

            api.createClient(
                tssId,
                clientId,
                ClientRequest(serial_number = clientId)
            )

            TssStorage.saveClientId(context, clientId)

            Log.d("FISKALY", "✅ NEW CLIENT CREATED: $clientId")

            delay(300)

        } else {
            Log.d("FISKALY", "♻️ REUSING CLIENT: $clientId")
        }

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