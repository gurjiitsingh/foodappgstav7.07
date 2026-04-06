package com.it10x.foodappgstav7_07.printer

import android.content.Context
import android.util.Log
import com.it10x.foodappgstav7_07.data.PrinterConfig
import com.it10x.foodappgstav7_07.data.PrinterPreferences
import com.it10x.foodappgstav7_07.data.PrinterRole
import com.it10x.foodappgstav7_07.data.PrinterType
import com.it10x.foodappgstav7_07.data.print.OutletInfo
import com.it10x.foodappgstav7_07.printer.bluetooth.BluetoothPrinter
import com.it10x.foodappgstav7_07.printer.lan.LanPrinter
import com.it10x.foodappgstav7_07.printer.usb.USBPrinter
import com.it10x.foodappgstav7_07.data.print.OutletMapper
import com.it10x.foodappgstav7_07.data.pos.AppDatabaseProvider
import com.it10x.foodappgstav7_07.data.pos.entities.PosKotItemEntity
import com.it10x.foodappgstav7_07.ui.sales.SalesUiState
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.it10x.foodappgstav7_07.printer.PrintJob
import kotlinx.coroutines.runBlocking


class PrinterManager(
    private val context: Context
) {

    private val prefs by lazy { PrinterPreferences(context) }
    fun appContext(): Context = context.applicationContext


    // --------------------------------
    // NEW PRINT JOB STRATAGY
    // --------------------------------


    fun print(job: PrintJob, onResult: (Boolean) -> Unit = {}) {
        when (job) {

            is PrintJob.SalesReport -> {

                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.salesFullReport(
                    state = job.state,
                    info = info,
                    width = width,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }


            is PrintJob.CategoryWiseSalesReport -> {

                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.categoryWiseSalesReport(
                    categorySales = job.categorySales,
                    info = info,
                    width = width,
                    fromMillis = job.fromMillis,
                    toMillis = job.toMillis,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }

            is PrintJob.SingleCategoryDetail -> {
                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.salesBySingleCategory(
                    category = job.category,
                    items = job.items,
                    outletInfo = info,
                    width = width,
                    fromMillis = job.fromMillis,
                    toMillis = job.toMillis,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }

            // ✅ NEW: Category Summary
//            is PrintJob.SingleCategorySummary -> {
//                printCategorySummary(
//                    PrinterRole.BILLING,
//                    job.category,
//                    job.qty,
//                    job.amount
//                )
//            }

            is PrintJob.TotalSalesReport -> {

                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.totalSalesReport(
                    beforeDiscount = job.beforeDiscount,
                    discount = job.discount,
                    afterDiscount = job.afterDiscount,
                    tax = job.tax,
                    info = info,
                    width = width,
                    fromMillis = job.fromMillis,
                    toMillis = job.toMillis,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }


            is PrintJob.CategorySummary -> {

                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.salesCategorySummary(
                    category = job.category,
                    totalQty = job.qty,
                    totalAmount = job.amount,
                    info = info,
                    width = width,
                    fromMillis = job.fromMillis,
                    toMillis = job.toMillis,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }

            is PrintJob.ProductSummary -> {
                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.salesProductSummary(
                    product = job.product,
                    qty = job.qty,
                    amount = job.amount,
                    info = info,
                    width = width,
                    fromMillis = job.fromMillis,
                    toMillis = job.toMillis,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }

            // ✅ ADD THIS BLOCK
            is PrintJob.CategoryProductReport -> {

                val size = prefs.getPrinterSize(PrinterRole.BILLING) ?: "80mm"
                val width = if (size == "80mm") 48 else 32

                val outletDao = AppDatabaseProvider.get(context).outletDao()
                val outletEntity = runBlocking { outletDao.getOutlet() }
                val info = OutletMapper.fromEntity(outletEntity)

                val text = ReceiptFormatter.salesCategoryProductList(
                    category = job.category,
                    items = job.items,
                    outletInfo = info,
                    width = width,
                    fromMillis = job.fromMillis,
                    toMillis = job.toMillis,
                    printMillis = job.printMillis
                )

                printText(PrinterRole.BILLING, text)
            }
        }
    }





    // --------------------------------
    // TEST PRINT (already OK)
    // --------------------------------
    fun printTest(
        config: PrinterConfig,
        onResult: (Boolean) -> Unit
    ) {
        val roleLabel = config.role.name

        when (config.type) {

            PrinterType.BLUETOOTH -> {
            //    Log.d("PRINT_BT", "Test BT address='${config.bluetoothAddress}'")
                if (config.bluetoothAddress.isBlank()) {
                    onResult(false)
                    return
                }
                BluetoothPrinter.printTest(
                    config.bluetoothAddress,
                    roleLabel,
                    onResult
                )
            }

            PrinterType.LAN -> {
                if (config.ip.isBlank()) {
                    onResult(false)
                    return
                }
                LanPrinter.printTest(
                    config.ip,
                    config.port,
                    roleLabel,
                    onResult
                )
            }



            PrinterType.USB -> {
                val device = config.usbDevice ?: run {
                    onResult(false)
                    return
                }

                USBPrinter.printTest(
                    context = context,
                    device = device,
                    roleLabel = roleLabel,
                    onResult = onResult
                )
            }






            PrinterType.WIFI -> onResult(false)
        }
    }

    // --------------------------------
    // REAL PRINT (USED BY BUTTON + AUTO)
    // --------------------------------
  fun printText(
    role: PrinterRole,
    text: String,
    onResult: (Boolean) -> Unit = {}
) {

        Log.e(
            "PRINTTEST",
            "\n================= printText =================\n$text\n=================================================="
        )
    val config = prefs.getPrinterConfig(role)
    if (config == null) {
        Log.e("PRINTTEST", "No printer configured for role=$role")
        onResult(false)
        return
    }

    //Log.d("PRINT", "Printing role=$role type=${config.type}")
    //  var  text1="kljkl"
    when (config.type) {

        PrinterType.BLUETOOTH -> {
            if (config.bluetoothAddress.isBlank()) {
                onResult(false)
                return
            }
            BluetoothPrinter.printText(
                config.bluetoothAddress,
                text,
                onResult
            )
        }

        PrinterType.LAN -> {
            if (config.ip.isBlank()) {
                onResult(false)
                return
            }
            LanPrinter.printText(
                config.ip,
                config.port,
                text,
                onResult
            )
        }

        PrinterType.USB -> {
            val device = config.usbDevice ?: run {
                onResult(false)
                return
            }
            USBPrinter.printText(
                text,
       onResult
            )



        }

        PrinterType.WIFI -> onResult(false)
    }
}

    suspend fun printTextNewSuspend(
        role: PrinterRole,
        order: PrintOrder,
        outletInfo: OutletInfo
    ): Boolean = suspendCancellableCoroutine { cont ->

        cont.invokeOnCancellation {
            // Optional: cancel printer job if you support it
            Log.e("PRINT", "Coroutine cancelled")
        }

        printTextNewImproved(role, order, outletInfo) { success ->
            if (cont.isActive) {
                cont.resume(success)
            }
        }
    }

    fun printTextNew(
        role: PrinterRole,
        order: PrintOrder,
        onResult: (Boolean) -> Unit = {}
    ) {
      //  Log.e("PRINT_NEW", "Printing for role=$role")

        // Get printer configuration and preferences
        val config = prefs.getPrinterConfig(role)


        if (config == null) {
            Log.e("PPRINTTEST", "No printer configured for role=$role")
            onResult(false)
            return
        }

        // ✅ Select format based on page size
        val size = prefs.getPrinterSize(role) ?: "80mm"

        // ✅ Auto-load outlet info if not provided

        val outletDao = AppDatabaseProvider.get(context).outletDao()
        val outletEntity = runBlocking { outletDao.getOutlet() }
        val info = OutletMapper.fromEntity(outletEntity)




        // ✅ Select format based on printer page size
        val receiptText = when (size) {
            "80mm" -> ReceiptFormatter.billing48(order, info)
            else -> ReceiptFormatter.billing(order, info)
        }


        Log.e(
            "PRINTTEST",
            "\n================= BILL NEWTEXT =================\n$receiptText\n=================================================="
        )

        // ✅ Printing logic (kept same as before)
        when (config.type) {
            PrinterType.BLUETOOTH -> {
                if (config.bluetoothAddress.isBlank()) {
                    Log.e("PRINT_NEW", "Bluetooth address missing")
                    onResult(false)
                    return
                }
                BluetoothPrinter.printText(
                    config.bluetoothAddress,
                    receiptText,
                    onResult
                )
            }

            PrinterType.LAN -> {
                if (config.ip.isBlank()) {
                    Log.e("PRINT_NEW", "LAN IP missing")
                    onResult(false)
                    return
                }
                LanPrinter.printText(
                    config.ip,
                    config.port,
                    receiptText,
                    onResult
                )
            }

            PrinterType.USB -> {
                val device = config.usbDevice ?: run {
                    Log.e("PRINT_NEW", "USB device not found")
                    onResult(false)
                    return
                }
                USBPrinter.printText(
                    receiptText,
                    onResult
                )
            }

            PrinterType.WIFI -> {
                Log.e("PRINT_NEW", "WiFi printing not supported yet")
                onResult(false)
            }
        }
    }


    fun printTextNewImproved(
        role: PrinterRole,
        order: PrintOrder,
        outletInfo: OutletInfo,
        onResult: (Boolean) -> Unit = {}
    ) {
        //  Log.e("PRINT_NEW", "Printing for role=$role")

        // Get printer configuration and preferences
        val config = prefs.getPrinterConfig(role)


        if (config == null) {
            Log.e("PPRINTTEST", "No printer configured for role=$role")
            onResult(false)
            return
        }

        // ✅ Select format based on page size
        val size = prefs.getPrinterSize(role) ?: "80mm"

        // ✅ Auto-load outlet info if not provided

        val info = outletInfo


//        Log.d(
//            "PRINT_NEW",
//            "Outlet Entity = $outletEntity   $info"
//        )

        // ✅ Select format based on printer page size
        val receiptText = when (size) {
            "80mm" -> ReceiptFormatter.billing48(order, info)
            else -> ReceiptFormatter.billing(order, info)
        }


        Log.e(
            "PRINTTEST",
            "\n================= BILL NEWTEXT =================\n$receiptText\n=================================================="
        )

        // ✅ Printing logic (kept same as before)
        when (config.type) {
            PrinterType.BLUETOOTH -> {
                if (config.bluetoothAddress.isBlank()) {
                    Log.e("PRINT_NEW", "Bluetooth address missing")
                    onResult(false)
                    return
                }
                BluetoothPrinter.printText(
                    config.bluetoothAddress,
                    receiptText,
                    onResult
                )
            }

            PrinterType.LAN -> {
                if (config.ip.isBlank()) {
                    Log.e("PRINT_NEW", "LAN IP missing")
                    onResult(false)
                    return
                }
                LanPrinter.printText(
                    config.ip,
                    config.port,
                    receiptText,
                    onResult
                )
            }

            PrinterType.USB -> {
                val device = config.usbDevice ?: run {
                    Log.e("PRINT_NEW", "USB device not found")
                    onResult(false)
                    return
                }
                USBPrinter.printText(
                    receiptText,
                    onResult
                )
            }

            PrinterType.WIFI -> {
                Log.e("PRINT_NEW", "WiFi printing not supported yet")
                onResult(false)
            }
        }
    }


    fun printTextKitchen(
        role: PrinterRole,
        sessionKey: String,
        orderType: String,
        items: List<PosKotItemEntity>,
        onResult: (Boolean) -> Unit = {}
    ) {

        val config = prefs.getPrinterConfig(role)
        if (config == null) {
            Log.e("PRINTTEST", "No printer configured for role=$role")
            onResult(false)
            return
        }

        val text = ReceiptFormatter.posKitchen(
            sessionKey ,
            orderType,
            items
        )

        Log.e(
            "PRINTTEST",
            "\n================= KITCHEN RECEIPT =================\n$text\n=================================================="
        )

        //Log.d("PRINT", "Printing role=$role type=${config.type}")
        //  var  text1="kljkl"
        when (config.type) {

            PrinterType.BLUETOOTH -> {
                if (config.bluetoothAddress.isBlank()) {
                    onResult(false)
                    return
                }
                BluetoothPrinter.printText(
                    config.bluetoothAddress,
                    text,
                    onResult
                )
            }

            PrinterType.LAN -> {
                if (config.ip.isBlank()) {
                    onResult(false)
                    return
                }
                LanPrinter.printText(
                    config.ip,
                    config.port,
                    text,
                    onResult
                )
            }

            PrinterType.USB -> {
                val device = config.usbDevice ?: run {
                    onResult(false)
                    return
                }
                USBPrinter.printText(
                    text,
                    onResult
                )


            }

            PrinterType.WIFI -> onResult(false)
        }
    }



    // --------------------------------
    // OPTIONAL
    // --------------------------------
    fun printTestForRole(
        configProvider: () -> PrinterConfig?,
        onResult: (Boolean) -> Unit
    ) {
        val config = configProvider()
        if (config == null) {
            onResult(false)
            return
        }
        printTest(config, onResult)
    }



    fun printSalesReport(
        role: PrinterRole,
        state: SalesUiState,
        onResult: (Boolean) -> Unit = {}
    ) {

        val config = prefs.getPrinterConfig(role)
        if (config == null) {
            onResult(false)
            return
        }

        val size = prefs.getPrinterSize(role) ?: "80mm"

        val outletDao = AppDatabaseProvider.get(context).outletDao()
        val outletEntity = runBlocking { outletDao.getOutlet() }
        val info = OutletMapper.fromEntity(outletEntity)

        val width = if (size == "80mm") 48 else 32

        val text = ReceiptFormatter.salesReport(
            state,
            info,
            width
        )

        printText(role, text, onResult)
    }


//    fun printCategorySummary(
//        role: PrinterRole,
//        category: String,
//        totalQty: Int,
//        totalAmount: Double,
//        onResult: (Boolean) -> Unit = {}
//    ) {
//
//
//
//
//
//        val config = prefs.getPrinterConfig(role)
//        if (config == null) {
//            onResult(false)
//            return
//        }
//
//        val size = prefs.getPrinterSize(role) ?: "80mm"
//        val width = if (size == "80mm") 48 else 32
//
//        val outletDao = AppDatabaseProvider.get(context).outletDao()
//        val outletEntity = runBlocking { outletDao.getOutlet() }
//        val info = OutletMapper.fromEntity(outletEntity)
//
//        val text = ReceiptFormatter.salesCategorySummary(
//            category,
//            totalQty,
//            totalAmount,
//            info,
//            width
//        )
//
//        val debugText = text.replace(Regex("[\\x00-\\x1F]"), "\n================= KITCHEN RECEIPT =================\n$text\n==================================================")
//        Log.e("PRINTTEST", debugText)
//
//        printText(role, text, onResult)
//    }


//    fun printSingleCategorySales(
//        role: PrinterRole,
//        category: String,
//        items: Map<String, Pair<Int, Double>>,
//        onResult: (Boolean) -> Unit = {}
//    ) {
//
//        val config = prefs.getPrinterConfig(role)
//        if (config == null) {
//            onResult(false)
//            return
//        }
//
//        val size = prefs.getPrinterSize(role) ?: "80mm"
//        val width = if (size == "80mm") 48 else 32
//
//        val outletDao = AppDatabaseProvider.get(context).outletDao()
//        val outletEntity = runBlocking { outletDao.getOutlet() }
//        val info = OutletMapper.fromEntity(outletEntity)
//
//        val text = ReceiptFormatter.salesBySingleCategory(
//            category,
//            items,
//            info,
//            width
//        )
//
//        printText(role, text, onResult)
//    }



}
