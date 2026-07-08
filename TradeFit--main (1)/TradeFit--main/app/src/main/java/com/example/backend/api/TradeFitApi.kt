package com.example.backend.api

import android.content.Context
import com.example.backend.service.*
import com.example.backend.repository.*

/**
 * TradeFitApi - Unified API Facade Layer for TradeFit ERP.
 * Centrally coordinates and provides easy discoverable access to:
 * 1. Firebase Authentication Services
 * 2. Firebase Storage Services
 * 3. 16 Cloud Firestore Collection Repositories (CRUD & Queries)
 */
object TradeFitApi {
    
    /**
     * Active company ID workspace context for multi-tenant data routing.
     * Set dynamically upon authentication/login to route all collections to the correct tenant.
     */
    @Volatile
    var activeCompanyId: String = "demo_company"
    
    /**
     * Centralized Authentication & User Session Service
     */
    val auth: FirebaseAuthService by lazy { FirebaseAuthService() }
    
    /**
     * Centralized File & Document Storage Service
     */
    val storage: FirebaseStorageService by lazy { FirebaseStorageService() }
    
    /**
     * 1. Companies Collection Repository
     */
    val companies: CompanyRepository by lazy { FirebaseCompanyRepository() }
    
    /**
     * 2. Users Collection Repository
     */
    val users: UserRepository by lazy { FirebaseUserRepository() }
    
    /**
     * 3. Customers Collection Repository
     */
    val customers: CustomerRepository by lazy { FirebaseCustomerRepository() }
    
    /**
     * 4. Fabrics Collection Repository
     */
    val fabrics: FabricRepository by lazy { FirebaseFabricRepository() }
    
    /**
     * 5. Stock / Inventory Collection Repository
     */
    val stock: StockRepository by lazy { FirebaseStockRepository() }
    
    /**
     * 6. Stock Transactions Collection Repository
     */
    val stockTransactions: StockTransactionRepository by lazy { FirebaseStockTransactionRepository() }

    /**
     * 7. Delivery Challans Collection Repository
     */
    val deliveryChallans: DeliveryChallanRepository by lazy { FirebaseDeliveryChallanRepository() }
    
    /**
     * 8. Delivery Challan Items Collection Repository
     */
    val deliveryChallanItems: DeliveryChallanItemRepository by lazy { FirebaseDeliveryChallanItemRepository() }

    /**
     * 9. Proforma Invoices Collection Repository
     */
    val proformaInvoices: ProformaInvoiceRepository by lazy { FirebaseProformaInvoiceRepository() }
    
    /**
     * 10. Proforma Invoice Items Collection Repository
     */
    val proformaItems: ProformaInvoiceItemRepository by lazy { FirebaseProformaInvoiceItemRepository() }

    /**
     * 11. Tax Invoices Collection Repository
     */
    val taxInvoices: TaxInvoiceRepository by lazy { FirebaseTaxInvoiceRepository() }
    
    /**
     * 12. Tax Invoice Items Collection Repository
     */
    val taxInvoiceItems: TaxInvoiceItemRepository by lazy { FirebaseTaxInvoiceItemRepository() }

    /**
     * 13. Payments Collection Repository
     */
    val payments: PaymentRepository by lazy { FirebasePaymentRepository() }
    
    /**
     * 14. Ledgers Collection Repository
     */
    val ledgers: LedgerRepository by lazy { FirebaseLedgerRepository() }
    
    /**
     * 15. Reports Collection Repository
     */
    val reports: ReportRepository by lazy { FirebaseReportRepository() }

    /**
     * 16. System Settings Collection Repository
     */
    val settings: SettingRepository by lazy { FirebaseSettingRepository() }

    /**
     * Initialize the unified API layer and Firebase services with environment configurations.
     * Call this inside the main entry point (e.g. MainActivity).
     */
    fun initialize(context: Context) {
        FirebaseService.initialize(context)
    }
}
