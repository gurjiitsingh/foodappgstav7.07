package com.it10x.foodappgstav7_07.utils

import com.it10x.foodappgstav7_07.data.online.models.OrderMasterData

fun OrderMasterData.createdAtMillis(): Long {
    return createdAt?.toDate()?.time ?: createdAtMillis
}