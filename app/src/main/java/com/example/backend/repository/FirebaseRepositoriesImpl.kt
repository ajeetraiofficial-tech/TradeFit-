package com.example.backend.repository

import com.example.backend.api.TradeFitApi
import com.example.backend.model.*
import com.example.backend.service.FirestoreCrudService
import com.example.backend.service.ResourceState
import com.example.config.FirebaseConfig

/**
 * Helper to extract parent ID and child ID from a compound identifier (e.g. parentId_childId).
 * Essential for subcollection lookups where parent ID is needed for path resolution.
 */
private fun splitId(id: String): Pair<String, String> {
    val index = id.indexOf('_')
    return if (index != -1) {
        id.substring(0, index) to id.substring(index + 1)
    } else {
        "" to id
    }
}

/**
 * 1. Concrete Company Repository Implementation (Root collection)
 */
class FirebaseCompanyRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : CompanyRepository {
    override suspend fun save(id: String, entity: Company) = 
        crud.create(FirebaseConfig.getCompaniesPath(), id, entity)
        
    override suspend fun getById(id: String) = 
        crud.read(FirebaseConfig.getCompaniesPath(), id, Company::class.java)
        
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = 
        crud.update(FirebaseConfig.getCompaniesPath(), id, fields)
        
    override suspend fun delete(id: String) = 
        crud.delete(FirebaseConfig.getCompaniesPath(), id)
        
    override suspend fun getAll() = 
        crud.getAll(FirebaseConfig.getCompaniesPath(), Company::class.java)
}

/**
 * 2. Concrete User Repository Implementation (Scoped subcollection)
 */
class FirebaseUserRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : UserRepository {
    private val path get() = FirebaseConfig.getUsersPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: User) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, User::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, User::class.java)
}

/**
 * 3. Concrete Customer Repository Implementation (Scoped subcollection)
 */
class FirebaseCustomerRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : CustomerRepository {
    private val path get() = FirebaseConfig.getCustomersPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: Customer) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, Customer::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, Customer::class.java)

    override suspend fun getActiveCustomers(): ResourceState<List<Customer>> =
        crud.getFiltered(path, "status", "Active", Customer::class.java)

    override suspend fun updateOutstandingAmount(id: String, newOutstanding: Double): ResourceState<String> =
        crud.update(path, id, mapOf("outstandingAmount" to newOutstanding, "updatedAt" to System.currentTimeMillis()))
}

/**
 * 4. Concrete Fabric Repository Implementation (Scoped subcollection)
 */
class FirebaseFabricRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : FabricRepository {
    private val path get() = FirebaseConfig.getFabricsPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: Fabric) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, Fabric::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, Fabric::class.java)

    override suspend fun getActiveFabrics(): ResourceState<List<Fabric>> =
        crud.getFiltered(path, "status", "Active", Fabric::class.java)

    override suspend fun updateStockCount(code: String, deltaStock: Double): ResourceState<String> =
        crud.update(path, code, mapOf("currentStock" to deltaStock, "updatedAt" to System.currentTimeMillis()))
}

/**
 * 5. Concrete Stock Repository Implementation (Scoped subcollection)
 */
class FirebaseStockRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : StockRepository {
    private val path get() = FirebaseConfig.getStockPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: StockEntry) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, StockEntry::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, StockEntry::class.java)

    override suspend fun getByFabricCode(fabricCode: String): ResourceState<List<StockEntry>> =
        crud.getFiltered(path, "fabricCode", fabricCode, StockEntry::class.java)

    override suspend fun getAvailableStock(): ResourceState<List<StockEntry>> =
        crud.getFiltered(path, "status", "Available", StockEntry::class.java)

    override suspend fun updateStockStatus(id: String, status: String): ResourceState<String> =
        crud.update(path, id, mapOf("status" to status))
}

/**
 * 6. Concrete Stock Transaction Repository Implementation (Scoped subcollection)
 */
class FirebaseStockTransactionRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : StockTransactionRepository {
    private val path get() = FirebaseConfig.getStockTransactionsPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: StockTransaction) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, StockTransaction::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, StockTransaction::class.java)

    override suspend fun getByStockEntryId(stockEntryId: String): ResourceState<List<StockTransaction>> =
        crud.getFiltered(path, "stockEntryId", stockEntryId, StockTransaction::class.java)

    override suspend fun getByFabricCode(fabricCode: String): ResourceState<List<StockTransaction>> =
        crud.getFiltered(path, "fabricCode", fabricCode, StockTransaction::class.java)
}

/**
 * 7. Concrete Delivery Challan Repository Implementation (Scoped subcollection)
 */
class FirebaseDeliveryChallanRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : DeliveryChallanRepository {
    private val path get() = FirebaseConfig.getDeliveryChallansPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: DeliveryChallan) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, DeliveryChallan::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, DeliveryChallan::class.java)

    override suspend fun getByCustomerId(customerId: String): ResourceState<List<DeliveryChallan>> =
        crud.getFiltered(path, "customerId", customerId, DeliveryChallan::class.java)

    override suspend fun getChallansByStatus(status: String): ResourceState<List<DeliveryChallan>> =
        crud.getFiltered(path, "status", status, DeliveryChallan::class.java)
}

/**
 * 8. Concrete Delivery Challan Item Repository Implementation (Nested subcollection under deliveryChallans)
 */
class FirebaseDeliveryChallanItemRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : DeliveryChallanItemRepository {
    
    private fun getPath(challanId: String) = 
        FirebaseConfig.getDeliveryChallanItemsPath(TradeFitApi.activeCompanyId, challanId)

    override suspend fun save(id: String, entity: DeliveryChallanItem) = 
        crud.create(getPath(entity.challanId), id, entity)

    override suspend fun getById(id: String): ResourceState<DeliveryChallanItem> {
        val (challanId, itemId) = splitId(id)
        return if (challanId.isNotEmpty()) {
            crud.read(getPath(challanId), itemId, DeliveryChallanItem::class.java)
        } else {
            ResourceState.Error(IllegalArgumentException("Challan Item ID must be structured as 'challanId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun updateFields(id: String, fields: Map<String, Any>): ResourceState<String> {
        val (challanId, itemId) = splitId(id)
        return if (challanId.isNotEmpty()) {
            crud.update(getPath(challanId), itemId, fields)
        } else {
            ResourceState.Error(IllegalArgumentException("Challan Item ID must be structured as 'challanId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun delete(id: String): ResourceState<String> {
        val (challanId, itemId) = splitId(id)
        return if (challanId.isNotEmpty()) {
            crud.delete(getPath(challanId), itemId)
        } else {
            ResourceState.Error(IllegalArgumentException("Challan Item ID must be structured as 'challanId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun getAll(): ResourceState<List<DeliveryChallanItem>> =
        ResourceState.Error(UnsupportedOperationException("Fetch items inside specific challan using getByChallanId"), "Operation not supported.")

    override suspend fun getByChallanId(challanId: String): ResourceState<List<DeliveryChallanItem>> =
        crud.getAll(getPath(challanId), DeliveryChallanItem::class.java)
}

/**
 * 9. Concrete Proforma Invoice Repository Implementation (Scoped subcollection)
 */
class FirebaseProformaInvoiceRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : ProformaInvoiceRepository {
    private val path get() = FirebaseConfig.getProformaInvoicesPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: ProformaInvoice) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, ProformaInvoice::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, ProformaInvoice::class.java)

    override suspend fun getByCustomerId(customerId: String): ResourceState<List<ProformaInvoice>> =
        crud.getFiltered(path, "customerId", customerId, ProformaInvoice::class.java)

    override suspend fun getInvoicesByStatus(status: String): ResourceState<List<ProformaInvoice>> =
        crud.getFiltered(path, "status", status, ProformaInvoice::class.java)
}

/**
 * 10. Concrete Proforma Invoice Item Repository Implementation (Nested subcollection under proformaInvoices)
 */
class FirebaseProformaInvoiceItemRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : ProformaInvoiceItemRepository {

    private fun getPath(proformaId: String) = 
        FirebaseConfig.getProformaItemsPath(TradeFitApi.activeCompanyId, proformaId)

    override suspend fun save(id: String, entity: ProformaInvoiceItem) = 
        crud.create(getPath(entity.proformaId), id, entity)

    override suspend fun getById(id: String): ResourceState<ProformaInvoiceItem> {
        val (proformaId, itemId) = splitId(id)
        return if (proformaId.isNotEmpty()) {
            crud.read(getPath(proformaId), itemId, ProformaInvoiceItem::class.java)
        } else {
            ResourceState.Error(IllegalArgumentException("Proforma Item ID must be structured as 'proformaId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun updateFields(id: String, fields: Map<String, Any>): ResourceState<String> {
        val (proformaId, itemId) = splitId(id)
        return if (proformaId.isNotEmpty()) {
            crud.update(getPath(proformaId), itemId, fields)
        } else {
            ResourceState.Error(IllegalArgumentException("Proforma Item ID must be structured as 'proformaId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun delete(id: String): ResourceState<String> {
        val (proformaId, itemId) = splitId(id)
        return if (proformaId.isNotEmpty()) {
            crud.delete(getPath(proformaId), itemId)
        } else {
            ResourceState.Error(IllegalArgumentException("Proforma Item ID must be structured as 'proformaId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun getAll(): ResourceState<List<ProformaInvoiceItem>> =
        ResourceState.Error(UnsupportedOperationException("Fetch items inside specific proforma using getByProformaId"), "Operation not supported.")

    override suspend fun getByProformaId(proformaId: String): ResourceState<List<ProformaInvoiceItem>> =
        crud.getAll(getPath(proformaId), ProformaInvoiceItem::class.java)
}

/**
 * 11. Concrete Tax Invoice Repository Implementation (Scoped subcollection)
 */
class FirebaseTaxInvoiceRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : TaxInvoiceRepository {
    private val path get() = FirebaseConfig.getTaxInvoicesPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: TaxInvoice) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, TaxInvoice::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, TaxInvoice::class.java)

    override suspend fun getByCustomerId(customerId: String): ResourceState<List<TaxInvoice>> =
        crud.getFiltered(path, "customerId", customerId, TaxInvoice::class.java)

    override suspend fun getUnpaidInvoices(): ResourceState<List<TaxInvoice>> =
        crud.getFiltered(path, "status", "Unpaid", TaxInvoice::class.java)

    override suspend fun updatePaymentStatus(id: String, status: String, amountPaidDelta: Double): ResourceState<String> {
        return try {
            val invoiceResult = getById(id)
            if (invoiceResult is ResourceState.Success) {
                val invoice = invoiceResult.data
                val newPaid = invoice.amountPaid + amountPaidDelta
                val newDue = (invoice.grandTotal - newPaid).coerceAtLeast(0.0)
                val finalStatus = if (newDue <= 0.0) "Paid" else if (newPaid > 0) "Partially Paid" else "Unpaid"
                
                val updates = mapOf(
                    "amountPaid" to newPaid,
                    "balanceDue" to newDue,
                    "status" to finalStatus,
                    "updatedAt" to System.currentTimeMillis()
                )
                crud.update(path, id, updates)
            } else {
                ResourceState.Error(Exception("Invoice not found"), "Could not fetch tax invoice with ID $id to update payment status.")
            }
        } catch (e: Exception) {
            ResourceState.Error(e, "Failed to update tax invoice payment status: ${e.localizedMessage}")
        }
    }
}

/**
 * 12. Concrete Tax Invoice Item Repository Implementation (Nested subcollection under taxInvoices)
 */
class FirebaseTaxInvoiceItemRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : TaxInvoiceItemRepository {

    private fun getPath(invoiceId: String) = 
        FirebaseConfig.getTaxInvoiceItemsPath(TradeFitApi.activeCompanyId, invoiceId)

    override suspend fun save(id: String, entity: TaxInvoiceItem) = 
        crud.create(getPath(entity.invoiceId), id, entity)

    override suspend fun getById(id: String): ResourceState<TaxInvoiceItem> {
        val (invoiceId, itemId) = splitId(id)
        return if (invoiceId.isNotEmpty()) {
            crud.read(getPath(invoiceId), itemId, TaxInvoiceItem::class.java)
        } else {
            ResourceState.Error(IllegalArgumentException("Invoice Item ID must be structured as 'invoiceId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun updateFields(id: String, fields: Map<String, Any>): ResourceState<String> {
        val (invoiceId, itemId) = splitId(id)
        return if (invoiceId.isNotEmpty()) {
            crud.update(getPath(invoiceId), itemId, fields)
        } else {
            ResourceState.Error(IllegalArgumentException("Invoice Item ID must be structured as 'invoiceId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun delete(id: String): ResourceState<String> {
        val (invoiceId, itemId) = splitId(id)
        return if (invoiceId.isNotEmpty()) {
            crud.delete(getPath(invoiceId), itemId)
        } else {
            ResourceState.Error(IllegalArgumentException("Invoice Item ID must be structured as 'invoiceId_itemId'"), "Missing parent ID reference.")
        }
    }

    override suspend fun getAll(): ResourceState<List<TaxInvoiceItem>> =
        ResourceState.Error(UnsupportedOperationException("Fetch items inside specific invoice using getByInvoiceId"), "Operation not supported.")

    override suspend fun getByInvoiceId(invoiceId: String): ResourceState<List<TaxInvoiceItem>> =
        crud.getAll(getPath(invoiceId), TaxInvoiceItem::class.java)
}

/**
 * 13. Concrete Payment Repository Implementation (Scoped subcollection)
 */
class FirebasePaymentRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : PaymentRepository {
    private val path get() = FirebaseConfig.getPaymentsPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: Payment) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, Payment::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, Payment::class.java)

    override suspend fun getByCustomerId(customerId: String): ResourceState<List<Payment>> =
        crud.getFiltered(path, "customerId", customerId, Payment::class.java)

    override suspend fun getByInvoiceId(invoiceId: String): ResourceState<List<Payment>> =
        crud.getFiltered(path, "invoiceId", invoiceId, Payment::class.java)
}

/**
 * 14. Concrete Ledger Repository Implementation (Scoped subcollection)
 */
class FirebaseLedgerRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : LedgerRepository {
    private val path get() = FirebaseConfig.getLedgersPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: LedgerEntry) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, LedgerEntry::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, LedgerEntry::class.java)

    override suspend fun getLedgerForCustomer(customerId: String): ResourceState<List<LedgerEntry>> =
        crud.getFiltered(path, "customerId", customerId, LedgerEntry::class.java)
}

/**
 * 15. Concrete Report Repository Implementation (Scoped subcollection)
 */
class FirebaseReportRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : ReportRepository {
    private val path get() = FirebaseConfig.getReportsPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: Report) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, Report::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, Report::class.java)

    override suspend fun getByType(type: String): ResourceState<List<Report>> =
        crud.getFiltered(path, "type", type, Report::class.java)
}

/**
 * 16. Concrete Setting Repository Implementation (Scoped subcollection)
 */
class FirebaseSettingRepository(
    private val crud: FirestoreCrudService = FirestoreCrudService()
) : SettingRepository {
    private val path get() = FirebaseConfig.getSettingsPath(TradeFitApi.activeCompanyId)

    override suspend fun save(id: String, entity: Setting) = crud.create(path, id, entity)
    override suspend fun getById(id: String) = crud.read(path, id, Setting::class.java)
    override suspend fun updateFields(id: String, fields: Map<String, Any>) = crud.update(path, id, fields)
    override suspend fun delete(id: String) = crud.delete(path, id)
    override suspend fun getAll() = crud.getAll(path, Setting::class.java)

    override suspend fun getByGroup(group: String): ResourceState<List<Setting>> =
        crud.getFiltered(path, "group", group, Setting::class.java)
}
