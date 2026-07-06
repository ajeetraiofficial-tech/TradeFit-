package com.example.backend.model

/**
 * TradeFit ERP Backend Models
 * Production-ready Firestore compatible schemas.
 * Every model supports audit fields, soft-delete, and status.
 */

// 1. Companies Collection
data class Company(
    val id: String = "",
    val name: String = "",
    val gstin: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val stateCode: String = "",
    val registrationDate: String = "",
    val isActive: Boolean = true,
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active" // "Active", "Suspended", "Onboarding"
)

// 2. Users Collection
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Staff", // "Admin", "Accounts", "Inventory", "Sales", "Staff"
    val companyId: String = "",
    val mobile: String = "",
    val profileImageUrl: String = "",
    val lastLoginAt: Long = System.currentTimeMillis(),
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active" // "Active", "Inactive", "PendingApproval"
)

// 3. Customers Collection
data class Customer(
    val id: String = "",
    val companyName: String = "",
    val customerName: String = "",
    val gstin: String = "",
    val pan: String = "",
    val mobile: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val country: String = "India",
    val placeOfSupply: String = "",
    val contactPerson: String = "",
    val creditDays: Int = 30,
    val creditLimit: Double = 0.0,
    val openingBalance: Double = 0.0,
    val paymentTerms: String = "",
    val transportName: String = "",
    val remark: String = "",
    val outstandingAmount: Double = 0.0,
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active" // "Active", "Inactive", "Blocked"
)

// 4. Fabrics Collection
data class Fabric(
    val code: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val fabricType: String = "",
    val construction: String = "",
    val composition: String = "",
    val gsm: Int = 180,
    val width: Double = 58.0,
    val weightUnit: String = "Kg",
    val colour: String = "",
    val shadeNumber: String = "",
    val finish: String = "",
    val stretchType: String = "",
    val texture: String = "",
    val season: String = "",
    val hsnCode: String = "",
    val gstPercentage: Double = 5.0,
    val ratePerKg: Double = 0.0,
    val ratePerMeter: Double = 0.0,
    val minStock: Double = 0.0,
    val description: String = "",
    val imageUrl: String? = null,
    val currentStock: Double = 0.0,
    val specialFeatures: List<String> = emptyList(),
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active" // "Active", "Discontinued", "Out of Stock"
)

// 5. Stock Collection
data class StockEntry(
    val id: String = "",
    val entryDate: String = "",
    val fabricCode: String = "",
    val fabricName: String = "",
    val category: String = "",
    val colour: String = "",
    val gsm: Int = 180,
    val width: Double = 58.0,
    val composition: String = "",
    val lotNumber: String = "",
    val rollNumber: String = "",
    val numberOfRolls: Int = 1,
    val grossWeight: Double = 0.0,
    val tareWeight: Double = 0.0,
    val netWeight: Double = 0.0,
    val ratePerKg: Double = 0.0,
    val totalValue: Double = 0.0,
    val supplierName: String = "",
    val purchaseInvoiceNo: String = "",
    val warehouse: String = "Main Warehouse",
    val rackNumber: String = "",
    val remarks: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Available" // "Available", "Reserved", "Dispatched", "QC Pending"
)

// 6. StockTransactions Collection
data class StockTransaction(
    val id: String = "",
    val transactionDate: String = "",
    val stockEntryId: String = "",
    val fabricCode: String = "",
    val fabricName: String = "",
    val transactionType: String = "IN", // "IN", "OUT", "ADJUSTMENT", "RESERVATION"
    val quantityRolls: Int = 0,
    val weightKg: Double = 0.0,
    val referenceType: String = "", // "PO", "DC", "TI", "ADJUST"
    val referenceId: String = "",
    val warehouse: String = "Main Warehouse",
    val remarks: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Completed" // "Completed", "Pending", "Cancelled"
)

// 7. DeliveryChallans Collection
data class DeliveryChallan(
    val id: String = "",
    val date: String = "",
    val deliveryDate: String = "",
    val customerId: String = "",
    val companyName: String = "",
    val gstNumber: String = "",
    val mobile: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val transportName: String = "",
    val vehicleNumber: String = "",
    val driverName: String = "",
    val driverMobile: String = "",
    val lrNumber: String = "",
    val dispatchFrom: String = "",
    val dispatchTo: String = "",
    val eWayBill: String = "",
    val totalRolls: Int = 0,
    val totalWeight: Double = 0.0,
    val subTotal: Double = 0.0,
    val discountPercentOrAmt: String = "Percent",
    val discountValue: Double = 0.0,
    val discountAmount: Double = 0.0,
    val freightCharges: Double = 0.0,
    val packingCharges: Double = 0.0,
    val otherCharges: Double = 0.0,
    val taxableAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val remarks: String = "",
    val termsAndConditions: String = "",
    val preparedBy: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Draft" // "Draft", "Approved", "Dispatched", "Cancelled"
)

// 8. DeliveryChallanItems Collection
data class DeliveryChallanItem(
    val id: String = "",
    val challanId: String = "",
    val fabricName: String = "",
    val colour: String = "",
    val gsm: Int = 0,
    val width: Double = 0.0,
    val lotNumber: String = "",
    val rollNumbers: List<String> = emptyList(),
    val numberOfRolls: Int = 0,
    val weightKg: Double = 0.0,
    val ratePerKg: Double = 0.0,
    val amount: Double = 0.0,
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active"
)

// 9. ProformaInvoices Collection
data class ProformaInvoice(
    val id: String = "",
    val date: String = "",
    val validityDate: String = "",
    val referenceNumber: String = "",
    val customerId: String = "",
    val companyName: String = "",
    val gstNumber: String = "",
    val mobile: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val transportName: String = "",
    val totalRolls: Int = 0,
    val totalWeight: Double = 0.0,
    val subTotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val freightCharges: Double = 0.0,
    val packingCharges: Double = 0.0,
    val otherCharges: Double = 0.0,
    val taxableAmount: Double = 0.0,
    val cgstRate: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstRate: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstRate: Double = 0.0,
    val igstAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val termsAndConditions: String = "",
    val preparedBy: String = "",
    val remarks: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Draft" // "Draft", "Approved", "Converted", "Cancelled", "Expired"
)

// 10. ProformaItems Collection
data class ProformaInvoiceItem(
    val id: String = "",
    val proformaId: String = "",
    val fabricName: String = "",
    val colour: String = "",
    val gsm: Int = 0,
    val width: Double = 0.0,
    val lotNumber: String = "",
    val rollNumbers: List<String> = emptyList(),
    val rollQty: Int = 0,
    val weightKg: Double = 0.0,
    val ratePerKg: Double = 0.0,
    val discountPercent: Double = 0.0,
    val amount: Double = 0.0,
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active"
)

// 11. TaxInvoices Collection
data class TaxInvoice(
    val id: String = "",
    val invoiceNumber: String = "",
    val date: String = "",
    val proformaId: String = "",
    val deliveryChallanId: String = "",
    val customerId: String = "",
    val companyName: String = "",
    val gstNumber: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val transportName: String = "",
    val totalRolls: Int = 0,
    val totalWeight: Double = 0.0,
    val subTotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val freightCharges: Double = 0.0,
    val packingCharges: Double = 0.0,
    val otherCharges: Double = 0.0,
    val taxableAmount: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val amountPaid: Double = 0.0,
    val balanceDue: Double = 0.0,
    val termsAndConditions: String = "",
    val preparedBy: String = "",
    val remarks: String = "",
    val pdfUrl: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Unpaid" // "Unpaid", "Partially Paid", "Paid", "Cancelled"
)

// 12. TaxInvoiceItems Collection
data class TaxInvoiceItem(
    val id: String = "",
    val invoiceId: String = "",
    val fabricName: String = "",
    val colour: String = "",
    val gsm: Int = 0,
    val width: Double = 0.0,
    val hsnCode: String = "",
    val lotNumber: String = "",
    val rollQty: Int = 0,
    val weightKg: Double = 0.0,
    val ratePerKg: Double = 0.0,
    val discountPercent: Double = 0.0,
    val amount: Double = 0.0,
    val cgstRate: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstRate: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstRate: Double = 0.0,
    val igstAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active"
)

// 13. Payments Collection
data class Payment(
    val id: String = "",
    val paymentNumber: String = "",
    val date: String = "",
    val invoiceId: String = "",
    val customerId: String = "",
    val companyName: String = "",
    val amount: Double = 0.0,
    val paymentMode: String = "", // "UPI", "Bank Transfer", "Cheque", "Cash"
    val referenceNumber: String = "",
    val remarks: String = "",
    val receiptUrl: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Pending" // "Pending", "Success", "Failed", "Bounced"
)

// 14. Ledgers Collection
data class LedgerEntry(
    val id: String = "",
    val date: String = "",
    val customerId: String = "",
    val particulars: String = "",
    val voucherType: String = "", // "Invoice", "Payment", "Debit Note", "Credit Note", "Opening Balance"
    val voucherNo: String = "",
    val debit: Double = 0.0,
    val credit: Double = 0.0,
    val balance: Double = 0.0,
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Posted" // "Posted", "Void", "Draft"
)

// 15. Reports Collection
data class Report(
    val id: String = "",
    val title: String = "",
    val type: String = "Sales", // "Sales", "Inventory", "Tax", "Outstanding", "Custom"
    val generatedAt: Long = System.currentTimeMillis(),
    val generatedBy: String = "",
    val parameters: Map<String, String> = emptyMap(),
    val summaryData: Map<String, Double> = emptyMap(),
    val fileUrl: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Ready" // "Generating", "Ready", "Error", "Expired"
)

// 16. Settings Collection
data class Setting(
    val key: String = "",
    val value: String = "",
    val group: String = "General", // "General", "Taxation", "Print"
    val description: String = "",
    
    // Audit & System Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val isDeleted: Boolean = false,
    val status: String = "Active"
)
