package com.it10x.foodappgstav7_07.fiskaly

import android.content.Context

object FiskalyProvider {

    fun provide(context: Context): FiskalyService {

        val country = getCountry(context)

        return when (country) {
            "DE" -> FiskalyServiceImpl(context, true) // ✅ FIXED
            else -> NoOpFiskalyService()
        }
    }

    private fun getCountry(context: Context): String {
        // 🔥 later: dynamic (Firebase / client config)
        return "DE"
    }
}