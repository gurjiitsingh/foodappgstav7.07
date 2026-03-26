package com.it10x.foodappgstav7_07.fiskaly

import android.content.Context

object FiskalyServiceFactory {

    fun create(context: Context, country: String): FiskalyService {
        return when (country) {
            "DE" -> FiskalyServiceImpl(context, true)  // ✅ HERE
            else -> NoOpFiskalyService()
        }
    }
}