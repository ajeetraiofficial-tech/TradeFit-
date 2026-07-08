package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backend.api.TradeFitApi
import com.example.backend.model.Customer as BackendCustomer
import com.example.backend.model.DeliveryChallan as BackendDeliveryChallan
import com.example.backend.model.DeliveryChallanItem as BackendDeliveryChallanItem
import com.example.backend.model.Fabric as BackendFabric
import com.example.backend.model.ProformaInvoice as BackendProformaInvoice
import com.example.backend.model.ProformaInvoiceItem as BackendProformaInvoiceItem
import com.example.backend.model.StockEntry as BackendStockEntry
import com.example.backend.model.StockTransaction as BackendStockTransaction
import com.example.backend.model.TaxInvoice as BackendTaxInvoice
import com.example.backend.model.TaxInvoiceItem as BackendTaxInvoiceItem
import com.example.backend.service.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

data class DocumentWorkflowUiState(
    val customers: List<BackendCustomer> = emptyList(),
    val fabrics: List<BackendFabric> = emptyList(),
    val rolls: List<BackendStockEntry> = emptyList(),
    val challans: List<BackendDeliveryChallan> = emptyList(),
    val proformas: List<BackendProformaInvoice> = emptyList(),
    val taxInvoices: List<BackendTaxInvoice> = emptyList(),
    val loading: Boolean = true,
    val saving: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class DocumentFormState(
    val id: String = "",
    val customerId: String = "",
    val fabricCode: String = "",
    val stockEntryId: String = "",
    val quantityKg: String = "",
    val ratePerKg: String = "",
    val gstPercent: String = "18",
    val transportName: String = "",
    val vehicleNumber: String = "",
    val driverName: String = "",
    val driverMobile: String = "",
    val lrNumber: String = "",
    val dispatchDate: String = today(),
    val discountValue: String = "0",
    val freightCharges: String = "0",
    val packingCharges: String = "0",
    val otherCharges: String = "0",
    val remarks: String = "",
    val paymentReceived: String = "0"
) {
    val quantity: Double get() = quantityKg.toDoubleOrNull() ?: 0.0
    val rate: Double get() = ratePerKg.toDoubleOrNull() ?: 0.0
    val gst: Double get() = gstPercent.toDoubleOrNull() ?: 0.0
    val discount: Double get() = discountValue.toDoubleOrNull() ?: 0.0
    val freight: Double get() = freightCharges.toDoubleOrNull() ?: 0.0
    val packing: Double get() = packingCharges.toDoubleOrNull() ?: 0.0
    val other: Double get() = otherCharges.toDoubleOrNull() ?: 0.0
    val subtotal: Double get() = money(quantity * rate)
    val taxable: Double get() = money((subtotal - discount + freight + packing + other).coerceAtLeast(0.0))
    val gstAmount: Double get() = money(taxable * gst / 100.0)
    val grandTotal: Double get() = money(taxable + gstAmount)
}

class DocumentWorkflowViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DocumentWorkflowUiState())
    val uiState: StateFlow<DocumentWorkflowUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val customers = TradeFitApi.customers.getActiveCustomers().dataOrEmpty()
            val fabrics = TradeFitApi.fabrics.getActiveFabrics().dataOrEmpty()
            val rolls = TradeFitApi.stock.getAvailableStock().dataOrEmpty()
            val challans = TradeFitApi.deliveryChallans.getAll().dataOrEmpty().filterNot { it.isDeleted }
            val proformas = TradeFitApi.proformaInvoices.getAll().dataOrEmpty().filterNot { it.isDeleted }
            val taxInvoices = TradeFitApi.taxInvoices.getAll().dataOrEmpty().filterNot { it.isDeleted }
            _uiState.value = DocumentWorkflowUiState(
                customers = customers,
                fabrics = fabrics,
                rolls = rolls,
                challans = challans.sortedByDescending { it.createdAt },
                proformas = proformas.sortedByDescending { it.createdAt },
                taxInvoices = taxInvoices.sortedByDescending { it.createdAt },
                loading = false
            )
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null, error = null) }

    fun saveChallan(form: DocumentFormState) {
        viewModelScope.launch {
            if (!validate(form)) return@launch
            setSaving(true)
            val customer = _uiState.value.customers.first { it.id == form.customerId }
            val fabric = _uiState.value.fabrics.firstOrNull { it.code == form.fabricCode }
            val roll = _uiState.value.rolls.firstOrNull { it.id == form.stockEntryId }
            val id = form.id.ifBlank { nextNumber("DC", _uiState.value.challans.map { it.id }) }
            val itemId = "$id-1"
            val item = BackendDeliveryChallanItem(
                id = itemId,
                challanId = id,
                fabricName = fabric?.name ?: roll?.fabricName.orEmpty(),
                colour = fabric?.colour ?: roll?.colour.orEmpty(),
                gsm = fabric?.gsm ?: roll?.gsm ?: 0,
                width = fabric?.width ?: roll?.width ?: 0.0,
                lotNumber = roll?.lotNumber.orEmpty(),
                rollNumbers = listOfNotNull(roll?.rollNumber).filter { it.isNotBlank() },
                numberOfRolls = roll?.numberOfRolls ?: 1,
                weightKg = form.quantity,
                ratePerKg = form.rate,
                amount = form.subtotal,
                updatedAt = System.currentTimeMillis()
            )
            val challan = BackendDeliveryChallan(
                id = id,
                date = today(),
                deliveryDate = form.dispatchDate,
                customerId = customer.id,
                companyName = customer.companyName,
                gstNumber = customer.gstin,
                mobile = customer.mobile,
                billingAddress = customer.billingAddress,
                shippingAddress = customer.shippingAddress,
                transportName = form.transportName.ifBlank { customer.transportName },
                vehicleNumber = form.vehicleNumber,
                driverName = form.driverName,
                driverMobile = form.driverMobile,
                lrNumber = form.lrNumber,
                dispatchFrom = "Main Warehouse",
                dispatchTo = customer.shippingAddress,
                totalRolls = item.numberOfRolls,
                totalWeight = form.quantity,
                subTotal = form.subtotal,
                discountValue = form.discount,
                discountAmount = form.discount,
                freightCharges = form.freight,
                packingCharges = form.packing,
                otherCharges = form.other,
                taxableAmount = form.taxable,
                grandTotal = form.grandTotal,
                remarks = form.remarks,
                termsAndConditions = "Goods dispatched subject to TradeFit ERP terms.",
                preparedBy = currentUserLabel(),
                status = if (form.id.isBlank()) "Draft" else existingChallanStatus(form.id),
                updatedAt = System.currentTimeMillis()
            )
            val saved = TradeFitApi.deliveryChallans.save(id, challan)
            if (saved is ResourceState.Success) {
                TradeFitApi.deliveryChallanItems.save(itemId, item)
                setMessage("Delivery Challan $id saved.")
                refresh()
            } else {
                setError(saved.errorMessage("Unable to save Delivery Challan."))
            }
            setSaving(false)
        }
    }

    fun deleteChallan(id: String) = updateStatusOrDelete(id, true)
    fun approveChallan(id: String) = updateChallanStatus(id, "Approved")
    fun dispatchChallan(id: String) {
        viewModelScope.launch {
            updateChallanStatus(id, "Dispatched", refreshAfter = false)
            TradeFitApi.stockTransactions.save(
                "STX-${System.currentTimeMillis()}",
                BackendStockTransaction(
                    id = "STX-${System.currentTimeMillis()}",
                    transactionDate = today(),
                    referenceType = "DC",
                    referenceId = id,
                    transactionType = "OUT",
                    status = "Completed"
                )
            )
            setMessage("Delivery Challan $id dispatched.")
            refresh()
        }
    }

    fun convertChallanToProforma(challan: BackendDeliveryChallan) {
        viewModelScope.launch {
            setSaving(true)
            val id = nextNumber("PI", _uiState.value.proformas.map { it.id })
            val gstAmount = money(challan.taxableAmount * 0.18)
            val proforma = BackendProformaInvoice(
                id = id,
                date = today(),
                validityDate = todayPlusDays(15),
                referenceNumber = challan.id,
                customerId = challan.customerId,
                companyName = challan.companyName,
                gstNumber = challan.gstNumber,
                mobile = challan.mobile,
                billingAddress = challan.billingAddress,
                shippingAddress = challan.shippingAddress,
                transportName = challan.transportName,
                totalRolls = challan.totalRolls,
                totalWeight = challan.totalWeight,
                subTotal = challan.subTotal,
                discountAmount = challan.discountAmount,
                freightCharges = challan.freightCharges,
                packingCharges = challan.packingCharges,
                otherCharges = challan.otherCharges,
                taxableAmount = challan.taxableAmount,
                cgstRate = 9.0,
                cgstAmount = money(gstAmount / 2),
                sgstRate = 9.0,
                sgstAmount = money(gstAmount / 2),
                grandTotal = money(challan.taxableAmount + gstAmount),
                termsAndConditions = challan.termsAndConditions,
                preparedBy = currentUserLabel(),
                remarks = "Converted from Delivery Challan ${challan.id}",
                status = "Draft"
            )
            when (val result = TradeFitApi.proformaInvoices.save(id, proforma)) {
                is ResourceState.Success -> {
                    TradeFitApi.deliveryChallans.updateFields(challan.id, mapOf("status" to "Approved", "updatedAt" to System.currentTimeMillis()))
                    setMessage("Proforma Invoice $id created from ${challan.id}.")
                    refresh()
                }
                is ResourceState.Error -> setError(result.message)
                else -> Unit
            }
            setSaving(false)
        }
    }

    fun saveProforma(form: DocumentFormState) {
        viewModelScope.launch {
            if (!validate(form)) return@launch
            setSaving(true)
            val customer = _uiState.value.customers.first { it.id == form.customerId }
            val id = form.id.ifBlank { nextNumber("PI", _uiState.value.proformas.map { it.id }) }
            val gstAmount = form.gstAmount
            val proforma = BackendProformaInvoice(
                id = id,
                date = today(),
                validityDate = todayPlusDays(15),
                referenceNumber = "DIRECT-$id",
                customerId = customer.id,
                companyName = customer.companyName,
                gstNumber = customer.gstin,
                mobile = customer.mobile,
                billingAddress = customer.billingAddress,
                shippingAddress = customer.shippingAddress,
                transportName = form.transportName.ifBlank { customer.transportName },
                totalRolls = 1,
                totalWeight = form.quantity,
                subTotal = form.subtotal,
                discountAmount = form.discount,
                freightCharges = form.freight,
                packingCharges = form.packing,
                otherCharges = form.other,
                taxableAmount = form.taxable,
                cgstRate = form.gst / 2,
                cgstAmount = money(gstAmount / 2),
                sgstRate = form.gst / 2,
                sgstAmount = money(gstAmount / 2),
                grandTotal = form.grandTotal,
                termsAndConditions = "Quotation valid for 15 days unless revised.",
                preparedBy = currentUserLabel(),
                remarks = form.remarks,
                status = if (form.id.isBlank()) "Draft" else existingProformaStatus(form.id),
                updatedAt = System.currentTimeMillis()
            )
            when (val result = TradeFitApi.proformaInvoices.save(id, proforma)) {
                is ResourceState.Success -> {
                    TradeFitApi.proformaItems.save(
                        "$id-1",
                        BackendProformaInvoiceItem(
                            id = "$id-1",
                            proformaId = id,
                            fabricName = _uiState.value.fabrics.firstOrNull { it.code == form.fabricCode }?.name.orEmpty(),
                            rollQty = 1,
                            weightKg = form.quantity,
                            ratePerKg = form.rate,
                            discountPercent = 0.0,
                            amount = form.subtotal
                        )
                    )
                    setMessage("Proforma Invoice $id saved.")
                    refresh()
                }
                is ResourceState.Error -> setError(result.message)
                else -> Unit
            }
            setSaving(false)
        }
    }

    fun deleteProforma(id: String) = viewModelScope.launch {
        setSaving(true)
        when (val result = TradeFitApi.proformaInvoices.delete(id)) {
            is ResourceState.Success -> {
                setMessage("Proforma Invoice $id deleted.")
                refresh()
            }
            is ResourceState.Error -> setError(result.message)
            else -> Unit
        }
        setSaving(false)
    }

    fun convertProformaToTaxInvoice(proforma: BackendProformaInvoice) {
        viewModelScope.launch {
            setSaving(true)
            val id = nextNumber("TI", _uiState.value.taxInvoices.map { it.id.ifBlank { it.invoiceNumber } })
            val taxInvoice = BackendTaxInvoice(
                id = id,
                invoiceNumber = id,
                date = today(),
                proformaId = proforma.id,
                customerId = proforma.customerId,
                companyName = proforma.companyName,
                gstNumber = proforma.gstNumber,
                billingAddress = proforma.billingAddress,
                shippingAddress = proforma.shippingAddress,
                transportName = proforma.transportName,
                totalRolls = proforma.totalRolls,
                totalWeight = proforma.totalWeight,
                subTotal = proforma.subTotal,
                discountAmount = proforma.discountAmount,
                freightCharges = proforma.freightCharges,
                packingCharges = proforma.packingCharges,
                otherCharges = proforma.otherCharges,
                taxableAmount = proforma.taxableAmount,
                cgstAmount = proforma.cgstAmount,
                sgstAmount = proforma.sgstAmount,
                igstAmount = proforma.igstAmount,
                grandTotal = proforma.grandTotal,
                balanceDue = proforma.grandTotal,
                termsAndConditions = proforma.termsAndConditions,
                preparedBy = currentUserLabel(),
                remarks = "Converted from Proforma ${proforma.id}",
                status = "Unpaid"
            )
            when (val result = TradeFitApi.taxInvoices.save(id, taxInvoice)) {
                is ResourceState.Success -> {
                    TradeFitApi.proformaInvoices.updateFields(proforma.id, mapOf("status" to "Converted", "updatedAt" to System.currentTimeMillis()))
                    setMessage("Tax Invoice $id created from ${proforma.id}.")
                    refresh()
                }
                is ResourceState.Error -> setError(result.message)
                else -> Unit
            }
            setSaving(false)
        }
    }

    fun saveTaxInvoice(form: DocumentFormState) {
        viewModelScope.launch {
            if (!validate(form)) return@launch
            setSaving(true)
            val customer = _uiState.value.customers.first { it.id == form.customerId }
            val id = form.id.ifBlank { nextNumber("TI", _uiState.value.taxInvoices.map { it.id }) }
            val paid = form.paymentReceived.toDoubleOrNull() ?: 0.0
            val due = (form.grandTotal - paid).coerceAtLeast(0.0)
            val status = if (due <= 0.0) "Paid" else if (paid > 0.0) "Partially Paid" else "Unpaid"
            val invoice = BackendTaxInvoice(
                id = id,
                invoiceNumber = id,
                date = today(),
                customerId = customer.id,
                companyName = customer.companyName,
                gstNumber = customer.gstin,
                billingAddress = customer.billingAddress,
                shippingAddress = customer.shippingAddress,
                transportName = form.transportName.ifBlank { customer.transportName },
                totalRolls = 1,
                totalWeight = form.quantity,
                subTotal = form.subtotal,
                discountAmount = form.discount,
                freightCharges = form.freight,
                packingCharges = form.packing,
                otherCharges = form.other,
                taxableAmount = form.taxable,
                cgstAmount = money(form.gstAmount / 2),
                sgstAmount = money(form.gstAmount / 2),
                grandTotal = form.grandTotal,
                amountPaid = paid,
                balanceDue = due,
                termsAndConditions = "Tax invoice payable as per agreed credit terms.",
                preparedBy = currentUserLabel(),
                remarks = form.remarks,
                status = status,
                updatedAt = System.currentTimeMillis()
            )
            when (val result = TradeFitApi.taxInvoices.save(id, invoice)) {
                is ResourceState.Success -> {
                    TradeFitApi.taxInvoiceItems.save(
                        "$id-1",
                        BackendTaxInvoiceItem(
                            id = "$id-1",
                            invoiceId = id,
                            fabricName = _uiState.value.fabrics.firstOrNull { it.code == form.fabricCode }?.name.orEmpty(),
                            hsnCode = _uiState.value.fabrics.firstOrNull { it.code == form.fabricCode }?.hsnCode.orEmpty(),
                            rollQty = 1,
                            weightKg = form.quantity,
                            ratePerKg = form.rate,
                            amount = form.subtotal,
                            cgstRate = form.gst / 2,
                            cgstAmount = money(form.gstAmount / 2),
                            sgstRate = form.gst / 2,
                            sgstAmount = money(form.gstAmount / 2),
                            totalAmount = form.grandTotal
                        )
                    )
                    setMessage("Tax Invoice $id saved.")
                    refresh()
                }
                is ResourceState.Error -> setError(result.message)
                else -> Unit
            }
            setSaving(false)
        }
    }

    fun deleteTaxInvoice(id: String) = viewModelScope.launch {
        setSaving(true)
        when (val result = TradeFitApi.taxInvoices.delete(id)) {
            is ResourceState.Success -> {
                setMessage("Tax Invoice $id deleted.")
                refresh()
            }
            is ResourceState.Error -> setError(result.message)
            else -> Unit
        }
        setSaving(false)
    }

    private fun updateStatusOrDelete(id: String, delete: Boolean) = viewModelScope.launch {
        setSaving(true)
        val result = if (delete) TradeFitApi.deliveryChallans.delete(id) else TradeFitApi.deliveryChallans.updateFields(id, emptyMap())
        if (result is ResourceState.Success) {
            setMessage("Delivery Challan $id deleted.")
            refresh()
        } else {
            setError(result.errorMessage("Unable to delete Delivery Challan."))
        }
        setSaving(false)
    }

    private fun updateChallanStatus(id: String, status: String, refreshAfter: Boolean = true) = viewModelScope.launch {
        setSaving(true)
        val result = TradeFitApi.deliveryChallans.updateFields(id, mapOf("status" to status, "updatedAt" to System.currentTimeMillis()))
        if (result is ResourceState.Success) {
            setMessage("Delivery Challan $id marked $status.")
            if (refreshAfter) refresh()
        } else {
            setError(result.errorMessage("Unable to update Delivery Challan."))
        }
        setSaving(false)
    }

    private fun validate(form: DocumentFormState): Boolean {
        val state = _uiState.value
        return when {
            form.customerId.isBlank() -> {
                setError("Select a customer before saving.")
                false
            }
            form.quantity <= 0.0 -> {
                setError("Enter a valid fabric quantity.")
                false
            }
            form.rate <= 0.0 -> {
                setError("Enter a valid rate.")
                false
            }
            state.customers.none { it.id == form.customerId } -> {
                setError("Selected customer is no longer available.")
                false
            }
            else -> true
        }
    }

    private fun existingChallanStatus(id: String) = _uiState.value.challans.firstOrNull { it.id == id }?.status ?: "Draft"
    private fun existingProformaStatus(id: String) = _uiState.value.proformas.firstOrNull { it.id == id }?.status ?: "Draft"
    private fun setSaving(value: Boolean) = _uiState.update { it.copy(saving = value) }
    private fun setMessage(message: String) = _uiState.update { it.copy(message = message, error = null) }
    private fun setError(message: String) = _uiState.update { it.copy(error = message, saving = false) }
    private fun currentUserLabel() = TradeFitApi.activeCompanyId.ifBlank { "TradeFit ERP" }
}

private fun <T> ResourceState<List<T>>.dataOrEmpty(): List<T> = when (this) {
    is ResourceState.Success -> data
    else -> emptyList()
}

private fun ResourceState<*>.errorMessage(fallback: String): String = when (this) {
    is ResourceState.Error -> message
    else -> fallback
}

private fun nextNumber(prefix: String, existing: List<String>): String {
    val max = existing.mapNotNull { it.substringAfter("$prefix-", "").toIntOrNull() }.maxOrNull() ?: 0
    return "$prefix-${(max + 1).toString().padStart(6, '0')}"
}

private fun money(value: Double): Double = round(value * 100.0) / 100.0

private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

private fun todayPlusDays(days: Int): String {
    val millis = System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
}
