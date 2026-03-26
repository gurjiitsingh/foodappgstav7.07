package com.it10x.foodappgstav7_07.network.fiskaly

import com.it10x.foodappgstav7_07.network.model.AuthRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FiskalyAuthService {

    suspend fun authenticate(api: FiskalyApi): String {

        val response = api.authenticate(
            AuthRequest(
                api_key = "test_cknta59sju6xr6elmxdhxgp3m_test-german",
                api_secret = "SlDVHFx355axCFF18CjbSBDOvPA7CqJPVtlTxjQbTK2"
            )
        )



        TokenManager.saveToken(response.access_token)

        return response.access_token
    }
}