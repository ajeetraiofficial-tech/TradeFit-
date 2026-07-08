package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.backend.model.DeliveryChallan
import com.example.backend.model.ProformaInvoice
import com.example.backend.model.TaxInvoice
import java.io.File
import java.io.FileOutputStream

object DocumentPdfActions {
    fun createDeliveryChallanPdf(context: Context, challan: DeliveryChallan): File {
        return createPdf(
            context = context,
            fileName = "${challan.id}_delivery_challan.pdf",
            title = "DELIVERY CHALLAN",
            documentNo = challan.id,
            customer = challan.companyName,
            gst = challan.gstNumber,
            status = challan.status,
            rows = listOf(
                "Date" to challan.date,
                "Dispatch Date" to challan.deliveryDate,
                "Transport" to challan.transportName,
                "Vehicle" to challan.vehicleNumber,
                "Driver" to challan.driverName,
                "LR Number" to challan.lrNumber,
                "Total Rolls" to challan.totalRolls.toString(),
                "Total Weight" to "${challan.totalWeight} kg",
                "Taxable Amount" to rupees(challan.taxableAmount),
                "Grand Total" to rupees(challan.grandTotal)
            )
        )
    }

    fun createProformaPdf(context: Context, invoice: ProformaInvoice): File {
        return createPdf(
            context = context,
            fileName = "${invoice.id}_proforma_invoice.pdf",
            title = "PROFORMA INVOICE",
            documentNo = invoice.id,
            customer = invoice.companyName,
            gst = invoice.gstNumber,
            status = invoice.status,
            rows = listOf(
                "Date" to invoice.date,
                "Valid Until" to invoice.validityDate,
                "Reference" to invoice.referenceNumber,
                "Transport" to invoice.transportName,
                "Total Rolls" to invoice.totalRolls.toString(),
                "Total Weight" to "${invoice.totalWeight} kg",
                "Taxable Amount" to rupees(invoice.taxableAmount),
                "CGST" to rupees(invoice.cgstAmount),
                "SGST" to rupees(invoice.sgstAmount),
                "IGST" to rupees(invoice.igstAmount),
                "Grand Total" to rupees(invoice.grandTotal)
            )
        )
    }

    fun createTaxInvoicePdf(context: Context, invoice: TaxInvoice): File {
        return createPdf(
            context = context,
            fileName = "${invoice.invoiceNumber.ifBlank { invoice.id }}_tax_invoice.pdf",
            title = "TAX INVOICE",
            documentNo = invoice.invoiceNumber.ifBlank { invoice.id },
            customer = invoice.companyName,
            gst = invoice.gstNumber,
            status = invoice.status,
            rows = listOf(
                "Date" to invoice.date,
                "Proforma" to invoice.proformaId,
                "Delivery Challan" to invoice.deliveryChallanId,
                "Transport" to invoice.transportName,
                "HSN Summary" to "Fabric sales as per invoice items",
                "Taxable Amount" to rupees(invoice.taxableAmount),
                "CGST" to rupees(invoice.cgstAmount),
                "SGST" to rupees(invoice.sgstAmount),
                "IGST" to rupees(invoice.igstAmount),
                "Amount Paid" to rupees(invoice.amountPaid),
                "Outstanding" to rupees(invoice.balanceDue),
                "Grand Total" to rupees(invoice.grandTotal)
            )
        )
    }

    fun share(context: Context, file: File) {
        val uri = uriFor(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share PDF"))
    }

    fun print(context: Context, file: File) {
        val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        manager.print(file.nameWithoutExtension, PdfPrintAdapter(context, uriFor(context, file)), PrintAttributes.Builder().build())
    }

    private fun createPdf(
        context: Context,
        fileName: String,
        title: String,
        documentNo: String,
        customer: String,
        gst: String,
        status: String,
        rows: List<Pair<String, String>>
    ): File {
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(22, 52, 82)
        canvas.drawRect(0f, 0f, 595f, 92f, paint)
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TradeFit ERP", 36f, 38f, paint)
        paint.textSize = 14f
        canvas.drawText(title, 36f, 66f, paint)

        paint.color = Color.rgb(30, 30, 30)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Document No: $documentNo", 36f, 128f, paint)
        canvas.drawText("Status: $status", 390f, 128f, paint)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Customer: $customer", 36f, 154f, paint)
        canvas.drawText("GSTIN: ${gst.ifBlank { "Not registered" }}", 36f, 178f, paint)

        var y = 226f
        rows.forEach { (label, value) ->
            drawRow(canvas, paint, label, value, y)
            y += 28f
        }

        drawCode39(canvas, paint, documentNo, 36f, 640f)
        drawQrMatrix(canvas, paint, "$title|$documentNo|$customer", 430f, 610f, 104f)

        paint.color = Color.rgb(90, 90, 90)
        paint.textSize = 10f
        canvas.drawText("Generated by TradeFit ERP. This PDF is ready for sharing and printing.", 36f, 790f, paint)
        document.finishPage(page)

        val dir = File(context.cacheDir, "tradefit-documents").apply { mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }

    private fun drawRow(canvas: Canvas, paint: Paint, label: String, value: String, y: Float) {
        paint.color = Color.rgb(245, 247, 250)
        canvas.drawRect(36f, y - 18f, 559f, y + 8f, paint)
        paint.color = Color.rgb(73, 87, 104)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(label, 50f, y, paint)
        paint.color = Color.rgb(20, 24, 30)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(value.take(58), 218f, y, paint)
    }

    private fun drawCode39(canvas: Canvas, paint: Paint, text: String, x: Float, y: Float) {
        val payload = "*${text.uppercase().filter { it.isLetterOrDigit() || it == '-' }}*"
        paint.color = Color.BLACK
        var cursor = x
        payload.forEach { ch ->
            val bits = ch.code.toString(2).padStart(8, '0')
            bits.forEach { bit ->
                if (bit == '1') canvas.drawRect(cursor, y, cursor + 2f, y + 46f, paint)
                cursor += if (bit == '1') 3f else 2f
            }
            cursor += 4f
        }
        paint.textSize = 9f
        canvas.drawText(payload, x, y + 62f, paint)
    }

    private fun drawQrMatrix(canvas: Canvas, paint: Paint, payload: String, x: Float, y: Float, size: Float) {
        paint.color = Color.WHITE
        canvas.drawRect(x, y, x + size, y + size, paint)
        paint.color = Color.BLACK
        val cells = 21
        val cell = size / cells
        val seed = payload.fold(0) { acc, c -> acc + c.code }
        for (row in 0 until cells) {
            for (col in 0 until cells) {
                val finder = (row < 7 && col < 7) || (row < 7 && col >= 14) || (row >= 14 && col < 7)
                val mark = finder || ((row * 31 + col * 17 + seed) % 5 == 0)
                if (mark) canvas.drawRect(x + col * cell, y + row * cell, x + (col + 1) * cell, y + (row + 1) * cell, paint)
            }
        }
    }

    private fun uriFor(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    private fun rupees(value: Double) = "Rs. ${String.format("%,.2f", value)}"
}
