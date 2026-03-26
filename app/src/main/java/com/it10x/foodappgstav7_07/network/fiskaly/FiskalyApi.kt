package com.it10x.foodappgstav7_07.network.fiskaly


import com.it10x.foodappgstav7_07.network.model.ActivateTssRequest
import com.it10x.foodappgstav7_07.network.model.AuthRequest
import com.it10x.foodappgstav7_07.network.model.AuthResponse
import com.it10x.foodappgstav7_07.network.model.TseResponse
import com.it10x.foodappgstav7_07.network.model.ClientRequest
import com.it10x.foodappgstav7_07.network.model.ClientResponse
import com.it10x.foodappgstav7_07.network.model.FinishTransactionRequest
import com.it10x.foodappgstav7_07.network.model.StartTransactionRequest
import com.it10x.foodappgstav7_07.network.model.TransactionResponse
import com.it10x.foodappgstav7_07.network.model.ExportResponse
import com.it10x.foodappgstav7_07.network.model.TssRequest
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface FiskalyApi {



    @POST("auth")
    suspend fun authenticate(
        @Body request: AuthRequest
    ): AuthResponse


    // CREATE TSS (requires GUID)
    @PUT("tss/{tssId}")
    suspend fun createTss(
        @Path("tssId") tssId: String,
        @Body request: TssRequest
    ): TseResponse



    // ACTIVATE TSS
//    @PATCH("tss/{tssId}")
//    suspend fun activateTss(
//        @Path("tssId") tssId: String,
//        @Body request: ActivateTssRequest
//    ): TseResponse
    @PATCH("tss/{tssId}")
    suspend fun updateTss(
        @Path("tssId") tssId: String,
        @Body request: UpdateTssRequest
    ): TseResponse


    // CHECK TSS STATUS
    @GET("tss/{tssId}")
    suspend fun getTssStatus(
        @Path("tssId") tssId: String
    ): TseResponse


    // CLIENT REGISTRATION (still under tse/v1)
    @POST("tse/v1/clients")
    suspend fun createClient(
        @Body request: ClientRequest
    ): ClientResponse


    // START TRANSACTION
    @POST("tse/v1/transactions")
    suspend fun startTransaction(
        @Body request: StartTransactionRequest
    ): TransactionResponse


    // FINISH TRANSACTION
    @PATCH("tse/v1/transactions/{transactionId}")
    suspend fun finishTransaction(
        @Path("transactionId") transactionId: String,
        @Body request: FinishTransactionRequest
    ): TransactionResponse


    // EXPORT DATA
    @POST("tse/v1/exports")
    suspend fun createExport(): ExportResponse


    fun buildProcessData(amount: Double): String {
        return """
    {
      "receipt": {
        "type": "receipt",
        "amount": ${amount.toInt()},
        "currency": "EUR"
      }
    }
    """.trimIndent()
    }





    @PATCH("tss/{tssId}/admin")
    suspend fun setAdminPin(
        @Path("tssId") tssId: String,
        @Body body: AdminPinRequest
    ): Response<Unit>

    @POST("tss/{tssId}/admin/auth")
    suspend fun adminAuth(
        @Path("tssId") tssId: String,
        @Body body: AdminAuthRequest
    ): Response<Unit>


    fun buildFinishRequest(amount: Double): FinishTransactionRequest {
        return FinishTransactionRequest(
            process_data = """
            {
              "receipt": {
                "type": "receipt",
                "amount": ${amount.toInt()},
                "currency": "EUR"
              }
            }
            """.trimIndent()
        )
    }

}

data class UpdateTssRequest(
    val state: String,
    val admin_pin: String? = null
)

data class AdminPinRequest(
    val admin_puk: String,
    val new_admin_pin: String
)

data class AdminAuthRequest(
    val admin_pin: String
)