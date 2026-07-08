# Firestore Database Architecture Specification — TradeFit ERP

This document specifies the official production-grade **Firestore Database Architecture** for **TradeFit ERP**. 

The architecture is designed to support **multi-tenancy** out-of-the-box by scoping all operational data under the top-level `/companies/{companyId}/` path. This provides complete data isolation between tenants, simplifies database rules, scales horizontally, and matches compliance standards.

---

## 🏗️ Hierarchical Database Tree

```
companies (Root Collection)
│
└── {companyId} (Document)
     ├── users (Subcollection)
     ├── customers (Subcollection)
     ├── fabrics (Subcollection)
     ├── stock (Subcollection)
     ├── stockTransactions (Subcollection)
     │
     ├── deliveryChallans (Subcollection)
     │    └── {challanId}
     │         └── items (Nested Subcollection)
     │
     ├── proformaInvoices (Subcollection)
     │    └── {proformaId}
     │         └── items (Nested Subcollection)
     │
     ├── taxInvoices (Subcollection)
     │    └── {invoiceId}
     │         └── items (Nested Subcollection)
     │
     ├── payments (Subcollection)
     ├── ledgers (Subcollection)
     ├── reports (Subcollection)
     └── settings (Subcollection)
```

---

## 🛠️ Global Design Standards

For every document in any subcollection, the following audit, integrity, and life-cycle metadata fields are strictly required:

| Field Name | Kotlin Type | Firestore Type | Description |
| :--- | :--- | :--- | :--- |
| `createdAt` | `Long` | `number (integer)` | Epoch millisecond timestamp of document creation. |
| `updatedAt` | `Long` | `number (integer)` | Epoch millisecond timestamp of the last document update. |
| `createdBy` | `String` | `string` | Firebase Auth UID of the user who created the document. |
| `updatedBy` | `String` | `string` | Firebase Auth UID of the user who last modified the document. |
| `isDeleted` | `Boolean` | `boolean` | Soft-delete flag (`false` by default). True if marked deleted. |
| `status` | `String` | `string` | Lifecycle state machine string (e.g. `"Active"`, `"Draft"`). |

---

## 📂 Detailed Collection Schemas

### 1. `companies` (Root Collection)
- **Path**: `/companies`
- **Document ID**: Unique system identifier (alphanumeric string).
- **Relationships**: Parent of all subcollections.
- **Fields**:
  - `id`: `string` (Matches Doc ID)
  - `name`: `string` (Legal registered company name)
  - `gstin`: `string` (GST Identification Number)
  - `address`: `string` (Registered office address)
  - `phone`: `string` (Office contact)
  - `email`: `string` (Company official email)
  - `stateCode`: `string` (2-digit GST state code)
  - `registrationDate`: `string` (YYYY-MM-DD)
  - `isActive`: `boolean` (License/active subscription flag)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active" | "Suspended" | "Onboarding"`)
- **Indexes**:
  - *Single Field*: `gstin` (Unique constraints lookup), `status`.

---

### 2. `users` (Subcollection)
- **Path**: `/companies/{companyId}/users`
- **Document ID**: Matches the Firebase Authentication user `uid`.
- **Relationships**: Scoped to parent `companyId`.
- **Fields**:
  - `uid`: `string` (Matches Firebase Auth UID)
  - `name`: `string` (Full name)
  - `email`: `string` (Registered email address)
  - `role`: `string` (`"Admin" | "Accounts" | "Inventory" | "Sales" | "Staff"`)
  - `companyId`: `string` (Reference to parent `/companies/{id}`)
  - `mobile`: `string` (Contact number)
  - `profileImageUrl`: `string` (Avatar link in Storage)
  - `lastLoginAt`: `number` (Epoch login timestamp)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active" | "Inactive" | "Pending"`)
- **Indexes**:
  - *Single Field*: `role`, `email`
  - *Composite*: `isDeleted` ASC + `status` ASC + `role` ASC

---

### 3. `customers` (Subcollection)
- **Path**: `/companies/{companyId}/customers`
- **Document ID**: Auto-generated string.
- **Relationships**: Scoped to parent `companyId`. Target of transactions.
- **Fields**:
  - `id`: `string`
  - `companyName`: `string` (Customer trade name)
  - `customerName`: `string` (Primary contact person name)
  - `gstin`: `string`
  - `pan`: `string`
  - `mobile`: `string`
  - `whatsapp`: `string`
  - `email`: `string`
  - `billingAddress`: `string`
  - `shippingAddress`: `string`
  - `city`: `string`
  - `state`: `string` (Used for SGST/CGST vs IGST split calculations)
  - `pincode`: `string`
  - `country`: `string`
  - `placeOfSupply`: `string`
  - `contactPerson`: `string`
  - `creditDays`: `number`
  - `creditLimit`: `number`
  - `openingBalance`: `number`
  - `paymentTerms`: `string`
  - `transportName`: `string`
  - `remark`: `string`
  - `outstandingAmount`: `number` (Maintained via increments for dashboard fast reads)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active" | "Inactive" | "Blocked"`)
- **Indexes**:
  - *Single Field*: `companyName`, `outstandingAmount`
  - *Composite*: `isDeleted` ASC + `status` ASC + `outstandingAmount` DESC

---

### 4. `fabrics` (Subcollection)
- **Path**: `/companies/{companyId}/fabrics`
- **Document ID**: Unique SKU or design code (e.g., `"FB-1002"`).
- **Relationships**: Scoped to parent `companyId`. Maps catalog catalog entries.
- **Fields**:
  - `code`: `string` (Matches Doc ID)
  - `name`: `string` (Fabric description name)
  - `brand`: `string`
  - `category`: `string` (`"Single Jersey" | "Interlock" | "Rib" | "Fleece"`)
  - `fabricType`: `string` (`"Cotton" | "Polyester" | "Nylon" | "Lycra"`)
  - `construction`: `string`
  - `composition`: `string` (Percentage blend info)
  - `gsm`: `number`
  - `width`: `number`
  - `weightUnit`: `string` (`"Kg" | "Meter"`)
  - `colour`: `string`
  - `shadeNumber`: `string`
  - `finish`: `string`
  - `stretchType`: `string`
  - `texture`: `string`
  - `season`: `string`
  - `hsnCode`: `string`
  - `gstPercentage`: `number` (Applicable GST rate, standard `5.0`)
  - `ratePerKg`: `number`
  - `ratePerMeter`: `number`
  - `minStock`: `number` (Reorder alert threshold)
  - `description`: `string`
  - `imageUrl`: `string?`
  - `currentStock`: `number` (Consolidated warehouse stock Kg)
  - `specialFeatures`: `array (string)`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active" | "Discontinued" | "Out of Stock"`)
- **Indexes**:
  - *Single Field*: `category`, `shadeNumber`, `brand`
  - *Composite*: `isDeleted` ASC + `status` ASC + `currentStock` ASC

---

### 5. `stock` (Subcollection)
- **Path**: `/companies/{companyId}/stock`
- **Document ID**: Roll piece barcode / unique serial key (e.g. `"R260706001"`).
- **Relationships**: Linked to `fabrics` by `fabricCode`.
- **Fields**:
  - `id`: `string` (Barcode ID)
  - `entryDate`: `string` (YYYY-MM-DD)
  - `fabricCode`: `string` (Reference SKU)
  - `fabricName`: `string` (Denormalized)
  - `category`: `string` (Denormalized)
  - `colour`: `string` (Denormalized)
  - `gsm`: `number`
  - `width`: `number`
  - `composition`: `string`
  - `lotNumber`: `string` (Dyeing Lot identification)
  - `rollNumber`: `string` (Lot sequence number)
  - `numberOfRolls`: `number`
  - `grossWeight`: `number`
  - `tareWeight`: `number`
  - `netWeight`: `number`
  - `ratePerKg`: `number`
  - `totalValue`: `number`
  - `supplierName`: `string`
  - `purchaseInvoiceNo`: `string`
  - `warehouse`: `string`
  - `rackNumber`: `string`
  - `remarks`: `string`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Available" | "Reserved" | "Dispatched" | "QC Pending"`)
- **Indexes**:
  - *Single Field*: `fabricCode`, `lotNumber`, `status`
  - *Composite*: `isDeleted` ASC + `status` ASC + `fabricCode` ASC

---

### 6. `stockTransactions` (Subcollection)
- **Path**: `/companies/{companyId}/stockTransactions`
- **Document ID**: Auto-generated string.
- **Relationships**: Linked to `/stock/{id}` and `/fabrics/{id}`.
- **Fields**:
  - `id`: `string`
  - `transactionDate`: `string` (YYYY-MM-DD)
  - `stockEntryId`: `string` (Target roll ID)
  - `fabricCode`: `string`
  - `fabricName`: `string`
  - `transactionType`: `string` (`"IN" | "OUT" | "ADJUSTMENT" | "RESERVATION"`)
  - `quantityRolls`: `number`
  - `weightKg`: `number`
  - `referenceType`: `string` (`"PO" | "DC" | "TI" | "ADJUST"`)
  - `referenceId`: `string` (Connected DC ID, Tax Invoice ID, or Adjustment Voucher ID)
  - `warehouse`: `string`
  - `remarks`: `string`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Completed" | "Pending" | "Cancelled"`)
- **Indexes**:
  - *Composite*: `fabricCode` ASC + `transactionType` ASC + `transactionDate` DESC

---

### 7. `deliveryChallans` (Subcollection)
- **Path**: `/companies/{companyId}/deliveryChallans`
- **Document ID**: Serialized DC number (e.g. `"DC-2627-01"`).
- **Relationships**: Parent of `deliveryChallans/{id}/items`. Linked to `customers`.
- **Fields**:
  - `id`: `string` (Serial Code)
  - `date`: `string` (YYYY-MM-DD)
  - `deliveryDate`: `string` (YYYY-MM-DD)
  - `customerId`: `string`
  - `companyName`: `string` (Denormalized)
  - `gstNumber`: `string`
  - `mobile`: `string`
  - `billingAddress`: `string`
  - `shippingAddress`: `string`
  - `transportName`: `string`
  - `vehicleNumber`: `string`
  - `driverName`: `string`
  - `driverMobile`: `string`
  - `lrNumber`: `string` (Lorry Receipt Number)
  - `dispatchFrom`: `string`
  - `dispatchTo`: `string`
  - `eWayBill`: `string`
  - `totalRolls`: `number`
  - `totalWeight`: `number`
  - `subTotal`: `number`
  - `discountPercentOrAmt`: `string`
  - `discountValue`: `number`
  - `discountAmount`: `number`
  - `freightCharges`: `number`
  - `packingCharges`: `number`
  - `otherCharges`: `number`
  - `taxableAmount`: `number`
  - `grandTotal`: `number`
  - `remarks`: `string`
  - `termsAndConditions`: `string`
  - `preparedBy`: `string`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Draft" | "Approved" | "Dispatched" | "Cancelled"`)
- **Indexes**:
  - *Single Field*: `customerId`, `status`, `date`
  - *Composite*: `isDeleted` ASC + `status` ASC + `date` DESC

---

### 8. `deliveryChallans/{challanId}/items` (Nested Subcollection)
- **Path**: `/companies/{companyId}/deliveryChallans/{challanId}/items`
- **Document ID**: Auto-generated string.
- **Relationships**: Child subcollection of `/deliveryChallans/{challanId}`.
- **Fields**:
  - `id`: `string`
  - `challanId`: `string`
  - `fabricName`: `string`
  - `colour`: `string`
  - `gsm`: `number`
  - `width`: `number`
  - `lotNumber`: `string`
  - `rollNumbers`: `array (string)` (Specific barcodes dispatched on this item row)
  - `numberOfRolls`: `number`
  - `weightKg`: `number`
  - `ratePerKg`: `number`
  - `amount`: `number`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active" | "Cancelled"`)
- **Indexes**:
  - Managed inherently inside the document hierarchy tree under parent `challanId`.

---

### 9. `proformaInvoices` (Subcollection)
- **Path**: `/companies/{companyId}/proformaInvoices`
- **Document ID**: Serialized PI number (e.g. `"PI-2627-01"`).
- **Relationships**: Parent of `proformaInvoices/{id}/items`. Linked to `customers`.
- **Fields**:
  - `id`: `string`
  - `date`: `string` (YYYY-MM-DD)
  - `validityDate`: `string` (YYYY-MM-DD)
  - `referenceNumber`: `string` (Purchase Order RFQ code)
  - `customerId`: `string`
  - `companyName`: `string` (Denormalized)
  - `gstNumber`: `string`
  - `mobile`: `string`
  - `billingAddress`: `string`
  - `shippingAddress`: `string`
  - `transportName`: `string`
  - `totalRolls`: `number`
  - `totalWeight`: `number`
  - `subTotal`: `number`
  - `discountAmount`: `number`
  - `freightCharges`: `number`
  - `packingCharges`: `number`
  - `otherCharges`: `number`
  - `taxableAmount`: `number`
  - `cgstRate`: `number`
  - `cgstAmount`: `number`
  - `sgstRate`: `number`
  - `sgstAmount`: `number`
  - `igstRate`: `number`
  - `igstAmount`: `number`
  - `grandTotal`: `number`
  - `termsAndConditions`: `string`
  - `preparedBy`: `string`
  - `remarks`: `string`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Draft" | "Approved" | "Converted" | "Cancelled" | "Expired"`)
- **Indexes**:
  - *Single Field*: `customerId`, `status`
  - *Composite*: `isDeleted` ASC + `status` ASC + `date` DESC

---

### 10. `proformaInvoices/{proformaId}/items` (Nested Subcollection)
- **Path**: `/companies/{companyId}/proformaInvoices/{proformaId}/items`
- **Document ID**: Auto-generated string.
- **Relationships**: Child of `/proformaInvoices/{proformaId}`.
- **Fields**:
  - `id`: `string`
  - `proformaId`: `string`
  - `fabricName`: `string`
  - `colour`: `string`
  - `gsm`: `number`
  - `width`: `number`
  - `lotNumber`: `string`
  - `rollNumbers`: `array (string)`
  - `rollQty`: `number`
  - `weightKg`: `number`
  - `ratePerKg`: `number`
  - `discountPercent`: `number`
  - `amount`: `number` (Taxable baseline)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active"`)

---

### 11. `taxInvoices` (Subcollection)
- **Path**: `/companies/{companyId}/taxInvoices`
- **Document ID**: Serialized Tax Invoice number (e.g. `"TI-2627-01"`).
- **Relationships**: Parent of `taxInvoices/{id}/items`. Linked to `customers`, `deliveryChallans`, `proformaInvoices`.
- **Fields**:
  - `id`: `string`
  - `invoiceNumber`: `string` (Legal sequence identifier)
  - `date`: `string` (YYYY-MM-DD)
  - `proformaId`: `string` (Optional conversion reference)
  - `deliveryChallanId`: `string` (Optional dispatch reference)
  - `customerId`: `string`
  - `companyName`: `string` (Denormalized)
  - `gstNumber`: `string`
  - `billingAddress`: `string`
  - `shippingAddress`: `string`
  - `transportName`: `string`
  - `totalRolls`: `number`
  - `totalWeight`: `number`
  - `subTotal`: `number`
  - `discountAmount`: `number`
  - `freightCharges`: `number`
  - `packingCharges`: `number`
  - `otherCharges`: `number`
  - `taxableAmount`: `number`
  - `cgstAmount`: `number`
  - `sgstAmount`: `number`
  - `igstAmount`: `number`
  - `grandTotal`: `number`
  - `amountPaid`: `number` (Updated via Payment entries)
  - `balanceDue`: `number` (Outstanding amount remaining)
  - `termsAndConditions`: `string`
  - `preparedBy`: `string`
  - `remarks`: `string`
  - `pdfUrl`: `string` (Compiled PDF download URL in Firebase Storage)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Unpaid" | "Partially Paid" | "Paid" | "Cancelled"`)
- **Indexes**:
  - *Single Field*: `customerId`, `invoiceNumber`, `status`
  - *Composite*: `isDeleted` ASC + `status` ASC + `date` DESC

---

### 12. `taxInvoices/{invoiceId}/items` (Nested Subcollection)
- **Path**: `/companies/{companyId}/taxInvoices/{invoiceId}/items`
- **Document ID**: Auto-generated string.
- **Relationships**: Child of `/taxInvoices/{invoiceId}`.
- **Fields**:
  - `id`: `string`
  - `invoiceId`: `string`
  - `fabricName`: `string`
  - `colour`: `string`
  - `gsm`: `number`
  - `width`: `number`
  - `hsnCode`: `string`
  - `lotNumber`: `string`
  - `rollQty`: `number`
  - `weightKg`: `number`
  - `ratePerKg`: `number`
  - `discountPercent`: `number`
  - `amount`: `number` (Taxable baseline)
  - `cgstRate`: `number`
  - `cgstAmount`: `number`
  - `sgstRate`: `number`
  - `sgstAmount`: `number`
  - `igstRate`: `number`
  - `igstAmount`: `number`
  - `totalAmount`: `number` (Taxable + GST total amount)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active"`)

---

### 13. `payments` (Subcollection)
- **Path**: `/companies/{companyId}/payments`
- **Document ID**: Auto-generated transaction ID.
- **Relationships**: Linked to `customers`, `taxInvoices`.
- **Fields**:
  - `id`: `string`
  - `paymentNumber`: `string` (Receipt sequence number)
  - `date`: `string` (YYYY-MM-DD)
  - `invoiceId`: `string` (Cleared invoice reference)
  - `customerId`: `string`
  - `companyName`: `string` (Denormalized)
  - `amount`: `number` (Payment amount)
  - `paymentMode`: `string` (`"UPI" | "Bank Transfer" | "Cheque" | "Cash"`)
  - `referenceNumber`: `string` (UTR / Cheque transaction key)
  - `remarks`: `string`
  - `receiptUrl`: `string` (Storage reference link to transaction slip)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Pending" | "Success" | "Failed" | "Bounced"`)
- **Indexes**:
  - *Single Field*: `customerId`, `invoiceId`
  - *Composite*: `isDeleted` ASC + `customerId` ASC + `date` DESC

---

### 14. `ledgers` (Subcollection)
- **Path**: `/companies/{companyId}/ledgers`
- **Document ID**: Auto-generated ledger posting ID.
- **Relationships**: Linked to `/customers/{id}`. Double-entry financial postings feed.
- **Fields**:
  - `id`: `string`
  - `date`: `string` (YYYY-MM-DD)
  - `customerId`: `string`
  - `particulars`: `string` (Narration text)
  - `voucherType`: `string` (`"Invoice" | "Payment" | "Debit Note" | "Credit Note" | "Opening Balance"`)
  - `voucherNo`: `string` (Linked voucher identifier)
  - `debit`: `number` (Positive additions to customer debt / invoices)
  - `credit`: `number` (Subtractions to customer debt / payments / credit notes)
  - `balance`: `number` (Running balance after the posting)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Posted" | "Void" | "Draft"`)
- **Indexes**:
  - *Composite*: `isDeleted` ASC + `customerId` ASC + `createdAt` ASC (Essential for compiling sequential running balance)

---

### 15. `reports` (Subcollection)
- **Path**: `/companies/{companyId}/reports`
- **Document ID**: Auto-generated ID.
- **Relationships**: Scoped to parent `companyId`. Represents generated business statements.
- **Fields**:
  - `id`: `string`
  - `title`: `string`
  - `type`: `string` (`"Sales" | "Inventory" | "Tax" | "Outstanding" | "Custom"`)
  - `generatedAt`: `number`
  - `generatedBy`: `string`
  - `parameters`: `map (string -> string)` (Filter parameters used)
  - `summaryData`: `map (string -> number)` (Consolidated metrics)
  - `fileUrl`: `string` (Download link to CSV / Excel sheet in Storage)
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Generating" | "Ready" | "Error" | "Expired"`)
- **Indexes**:
  - *Single Field*: `type`, `generatedAt`

---

### 16. `settings` (Subcollection)
- **Path**: `/companies/{companyId}/settings`
- **Document ID**: Standard system unique keys (e.g. `"invoice_billing_terms"`).
- **Relationships**: Scoped to parent `companyId`.
- **Fields**:
  - `key`: `string` (Matches Doc ID)
  - `value`: `string` (Can contain serialized configurations/JSON details)
  - `group`: `string` (`"General" | "Taxation" | "Print"`)
  - `description`: `string`
  - *Standard Audit & Metadata Fields* (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`, `status` = `"Active"`)
- **Indexes**:
  - *Single Field*: `group`

---

## 📈 Scalability & Performance Guidelines

1. **Horizontal Distribution**: Document IDs utilize random tokens (via Firestore's built-in `db.collection().document()`) to distribute load evenly across physical Google Spanner partitions.
2. **Infinite Line Subcollections**: By nesting `items` as dynamic subcollections of their respective documents, we prevent breaching the 1MB single document size limit in Firestore, allowing invoices with hundreds or thousands of transactions.
3. **Optimistic Updates**: Increments are managed using atomic transactions or Firestore's `FieldValue.increment()` to ensure high accuracy without race conditions across simultaneous sales/accounting terminals.
