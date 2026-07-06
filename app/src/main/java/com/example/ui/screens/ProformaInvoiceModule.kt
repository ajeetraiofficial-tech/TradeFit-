package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProformaInvoiceScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Invoices") }
    val filters = listOf("All Invoices", "Draft", "Generated")
    var sortBy by remember { mutableStateOf("ID") }
    val sortOptions = listOf("ID", "Customer", "Date", "Grand Total")
    
    var showEditor by remember { mutableStateOf(false) }
    var editingProforma by remember { mutableStateOf<ProformaInvoice?>(null) }
    var viewingProforma by remember { mutableStateOf<ProformaInvoice?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<ProformaInvoice?>(null) }
    var showConvertConfirm by remember { mutableStateOf<ProformaInvoice?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingSkeleton by remember { mutableStateOf(false) }

    // Table Pagination state
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    // Dynamic list of Proforma Invoices
    val proformas = TradeFitSharedState.proformaInvoices

    // Handle incoming converted challan from delivery challan screen!
    LaunchedEffect(TradeFitSharedState.openFormPrefilled) {
        if (TradeFitSharedState.openFormPrefilled && TradeFitSharedState.pendingConversionChallan != null) {
            editingProforma = null // Force creating a new one with prefilled data
            showEditor = true
            TradeFitSharedState.openFormPrefilled = false
            toastMessage = "Prefilled form from Delivery Challan ${TradeFitSharedState.pendingConversionChallan?.id}!"
        }
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3000)
            toastMessage = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .testTag("proforma_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Breadcrumb(path = listOf("Home", "Finance", "Proforma Invoices"))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Proforma Invoices (Quotation Statements)",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MatteBlack,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Issue proforma statements, convert delivery challans into quotations, and track advances.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextDark
                    )
                }

                Button(
                    onClick = {
                        editingProforma = null
                        showEditor = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                    modifier = Modifier.testTag("add_proforma_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create", tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Proforma Quote", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }

            // Summary KPIs section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val totalAmount = proformas.sumOf { it.grandTotal }
                val activeCount = proformas.count { it.status == "Generated" }
                val draftCount = proformas.count { it.status == "Draft" }

                DashboardKPICard(
                    title = "Total Quoted Value",
                    value = "₹${String.format("%,.2f", totalAmount)}",
                    subtitle = "Across all active quotes",
                    icon = Icons.Default.MonetizationOn,
                    color = PositiveGrowth,
                    modifier = Modifier.weight(1f)
                )
                DashboardKPICard(
                    title = "Generated Invoices",
                    value = "$activeCount Invoices",
                    subtitle = "Sent & awaiting advance",
                    icon = Icons.Default.Receipt,
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                DashboardKPICard(
                    title = "Pending Drafts",
                    value = "$draftCount Drafts",
                    subtitle = "Quotation worksheets",
                    icon = Icons.Default.Edit,
                    color = SlateTextLight,
                    modifier = Modifier.weight(1f)
                )
            }

            // Search, Filter and Sort Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1.5f)) {
                        SearchComponent(
                            query = searchKeyword,
                            onQueryChange = { searchKeyword = it },
                            placeholder = "Search by ID, Company or Ref No..."
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        FilterComponent(
                            selectedFilter = selectedFilter,
                            availableFilters = filters,
                            onFilterSelected = { selectedFilter = it }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sort: ", style = MaterialTheme.typography.bodyMedium, color = SlateTextDark, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            var showSortDropdown by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { showSortDropdown = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(sortBy, style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort Options", tint = MatteBlack)
                                }
                                DropdownMenu(
                                    expanded = showSortDropdown,
                                    onDismissRequest = { showSortDropdown = false }
                                ) {
                                    sortOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt) },
                                            onClick = {
                                                sortBy = opt
                                                showSortDropdown = false
                                                isLoadingSkeleton = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Simulate Loading Skeleton on sorting
            if (isLoadingSkeleton) {
                LaunchedEffect(Unit) {
                    delay(400)
                    isLoadingSkeleton = false
                }
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardBorder()
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(4) {
                            Box(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray.copy(alpha = 0.3f)))
                        }
                    }
                }
            } else {
                // Main Table Content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardBorder()
                ) {
                    val filtered = proformas.filter {
                        (selectedFilter == "All Invoices" || it.status.equals(selectedFilter, ignoreCase = true)) &&
                        (it.id.contains(searchKeyword, ignoreCase = true) ||
                         it.companyName.contains(searchKeyword, ignoreCase = true) ||
                         it.referenceNumber.contains(searchKeyword, ignoreCase = true))
                    }.sortedWith { a, b ->
                        when (sortBy) {
                            "Customer" -> a.companyName.compareTo(b.companyName, ignoreCase = true)
                            "Date" -> b.date.compareTo(a.date) // reverse date
                            "Grand Total" -> b.grandTotal.compareTo(a.grandTotal)
                            else -> a.id.compareTo(b.id)
                        }
                    }

                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            EmptyState(
                                title = "No Proforma Invoices Found",
                                message = "No records matched your query or selected criteria."
                            )
                        }
                    } else {
                        // Paginate
                        val totalPages = maxOf(1, (filtered.size + itemsPerPage - 1) / itemsPerPage)
                        if (currentPage > totalPages) currentPage = totalPages
                        val startIndex = (currentPage - 1) * itemsPerPage
                        val endIndex = minOf(startIndex + itemsPerPage, filtered.size)
                        val pageItems = filtered.subList(startIndex, endIndex)

                        Column(modifier = Modifier.fillMaxSize()) {
                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OffWhite)
                                    .padding(horizontal = 24.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Proforma ID", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                                Text("Date", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                Text("Validity Date", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                Text("Customer Company", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.8f))
                                Text("Total Rolls / Weight", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.2f))
                                Text("Grand Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.2f))
                                Text("Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                Text("Actions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.8f))
                            }

                            HorizontalDivider(color = DarkSlateBorder)

                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(pageItems) { pi ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 12.dp)
                                            .clickable { viewingProforma = pi },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(pi.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = ExecutiveBlue, modifier = Modifier.weight(1f))
                                        Text(pi.date, style = MaterialTheme.typography.bodyMedium, color = MatteBlack, modifier = Modifier.weight(0.8f))
                                        Text(pi.validityDate, style = MaterialTheme.typography.bodyMedium, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                        Text(pi.companyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MatteBlack, modifier = Modifier.weight(1.8f))
                                        Text("${pi.totalRolls} Rolls / ${pi.totalWeight} Kg", style = MaterialTheme.typography.bodyMedium, color = MatteBlack, modifier = Modifier.weight(1.2f))
                                        Text("₹${String.format("%,.2f", pi.grandTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack, modifier = Modifier.weight(1.2f))
                                        
                                        Box(modifier = Modifier.weight(0.8f)) {
                                            val badgeColor = when (pi.status) {
                                                "Generated" -> PositiveGrowth
                                                "Draft" -> SlateTextLight
                                                else -> SlateTextDark
                                            }
                                            StatusBadge(status = pi.status.uppercase(), color = badgeColor)
                                        }

                                        Row(
                                            modifier = Modifier.weight(1.8f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = { viewingProforma = pi },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Visibility, contentDescription = "View Details", tint = ExecutiveBlue, modifier = Modifier.size(18.dp))
                                            }

                                            IconButton(
                                                onClick = {
                                                    editingProforma = pi
                                                    showEditor = true
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MatteBlack, modifier = Modifier.size(18.dp))
                                            }

                                            IconButton(
                                                onClick = {
                                                    val nextId = "PI-${String.format("%06d", proformas.size + 1)}"
                                                    val duplicated = pi.copy(
                                                        id = nextId,
                                                        date = "2026-07-06",
                                                        validityDate = "2026-07-20",
                                                        status = "Draft"
                                                    )
                                                    proformas.add(duplicated)
                                                    toastMessage = "Proforma ${pi.id} duplicated successfully as draft $nextId"
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = ProgressiveOrange, modifier = Modifier.size(18.dp))
                                            }

                                            IconButton(
                                                onClick = { showDeleteConfirm = pi },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WarningAlert, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = DarkSlateBorder.copy(alpha = 0.5f))
                                }
                            }

                            // Pagination Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OffWhite)
                                    .padding(horizontal = 24.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Showing ${startIndex + 1}-${endIndex} of ${filtered.size} records", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { if (currentPage > 1) currentPage-- },
                                        enabled = currentPage > 1
                                    ) {
                                        Text("Previous", color = if (currentPage > 1) ExecutiveBlue else SlateTextLight)
                                    }
                                    Text("Page $currentPage of $totalPages", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    TextButton(
                                        onClick = { if (currentPage < totalPages) currentPage++ },
                                        enabled = currentPage < totalPages
                                    ) {
                                        Text("Next", color = if (currentPage < totalPages) ExecutiveBlue else SlateTextLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Float Toast Message
        toastMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MatteBlack.copy(alpha = 0.9f))
                    .border(1.dp, DarkSlateBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = PureWhite, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(msg, color = PureWhite, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // 1. Proforma Creator / Editor Modal
        if (showEditor) {
            ProformaFormEditor(
                proforma = editingProforma,
                onDismiss = { showEditor = false },
                onSave = { updated ->
                    val index = proformas.indexOfFirst { it.id == updated.id }
                    if (index >= 0) {
                        proformas[index] = updated
                        toastMessage = "Proforma ${updated.id} updated successfully."
                    } else {
                        proformas.add(updated)
                        toastMessage = "Proforma ${updated.id} created successfully."
                    }
                    showEditor = false
                }
            )
        }

        // 2. View Proforma Preview Sheet Modal
        viewingProforma?.let { pi ->
            ProformaPreviewSheet(
                proforma = pi,
                onDismiss = { viewingProforma = null },
                onDownloadPdf = {
                    toastMessage = "Download initiated... PDF compiled successfully for ${pi.id}."
                },
                onSharePdf = {
                    toastMessage = "Share link copied: tradefit-erp://proforma/${pi.id}"
                },
                onConvertToTaxInvoice = {
                    showConvertConfirm = pi
                }
            )
        }

        // 3. Confirmation Dialog for Delete
        showDeleteConfirm?.let { pi ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to permanently delete Proforma Invoice ${pi.id} for ${pi.companyName}? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            proformas.remove(pi)
                            toastMessage = "Proforma Invoice ${pi.id} deleted successfully."
                            showDeleteConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAlert)
                    ) {
                        Text("Delete", color = PureWhite)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteConfirm = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 4. Confirmation Dialog for Convert to Tax Invoice
        showConvertConfirm?.let { pi ->
            AlertDialog(
                onDismissRequest = { showConvertConfirm = null },
                title = { Text("Convert to Tax Invoice") },
                text = { Text("You are about to convert Proforma ${pi.id} into a final Tax Invoice. This will finalize pricing, lock fields, and record the transaction. Proceed?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewingProforma = null
                            showConvertConfirm = null
                            toastMessage = "Successfully converted Proforma ${pi.id} to Tax Invoice TX-2026-503!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PositiveGrowth)
                    ) {
                        Text("Convert & Lock", color = PureWhite)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showConvertConfirm = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ProformaFormEditor(
    proforma: ProformaInvoice?,
    onDismiss: () -> Unit,
    onSave: (ProformaInvoice) -> Unit
) {
    val proformas = TradeFitSharedState.proformaInvoices
    val proformaId = proforma?.id ?: "PI-${String.format("%06d", proformas.size + 1)}"

    var date by remember { mutableStateOf(proforma?.date ?: "2026-07-06") }
    var validityDate by remember { mutableStateOf(proforma?.validityDate ?: "2026-07-20") }
    var referenceNumber by remember { mutableStateOf(proforma?.referenceNumber ?: "") }

    // Customer parameters
    var selectedCustId by remember { mutableStateOf(proforma?.customerId ?: "") }
    var companyName by remember { mutableStateOf(proforma?.companyName ?: "") }
    var gstNumber by remember { mutableStateOf(proforma?.gstNumber ?: "") }
    var mobile by remember { mutableStateOf(proforma?.mobile ?: "") }
    var billingAddress by remember { mutableStateOf(proforma?.billingAddress ?: "") }
    var shippingAddress by remember { mutableStateOf(proforma?.shippingAddress ?: "") }
    var transportName by remember { mutableStateOf(proforma?.transportName ?: "") }

    // Items Grid State
    val items = remember {
        mutableStateListOf<ProformaInvoiceItem>().apply {
            if (proforma != null) {
                addAll(proforma.items)
            } else if (TradeFitSharedState.pendingConversionChallan != null) {
                // Pre-fill from pending conversion challan!
                val ch = TradeFitSharedState.pendingConversionChallan!!
                selectedCustId = ch.customerId
                companyName = ch.companyName
                gstNumber = ch.gstNumber
                mobile = ch.mobile
                billingAddress = ch.billingAddress
                shippingAddress = ch.shippingAddress
                transportName = ch.transportName
                
                ch.items.forEachIndexed { idx, it ->
                    add(
                        ProformaInvoiceItem(
                            id = "PI-ITEM-${idx + 1}",
                            fabricName = it.fabricName,
                            colour = it.colour,
                            gsm = it.gsm,
                            width = it.width,
                            lotNumber = it.lotNumber,
                            rollNumbers = it.rollNumbers,
                            rollQty = it.numberOfRolls,
                            weightKg = it.weightKg,
                            ratePerKg = it.ratePerKg,
                            discountPercent = 0.0,
                            amount = it.amount
                        )
                    )
                }
                // Clear the state so it doesn't get trigger-happy next time
                TradeFitSharedState.pendingConversionChallan = null
            } else {
                // Add a blank row
                add(
                    ProformaInvoiceItem(
                        id = "PI-ITEM-1",
                        fabricName = "",
                        colour = "",
                        gsm = 0,
                        width = 0.0,
                        lotNumber = "",
                        rollNumbers = emptyList(),
                        rollQty = 0,
                        weightKg = 0.0,
                        ratePerKg = 0.0,
                        discountPercent = 0.0,
                        amount = 0.0
                    )
                )
            }
        }
    }

    // Calculations & summaries
    var freightChargesStr by remember { mutableStateOf(proforma?.freightCharges?.toString() ?: "0.0") }
    var packingChargesStr by remember { mutableStateOf(proforma?.packingCharges?.toString() ?: "0.0") }
    var otherChargesStr by remember { mutableStateOf(proforma?.otherCharges?.toString() ?: "0.0") }
    var discountAmountStr by remember { mutableStateOf(proforma?.discountAmount?.toString() ?: "0.0") }

    var termsAndConditions by remember { mutableStateOf(proforma?.termsAndConditions ?: "1. Goods once sold are not returnable.\n2. 50% advance along with order confirmation, balance before loading.") }
    var preparedBy by remember { mutableStateOf(proforma?.preparedBy ?: "Anita Roy (Accounts Head)") }
    var isDraftState by remember { mutableStateOf(proforma?.status == "Draft" || proforma == null) }
    var remarks by remember { mutableStateOf(proforma?.remarks ?: "") }

    var showCustomerSearch by remember { mutableStateOf(false) }
    var showChallanSearch by remember { mutableStateOf(false) }
    var activeItemIndexForSearch by remember { mutableStateOf<Int?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Dynamic Live calculations
    val totalRolls = items.sumOf { it.rollQty }
    val totalWeight = items.sumOf { it.weightKg }
    val subTotal = items.sumOf { it.amount }

    val discountAmt = discountAmountStr.toDoubleOrNull() ?: 0.0
    val freight = freightChargesStr.toDoubleOrNull() ?: 0.0
    val packing = packingChargesStr.toDoubleOrNull() ?: 0.0
    val other = otherChargesStr.toDoubleOrNull() ?: 0.0

    val taxableAmount = subTotal - discountAmt + freight + packing + other
    
    // CGST/SGST vs IGST calculation
    // Maharashtra state code is "27", so if customer GST starts with "27", apply CGST/SGST @9% each. Otherwise IGST @18%
    val isInterstate = gstNumber.isNotEmpty() && !gstNumber.startsWith("27")
    val cgstRate = if (isInterstate) 0.0 else 9.0
    val sgstRate = if (isInterstate) 0.0 else 9.0
    val igstRate = if (isInterstate) 18.0 else 0.0

    val cgstAmount = taxableAmount * cgstRate / 100.0
    val sgstAmount = taxableAmount * sgstRate / 100.0
    val igstAmount = taxableAmount * igstRate / 100.0
    val grandTotal = taxableAmount + cgstAmount + sgstAmount + igstAmount

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(40.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("proforma_editor_modal"),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OffWhite)
                            .padding(horizontal = 24.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (proforma == null) "Create Proforma Invoice" else "Modify Proforma Invoice",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MatteBlack
                            )
                            Text(
                                text = "Document ID: $proformaId",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, DarkSlateBorder)
                            ) {
                                Text("Discard Changes", color = SlateTextDark)
                            }

                            Button(
                                onClick = {
                                    if (companyName.isEmpty()) {
                                        validationError = "Customer selection is required."
                                        return@Button
                                    }
                                    if (items.isEmpty() || items.any { it.fabricName.isEmpty() || it.weightKg <= 0.0 }) {
                                        validationError = "At least one valid fabric item is required with a positive weight."
                                        return@Button
                                    }

                                    val newProforma = ProformaInvoice(
                                        id = proformaId,
                                        date = date,
                                        validityDate = validityDate,
                                        referenceNumber = referenceNumber,
                                        customerId = selectedCustId,
                                        companyName = companyName,
                                        gstNumber = gstNumber,
                                        billingAddress = billingAddress,
                                        shippingAddress = shippingAddress,
                                        transportName = transportName,
                                        items = items.toList(),
                                        totalRolls = totalRolls,
                                        totalWeight = totalWeight,
                                        subTotal = subTotal,
                                        discountAmount = discountAmt,
                                        freightCharges = freight,
                                        packingCharges = packing,
                                        otherCharges = other,
                                        taxableAmount = taxableAmount,
                                        cgstRate = cgstRate,
                                        cgstAmount = cgstAmount,
                                        sgstRate = sgstRate,
                                        sgstAmount = sgstAmount,
                                        igstRate = igstRate,
                                        igstAmount = igstAmount,
                                        grandTotal = grandTotal,
                                        termsAndConditions = termsAndConditions,
                                        preparedBy = preparedBy,
                                        remarks = remarks,
                                        status = if (isDraftState) "Draft" else "Generated"
                                    )
                                    onSave(newProforma)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                            ) {
                                Text(if (proforma == null) "Generate & Issue" else "Save Changes", color = PureWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = DarkSlateBorder)

                    // Error bar
                    validationError?.let { err ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WarningAlert.copy(alpha = 0.15f))
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = WarningAlert)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(err, color = WarningAlert, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { validationError = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = WarningAlert, modifier = Modifier.size(16.dp))
                            }
                        }
                        HorizontalDivider(color = WarningAlert.copy(alpha = 0.3f))
                    }

                    // Content Split View
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        // Left Column (Details, Customer, Convert)
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight()
                                .background(OffWhite)
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Direct / Convert selector
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Quotation Workflow Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { /* Direct entry standard */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue.copy(alpha = 0.1f), contentColor = ExecutiveBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Direct Quote", fontWeight = FontWeight.SemiBold)
                                        }
                                        OutlinedButton(
                                            onClick = { showChallanSearch = true },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            Icon(Icons.Default.SwapHoriz, contentDescription = "Convert icon", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Convert Challan", color = MatteBlack, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }

                            // Dates & reference
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Quotation Parameters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = date, onValueChange = { date = it }, label = "Invoice Date", placeholder = "YYYY-MM-DD")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = validityDate, onValueChange = { validityDate = it }, label = "Validity Date", placeholder = "YYYY-MM-DD")
                                        }
                                    }
                                    TradeFitTextField(value = referenceNumber, onValueChange = { referenceNumber = it }, label = "Buyer Purchase Ref No (Optional)", placeholder = "e.g., PO-89021")
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = isDraftState, onCheckedChange = { isDraftState = it })
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Save as Draft (Unissued Worksheet)", style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
                                    }
                                }
                            }

                            // Customer search and autofill
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Buyer Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                        TextButton(onClick = { showCustomerSearch = true }) {
                                            Icon(Icons.Default.Search, contentDescription = "Search buyers", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Search Customers", color = ExecutiveBlue)
                                        }
                                    }

                                    if (companyName.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, DarkSlateBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .clickable { showCustomerSearch = true }
                                                .padding(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Select a customer to automatically populate GST, Billing/Shipping and transport details", style = MaterialTheme.typography.bodyMedium, color = SlateTextLight, modifier = Modifier.padding(horizontal = 8.dp))
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            TradeFitTextField(value = companyName, onValueChange = { companyName = it }, label = "Customer Company Name", placeholder = "Autofilled")
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Box(modifier = Modifier.weight(1.2f)) {
                                                    TradeFitTextField(value = gstNumber, onValueChange = { gstNumber = it }, label = "GSTIN", placeholder = "Autofilled")
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(value = mobile, onValueChange = { mobile = it }, label = "Contact Phone", placeholder = "Autofilled")
                                                }
                                            }
                                            TradeFitTextField(value = billingAddress, onValueChange = { billingAddress = it }, label = "Billing Address", placeholder = "Autofilled")
                                            TradeFitTextField(value = shippingAddress, onValueChange = { shippingAddress = it }, label = "Shipping Address", placeholder = "Autofilled")
                                            TradeFitTextField(value = transportName, onValueChange = { transportName = it }, label = "Pre-arranged Transport Agency", placeholder = "Autofilled")
                                        }
                                    }
                                }
                            }
                        }

                        // Right Column (Items grid, computations, QR/bank details)
                        Column(
                            modifier = Modifier
                                .weight(1.8f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Fabric items list card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Quoted Fabric Line Items *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                        Button(
                                            onClick = {
                                                items.add(
                                                    ProformaInvoiceItem(
                                                        id = "PI-ITEM-${items.size + 1}",
                                                        fabricName = "",
                                                        colour = "",
                                                        gsm = 0,
                                                        width = 0.0,
                                                        lotNumber = "",
                                                        rollNumbers = emptyList(),
                                                        rollQty = 0,
                                                        weightKg = 0.0,
                                                        ratePerKg = 0.0,
                                                        discountPercent = 0.0,
                                                        amount = 0.0
                                                    )
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Add item row", tint = PureWhite, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Add Row", color = PureWhite, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    items.forEachIndexed { index, item ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, DarkSlateBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                                .background(OffWhite.copy(alpha = 0.3f))
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Item #${index + 1} (${item.id})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                                if (items.size > 1) {
                                                    IconButton(onClick = { items.removeAt(index) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Remove row", tint = WarningAlert, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Box(modifier = Modifier.weight(1.5f)) {
                                                    TradeFitTextField(
                                                        value = item.fabricName,
                                                        onValueChange = {
                                                            // Prefill other variables if selected
                                                            val matched = initialFabrics.firstOrNull { f -> f.name.equals(it, ignoreCase = true) }
                                                            items[index] = item.copy(
                                                                fabricName = it,
                                                                gsm = matched?.gsm ?: item.gsm,
                                                                width = matched?.width ?: item.width,
                                                                ratePerKg = matched?.ratePerKg ?: item.ratePerKg
                                                            )
                                                        },
                                                        label = "Fabric Name",
                                                        placeholder = "Type or select"
                                                    )
                                                    IconButton(
                                                        onClick = { activeItemIndexForSearch = index },
                                                        modifier = Modifier.align(Alignment.CenterEnd).padding(top = 16.dp)
                                                    ) {
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                                    }
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = item.colour,
                                                        onValueChange = { items[index] = item.copy(colour = it) },
                                                        label = "Colour",
                                                        placeholder = "e.g., Sky Blue"
                                                    )
                                                }
                                            }

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = if (item.gsm == 0) "" else item.gsm.toString(),
                                                        onValueChange = { items[index] = item.copy(gsm = it.toIntOrNull() ?: 0) },
                                                        label = "GSM",
                                                        placeholder = "e.g., 200"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = if (item.width == 0.0) "" else item.width.toString(),
                                                        onValueChange = { items[index] = item.copy(width = it.toDoubleOrNull() ?: 0.0) },
                                                        label = "Width (Inches)",
                                                        placeholder = "e.g., 58.0"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = item.lotNumber,
                                                        onValueChange = { items[index] = item.copy(lotNumber = it) },
                                                        label = "Lot Number",
                                                        placeholder = "e.g., LOT-101"
                                                    )
                                                }
                                            }

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Box(modifier = Modifier.weight(1.2f)) {
                                                    TradeFitTextField(
                                                        value = if (item.rollQty == 0) "" else item.rollQty.toString(),
                                                        onValueChange = {
                                                            val q = it.toIntOrNull() ?: 0
                                                            val rolls = (1..q).map { r -> "ROLL-${100 + index * 10 + r}" }
                                                            items[index] = item.copy(
                                                                rollQty = q,
                                                                rollNumbers = rolls
                                                            )
                                                        },
                                                        label = "Roll Qty",
                                                        placeholder = "No of rolls"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1.2f)) {
                                                    TradeFitTextField(
                                                        value = if (item.weightKg == 0.0) "" else item.weightKg.toString(),
                                                        onValueChange = {
                                                            val w = it.toDoubleOrNull() ?: 0.0
                                                            items[index] = item.copy(
                                                                weightKg = w,
                                                                amount = w * item.ratePerKg * (1 - item.discountPercent / 100.0)
                                                            )
                                                        },
                                                        label = "Weight (Kg) *",
                                                        placeholder = "0.0"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1.2f)) {
                                                    TradeFitTextField(
                                                        value = if (item.ratePerKg == 0.0) "" else item.ratePerKg.toString(),
                                                        onValueChange = {
                                                            val r = it.toDoubleOrNull() ?: 0.0
                                                            items[index] = item.copy(
                                                                ratePerKg = r,
                                                                amount = item.weightKg * r * (1 - item.discountPercent / 100.0)
                                                            )
                                                        },
                                                        label = "Rate/Kg *",
                                                        placeholder = "0.0"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = if (item.discountPercent == 0.0) "" else item.discountPercent.toString(),
                                                        onValueChange = {
                                                            val d = it.toDoubleOrNull() ?: 0.0
                                                            items[index] = item.copy(
                                                                discountPercent = d,
                                                                amount = item.weightKg * item.ratePerKg * (1 - d / 100.0)
                                                            )
                                                        },
                                                        label = "Disc %",
                                                        placeholder = "0"
                                                    )
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Calculated Line Item Valuation: ₹${String.format("%,.2f", item.amount)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SlateTextDark
                                                )
                                                if (item.rollNumbers.isNotEmpty()) {
                                                    Text(
                                                        text = "Rolls: ${item.rollNumbers.joinToString(", ")}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = ExecutiveBlue,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Math and financials card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("Pricing, Deductions & Taxes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = discountAmountStr, onValueChange = { discountAmountStr = it }, label = "Order Discount Amount (₹)", placeholder = "0.0")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = freightChargesStr, onValueChange = { freightChargesStr = it }, label = "Freight / Transport (₹)", placeholder = "0.0")
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = packingChargesStr, onValueChange = { packingChargesStr = it }, label = "Packing Charges (₹)", placeholder = "0.0")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = otherChargesStr, onValueChange = { otherChargesStr = it }, label = "Other Incidental Charges (₹)", placeholder = "0.0")
                                        }
                                    }

                                    HorizontalDivider(color = DarkSlateBorder)

                                    // Real-time calculations display
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        SummaryMathRow("Cumulative Total Roll Count:", "$totalRolls Rolls")
                                        SummaryMathRow("Cumulative Dispatch Weight:", "${String.format("%,.2f", totalWeight)} Kg")
                                        SummaryMathRow("Sub-Total Item Valuation (Excl. Tax):", "₹${String.format("%,.2f", subTotal)}")
                                        SummaryMathRow("Applied Discount Deduction:", "- ₹${String.format("%,.2f", discountAmt)}")
                                        SummaryMathRow("Logistics & Miscellaneous:", "+ ₹${String.format("%,.2f", freight + packing + other)}")
                                        SummaryMathRow("Net Taxable Asset Value:", "₹${String.format("%,.2f", taxableAmount)}")

                                        if (isInterstate) {
                                            SummaryMathRow("Interstate IGST (18%):", "₹${String.format("%,.2f", igstAmount)}")
                                        } else {
                                            SummaryMathRow("Intrastate CGST (9%):", "₹${String.format("%,.2f", cgstAmount)}")
                                            SummaryMathRow("Intrastate SGST (9%):", "₹${String.format("%,.2f", sgstAmount)}")
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(ExecutiveBlue.copy(alpha = 0.08f))
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Calculated Grand Total Quotation:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                            Text("₹${String.format("%,.2f", grandTotal)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = ExecutiveBlue)
                                        }
                                    }
                                }
                            }

                            // Remarks, terms, and prepared by
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Quotation Remarks & Authorizations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    TradeFitTextField(value = remarks, onValueChange = { remarks = it }, label = "Internal / Logistics Remarks", placeholder = "e.g., Deliver before evening shift")
                                    TradeFitTextField(value = termsAndConditions, onValueChange = { termsAndConditions = it }, label = "Quotation Specific Terms & Conditions", placeholder = "Standard company terms")
                                    TradeFitTextField(value = preparedBy, onValueChange = { preparedBy = it }, label = "Prepared By (Employee Signature)", placeholder = "Full name")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. Popup search dialog for Customers
    if (showCustomerSearch) {
        Dialog(onDismissRequest = { showCustomerSearch = false }) {
            Card(
                modifier = Modifier.size(500.dp, 600.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Customer Company", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = DarkSlateBorder)
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(initialCustomers) { cust ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OffWhite)
                                    .clickable {
                                        selectedCustId = cust.id
                                        companyName = cust.companyName
                                        gstNumber = cust.gstin
                                        mobile = cust.mobile
                                        billingAddress = cust.billingAddress
                                        shippingAddress = cust.shippingAddress
                                        transportName = cust.transportName
                                        showCustomerSearch = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(cust.companyName, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    Text("ID: ${cust.id} | GST: ${cust.gstin}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                }
                                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Select", tint = ExecutiveBlue, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Button(
                        onClick = { showCustomerSearch = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MatteBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = PureWhite)
                    }
                }
            }
        }
    }

    // 2. Popup search dialog for Fabrics (Items dropdown)
    activeItemIndexForSearch?.let { index ->
        Dialog(onDismissRequest = { activeItemIndexForSearch = null }) {
            Card(
                modifier = Modifier.size(450.dp, 500.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Premium Fabric Grade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = DarkSlateBorder)
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(initialFabrics) { fab ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OffWhite)
                                    .clickable {
                                        val current = items[index]
                                        items[index] = current.copy(
                                            fabricName = fab.name,
                                            gsm = fab.gsm,
                                            width = fab.width,
                                            ratePerKg = fab.ratePerKg,
                                            amount = current.weightKg * fab.ratePerKg * (1 - current.discountPercent / 100.0)
                                        )
                                        activeItemIndexForSearch = null
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(fab.name, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    Text("GSM: ${fab.gsm} | Width: ${fab.width}\" | Catalog Rate: ₹${fab.ratePerKg}/Kg", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                }
                                Icon(Icons.Default.Add, contentDescription = "Pick Fabric", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Button(
                        onClick = { activeItemIndexForSearch = null },
                        colors = ButtonDefaults.buttonColors(containerColor = MatteBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = PureWhite)
                    }
                }
            }
        }
    }

    // 3. Popup search dialog for existing Delivery Challans to convert
    if (showChallanSearch) {
        Dialog(onDismissRequest = { showChallanSearch = false }) {
            Card(
                modifier = Modifier.size(600.dp, 550.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Delivery Challan for Conversion", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Selecting a challan will overwrite current customer and item grid inputs.", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                    HorizontalDivider(color = DarkSlateBorder)
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val availableChallans = TradeFitSharedState.deliveryChallans.filter { it.status != "Cancelled" }
                        if (availableChallans.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No active Delivery Challans found.", color = SlateTextDark)
                                }
                            }
                        } else {
                            items(availableChallans) { ch ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OffWhite)
                                        .clickable {
                                            selectedCustId = ch.customerId
                                            companyName = ch.companyName
                                            gstNumber = ch.gstNumber
                                            mobile = ch.mobile
                                            billingAddress = ch.billingAddress
                                            shippingAddress = ch.shippingAddress
                                            transportName = ch.transportName
                                            remarks = ch.remarks
                                            
                                            // Overwrite items
                                            items.clear()
                                            ch.items.forEachIndexed { idx, it ->
                                                items.add(
                                                    ProformaInvoiceItem(
                                                        id = "PI-ITEM-${idx + 1}",
                                                        fabricName = it.fabricName,
                                                        colour = it.colour,
                                                        gsm = it.gsm,
                                                        width = it.width,
                                                        lotNumber = it.lotNumber,
                                                        rollNumbers = it.rollNumbers,
                                                        rollQty = it.numberOfRolls,
                                                        weightKg = it.weightKg,
                                                        ratePerKg = it.ratePerKg,
                                                        discountPercent = 0.0,
                                                        amount = it.amount
                                                    )
                                                )
                                            }
                                            showChallanSearch = false
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${ch.id} - ${ch.companyName}", fontWeight = FontWeight.Bold, color = MatteBlack)
                                        Text("Date: ${ch.date} | ${ch.totalRolls} Rolls / ${ch.totalWeight} Kg | Val: ₹${ch.grandTotal}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                    }
                                    Icon(Icons.Default.Cached, contentDescription = "Convert", tint = PositiveGrowth, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { showChallanSearch = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MatteBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = PureWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun ProformaPreviewSheet(
    proforma: ProformaInvoice,
    onDismiss: () -> Unit,
    onDownloadPdf: () -> Unit,
    onSharePdf: () -> Unit,
    onConvertToTaxInvoice: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(40.dp)
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .testTag("proforma_preview_modal"),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(12.dp),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Modal Topbar Action Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OffWhite)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Proforma Invoice Preview - ${proforma.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MatteBlack
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = onSharePdf) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = SlateTextDark)
                            }
                            IconButton(onClick = onDownloadPdf) {
                                Icon(Icons.Default.Download, contentDescription = "Download PDF", tint = SlateTextDark)
                            }
                            IconButton(onClick = { /* Trigger system print dialog mock */ }) {
                                Icon(Icons.Default.Print, contentDescription = "Trigger Print", tint = SlateTextDark)
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))

                            Button(
                                onClick = onConvertToTaxInvoice,
                                colors = ButtonDefaults.buttonColors(containerColor = PositiveGrowth)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Convert", tint = PureWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Convert to Tax Invoice", color = PureWhite, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = MatteBlack)
                            ) {
                                Text("Close", color = PureWhite)
                            }
                        }
                    }

                    HorizontalDivider(color = DarkSlateBorder)

                    // Printable Sheet Scroll Area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Letterhead header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("TRADEFIT INDUSTRIES", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = ExecutiveBlue)
                                Text("Premium Apparel & Activewear Fabrics Manufacturer", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, fontWeight = FontWeight.SemiBold)
                                Text("Plot 12A, Textile Center, Ring Road, Surat, Gujarat - 395002", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                                Text("GSTIN: 24AAACT9012F1ZK | Contact: +91 261 4001234", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ExecutiveBlue.copy(alpha = 0.12f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("PROFORMA INVOICE", style = MaterialTheme.typography.titleMedium, color = ExecutiveBlue, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Draft / Quotation Statement", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, fontWeight = FontWeight.Medium)
                            }
                        }

                        HorizontalDivider(color = DarkSlateBorder.copy(alpha = 0.5f))

                        // Document details (Invoice No, dates, ref)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = OffWhite.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, DarkSlateBorder.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("INVOICE IDENTIFICATION", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    DetailItemRow("Proforma Invoice No:", proforma.id)
                                    DetailItemRow("Document Date:", proforma.date)
                                    DetailItemRow("Validity Due Date:", proforma.validityDate)
                                    DetailItemRow("Buyer Ref Number:", proforma.referenceNumber.ifEmpty { "N/A" })
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1.2f),
                                colors = CardDefaults.cardColors(containerColor = OffWhite.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, DarkSlateBorder.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("BUYER / CONSIGNEE DETAILS", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    DetailItemRow("Billing Name:", proforma.companyName)
                                    DetailItemRow("Buyer GSTIN:", proforma.gstNumber)
                                    DetailItemRow("Contact Mobile:", proforma.mobile)
                                    DetailItemRow("Shipping Address:", proforma.shippingAddress)
                                }
                            }
                        }

                        // Grid Table of Items
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, DarkSlateBorder)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Table headers
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(OffWhite)
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.3f))
                                    Text("Fabric Description", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.5f))
                                    Text("Colour", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                    Text("GSM / Width", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                                    Text("Lot No", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                    Text("Rolls", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.6f))
                                    Text("Weight (Kg)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                                    Text("Rate/Kg", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                    Text("Disc %", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.6f))
                                    Text("Amount (₹)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.2f))
                                }

                                HorizontalDivider(color = DarkSlateBorder)

                                proforma.items.forEachIndexed { idx, it ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${idx + 1}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.3f))
                                        Text(it.fabricName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(1.5f))
                                        Text(it.colour, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(0.8f))
                                        Text("${it.gsm} / ${it.width}\"", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                                        Text(it.lotNumber, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                                        Text("${it.rollQty}", style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.6f))
                                        Text("${it.weightKg} Kg", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(1f))
                                        Text("₹${it.ratePerKg}", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(0.8f))
                                        Text("${it.discountPercent}%", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.6f))
                                        Text("₹${String.format("%,.2f", it.amount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(1.2f))
                                    }
                                    HorizontalDivider(color = DarkSlateBorder.copy(alpha = 0.3f))
                                }
                            }
                        }

                        // Calculations, bank details, QR code layout in two columns
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Left Side: Terms and QR Code
                            Column(
                                modifier = Modifier.weight(1.2f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                                    border = BorderStroke(1.dp, DarkSlateBorder.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("PRE-SET COMPANY TERMS & CONDITIONS", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                        Text(proforma.termsAndConditions, style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Bank Details
                                    Card(
                                        modifier = Modifier.weight(1.2f),
                                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                                        border = BorderStroke(1.dp, DarkSlateBorder.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("OFFICIAL BANK TRANSFER", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                            Text(proforma.bankDetails, style = MaterialTheme.typography.bodySmall, color = SlateTextLight, fontSize = 11.sp, lineHeight = 14.sp)
                                        }
                                    }

                                    // UPI QR code placeholder
                                    Card(
                                        modifier = Modifier.weight(0.8f),
                                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                                        border = BorderStroke(1.dp, DarkSlateBorder.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Text("SCAN UPI TO PAY", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateTextDark, fontSize = 9.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            // Mock drawing a QR code using rows/columns
                                            Column(modifier = Modifier.size(60.dp).background(Color.White).padding(2.dp)) {
                                                repeat(5) { r ->
                                                    Row {
                                                        repeat(5) { c ->
                                                            val isBlack = (r + c) % 2 == 0 || (r == 0 && c == 0) || (r == 0 && c == 4) || (r == 4 && c == 0)
                                                            Box(modifier = Modifier.weight(1f).aspectRatio(1f).background(if (isBlack) Color.Black else Color.White))
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(proforma.upiId, style = MaterialTheme.typography.bodySmall, color = ExecutiveBlue, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }

                            // Right Side: Calculations block
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = OffWhite.copy(alpha = 0.3f)),
                                border = BorderStroke(1.dp, DarkSlateBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("SUMMARY COMPUTATIONS", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    DetailItemRow("Total Roll Count:", "${proforma.totalRolls} Rolls")
                                    DetailItemRow("Total Fabric Weight:", "${String.format("%,.2f", proforma.totalWeight)} Kg")
                                    
                                    HorizontalDivider(color = DarkSlateBorder.copy(alpha = 0.3f))
                                    
                                    DetailItemRow("Item Subtotal Value:", "₹${String.format("%,.2f", proforma.subTotal)}")
                                    DetailItemRow("Applied Order Discount:", "- ₹${String.format("%,.2f", proforma.discountAmount)}")
                                    DetailItemRow("Logistics (Freight/Packing):", "₹${String.format("%,.2f", proforma.freightCharges + proforma.packingCharges + proforma.otherCharges)}")
                                    DetailItemRow("Net Taxable Asset Value:", "₹${String.format("%,.2f", proforma.taxableAmount)}")

                                    val hasIgst = proforma.igstAmount > 0.0
                                    if (hasIgst) {
                                        DetailItemRow("Interstate IGST (18%):", "₹${String.format("%,.2f", proforma.igstAmount)}")
                                    } else {
                                        DetailItemRow("CGST (9%):", "₹${String.format("%,.2f", proforma.cgstAmount)}")
                                        DetailItemRow("SGST (9%):", "₹${String.format("%,.2f", proforma.sgstAmount)}")
                                    }

                                    HorizontalDivider(color = DarkSlateBorder)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Calculated Grand Total:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                        Text("₹${String.format("%,.2f", proforma.grandTotal)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = ExecutiveBlue)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Signature and Sign-offs lines
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Prepared By:", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                                Spacer(modifier = Modifier.height(30.dp))
                                Text(proforma.preparedBy.ifEmpty { "Accounts Representative" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                HorizontalDivider(color = MatteBlack, modifier = Modifier.width(150.dp), thickness = 1.dp)
                                Text("Employee Signature & Seal", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Authorized Sign-off:", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                                Spacer(modifier = Modifier.height(30.dp))
                                Text(proforma.authorizedSignature, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                HorizontalDivider(color = MatteBlack, modifier = Modifier.width(180.dp), thickness = 1.dp)
                                Text("Manager Authorized Stamp & Seal", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                            }
                        }
                    }
                }
            }
        }
    }
}
