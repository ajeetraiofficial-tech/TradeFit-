package com.example.backend.repository

import com.example.backend.model.*
import com.example.backend.service.ResourceState

/**
 * Base Repository interface for standard CRUD operations.
 */
interface IRepository<T, ID> {
    suspend fun save(id: ID, entity: T): ResourceState<ID>
    suspend fun getById(id: ID): ResourceState<T>
    suspend fun updateFields(id: ID, fields: Map<String, Any>): ResourceState<ID>
    suspend fun delete(id: ID): ResourceState<ID>
    suspend fun getAll(): ResourceState<List<T>>
}

/**
 * Repository interface for companies collection
 */
interface CompanyRepository : IRepository<Company, String>

/**
 * Repository interface for users collection
 */
interface UserRepository : IRepository<User, String>

/**
 * Repository interface for customers collection
 */
interface CustomerRepository : IRepository<Customer, String> {
    suspend fun getActiveCustomers(): ResourceState<List<Customer>>
    suspend fun updateOutstandingAmount(id: String, newOutstanding: Double): ResourceState<String>
}

/**
 * Repository interface for fabrics collection
 */
interface FabricRepository : IRepository<Fabric, String> {
    suspend fun getActiveFabrics(): ResourceState<List<Fabric>>
    suspend fun updateStockCount(code: String, deltaStock: Double): ResourceState<String>
}

/**
 * Repository interface for stock collection
 */
interface StockRepository : IRepository<StockEntry, String> {
    suspend fun getByFabricCode(fabricCode: String): ResourceState<List<StockEntry>>
    suspend fun getAvailableStock(): ResourceState<List<StockEntry>>
    suspend fun updateStockStatus(id: String, status: String): ResourceState<String>
}

/**
 * Repository interface for stockTransactions collection
 */
interface StockTransactionRepository : IRepository<StockTransaction, String> {
    suspend fun getByStockEntryId(stockEntryId: String): ResourceState<List<StockTransaction>>
    suspend fun getByFabricCode(fabricCode: String): ResourceState<List<StockTransaction>>
}

/**
 * Repository interface for deliveryChallans collection
 */
interface DeliveryChallanRepository : IRepository<DeliveryChallan, String> {
    suspend fun getByCustomerId(customerId: String): ResourceState<List<DeliveryChallan>>
    suspend fun getChallansByStatus(status: String): ResourceState<List<DeliveryChallan>>
}

/**
 * Repository interface for deliveryChallanItems collection
 */
interface DeliveryChallanItemRepository : IRepository<DeliveryChallanItem, String> {
    suspend fun getByChallanId(challanId: String): ResourceState<List<DeliveryChallanItem>>
}

/**
 * Repository interface for proformaInvoices collection
 */
interface ProformaInvoiceRepository : IRepository<ProformaInvoice, String> {
    suspend fun getByCustomerId(customerId: String): ResourceState<List<ProformaInvoice>>
    suspend fun getInvoicesByStatus(status: String): ResourceState<List<ProformaInvoice>>
}

/**
 * Repository interface for proformaItems collection
 */
interface ProformaInvoiceItemRepository : IRepository<ProformaInvoiceItem, String> {
    suspend fun getByProformaId(proformaId: String): ResourceState<List<ProformaInvoiceItem>>
}

/**
 * Repository interface for taxInvoices collection
 */
interface TaxInvoiceRepository : IRepository<TaxInvoice, String> {
    suspend fun getByCustomerId(customerId: String): ResourceState<List<TaxInvoice>>
    suspend fun getUnpaidInvoices(): ResourceState<List<TaxInvoice>>
    suspend fun updatePaymentStatus(id: String, status: String, amountPaidDelta: Double): ResourceState<String>
}

/**
 * Repository interface for taxInvoiceItems collection
 */
interface TaxInvoiceItemRepository : IRepository<TaxInvoiceItem, String> {
    suspend fun getByInvoiceId(invoiceId: String): ResourceState<List<TaxInvoiceItem>>
}

/**
 * Repository interface for payments collection
 */
interface PaymentRepository : IRepository<Payment, String> {
    suspend fun getByCustomerId(customerId: String): ResourceState<List<Payment>>
    suspend fun getByInvoiceId(invoiceId: String): ResourceState<List<Payment>>
}

/**
 * Repository interface for ledgers collection
 */
interface LedgerRepository : IRepository<LedgerEntry, String> {
    suspend fun getLedgerForCustomer(customerId: String): ResourceState<List<LedgerEntry>>
}

/**
 * Repository interface for reports collection
 */
interface ReportRepository : IRepository<Report, String> {
    suspend fun getByType(type: String): ResourceState<List<Report>>
}

/**
 * Repository interface for settings collection
 */
interface SettingRepository : IRepository<Setting, String> {
    suspend fun getByGroup(group: String): ResourceState<List<Setting>>
}
