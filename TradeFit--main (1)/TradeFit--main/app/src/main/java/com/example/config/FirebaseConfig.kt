package com.example.config

/**
 * TradeFit ERP - Firebase Configuration Placeholders
 * This file serves as a structure for Firebase Services injection once we implement the actual logic.
 *
 * In accordance with instructions, we configure placeholders for:
 * 1. Firebase Authentication
 * 2. Cloud Firestore (for ERP data like Customers, Fabrics, Stock, Invoices)
 * 3. Firebase Storage (for Delivery Challans, Invoices PDF storage)
 */
object FirebaseConfig {
    
    // Auth configuration
    const val USE_EMULATOR = false
    const val EMULATOR_HOST = "10.0.2.2"
    const val AUTH_PORT = 9099
    
    // Firestore Collections
    object Collections {
        const val COMPANIES = "companies"
        const val USERS = "users"
        const val CUSTOMERS = "customers"
        const val FABRICS = "fabrics"
        const val STOCK = "stock"
        const val STOCK_TRANSACTIONS = "stockTransactions"
        const val DELIVERY_CHALLANS = "deliveryChallans"
        const val DELIVERY_CHALLAN_ITEMS = "deliveryChallanItems"
        const val PROFORMA_INVOICES = "proformaInvoices"
        const val PROFORMA_ITEMS = "proformaItems"
        const val TAX_INVOICES = "taxInvoices"
        const val TAX_INVOICE_ITEMS = "taxInvoiceItems"
        const val PAYMENTS = "payments"
        const val LEDGERS = "ledgers"
        const val REPORTS = "reports"
        const val SETTINGS = "settings"
    }

    // Dynamic Multi-tenant Path Resolvers
    fun getCompaniesPath() = "companies"
    fun getUsersPath(companyId: String) = "companies/$companyId/users"
    fun getCustomersPath(companyId: String) = "companies/$companyId/customers"
    fun getFabricsPath(companyId: String) = "companies/$companyId/fabrics"
    fun getStockPath(companyId: String) = "companies/$companyId/stock"
    fun getStockTransactionsPath(companyId: String) = "companies/$companyId/stockTransactions"
    fun getDeliveryChallansPath(companyId: String) = "companies/$companyId/deliveryChallans"
    fun getDeliveryChallanItemsPath(companyId: String, challanId: String) = "companies/$companyId/deliveryChallans/$challanId/items"
    fun getProformaInvoicesPath(companyId: String) = "companies/$companyId/proformaInvoices"
    fun getProformaItemsPath(companyId: String, proformaId: String) = "companies/$companyId/proformaInvoices/$proformaId/items"
    fun getTaxInvoicesPath(companyId: String) = "companies/$companyId/taxInvoices"
    fun getTaxInvoiceItemsPath(companyId: String, invoiceId: String) = "companies/$companyId/taxInvoices/$invoiceId/items"
    fun getPaymentsPath(companyId: String) = "companies/$companyId/payments"
    fun getLedgersPath(companyId: String) = "companies/$companyId/ledgers"
    fun getReportsPath(companyId: String) = "companies/$companyId/reports"
    fun getSettingsPath(companyId: String) = "companies/$companyId/settings"

    // Storage Paths
    object StoragePaths {
        const val FABRIC_SWATCHES = "fabrics/swatches/"
        const val CHALLAN_DOCS = "documents/challans/"
        const val INVOICE_PDFS = "documents/invoices/"
    }

    /**
     * Firebase Service Initialization status
     */
    fun getStatus(): FirebaseStatus {
        return FirebaseStatus(
            isAuthReady = true,
            isFirestoreReady = true,
            isStorageReady = true,
            currentEnvironment = if (USE_EMULATOR) "Firebase Emulator Suite" else "Production Cloud Firebase"
        )
    }
}

data class FirebaseStatus(
    val isAuthReady: Boolean,
    val isFirestoreReady: Boolean,
    val isStorageReady: Boolean,
    val currentEnvironment: String
)
