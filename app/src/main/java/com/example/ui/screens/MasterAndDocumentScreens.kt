package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

// ==========================================
// 3. CUSTOMER MASTER SCREEN (PRODUCTION-READY)
// ==========================================

data class Customer(
    val id: String,
    val companyName: String,
    val customerName: String,
    val gstin: String,
    val pan: String,
    val mobile: String,
    val whatsapp: String,
    val email: String,
    val billingAddress: String,
    val shippingAddress: String,
    val city: String,
    val state: String,
    val pincode: String,
    val country: String,
    val placeOfSupply: String,
    val contactPerson: String,
    val creditDays: Int,
    val creditLimit: Double,
    val openingBalance: Double,
    val paymentTerms: String,
    val transportName: String,
    val remark: String,
    val status: String, // "Active" / "Inactive"
    val outstandingAmount: Double
)

val initialCustomers = listOf(
    Customer(
        id = "CUST-1001",
        companyName = "Evergreen Textiles Ltd.",
        customerName = "Amit Sharma",
        gstin = "27AAACE1234F1Z8",
        pan = "AAACE1234F",
        mobile = "9876543210",
        whatsapp = "9876543210",
        email = "amit@evergreentextiles.com",
        billingAddress = "102, Nariman Point, Industrial Area",
        shippingAddress = "Plot 45, GIDC Estate, Sachin",
        city = "Mumbai",
        state = "Maharashtra",
        pincode = "400021",
        country = "India",
        placeOfSupply = "27-Maharashtra",
        contactPerson = "Mr. Amit Sharma",
        creditDays = 45,
        creditLimit = 500000.0,
        openingBalance = 120000.0,
        paymentTerms = "Net 45 Days",
        transportName = "VRL Logistics",
        remark = "Regular customer, premium quality focus",
        status = "Active",
        outstandingAmount = 145000.0
    ),
    Customer(
        id = "CUST-1002",
        companyName = "Maharani Silks & Fabrics",
        customerName = "Pooja Gupta",
        gstin = "07BBBDF5678A2Z3",
        pan = "BBBDF5678A",
        mobile = "8765432109",
        whatsapp = "8765432109",
        email = "info@maharanisilks.com",
        billingAddress = "B-4, Chandni Chowk Main Road",
        shippingAddress = "B-4, Chandni Chowk Main Road",
        city = "Delhi",
        state = "Delhi",
        pincode = "110006",
        country = "India",
        placeOfSupply = "07-Delhi",
        contactPerson = "Mrs. Pooja Gupta",
        creditDays = 30,
        creditLimit = 300000.0,
        openingBalance = 50000.0,
        paymentTerms = "Net 30 Days",
        transportName = "Safe Express",
        remark = "Prompt payer, requires delivery challan copies",
        status = "Active",
        outstandingAmount = 75000.0
    ),
    Customer(
        id = "CUST-1003",
        companyName = "Vardhman Hosiery Co.",
        customerName = "Rajesh Jain",
        gstin = "03CCCEG9012B3Z4",
        pan = "CCCEG9012B",
        mobile = "7654321098",
        whatsapp = "7654321098",
        email = "rajesh@vardhmanhosiery.com",
        billingAddress = "Industrial Focal Point, Phase V",
        shippingAddress = "Industrial Focal Point, Phase V",
        city = "Ludhiana",
        state = "Punjab",
        pincode = "141010",
        country = "India",
        placeOfSupply = "03-Punjab",
        contactPerson = "Mr. Rajesh Jain",
        creditDays = 60,
        creditLimit = 800000.0,
        openingBalance = 210000.0,
        paymentTerms = "Net 60 Days",
        transportName = "ARC Carriers",
        remark = "Bulk purchaser, seasonal fluctuations",
        status = "Active",
        outstandingAmount = 210000.0
    ),
    Customer(
        id = "CUST-1004",
        companyName = "Royal Garments House",
        customerName = "Vikram Singh",
        gstin = "33DDDFH4567C4Z5",
        pan = "DDDFH4567C",
        mobile = "9988776655",
        whatsapp = "9988776655",
        email = "vikram@royalgarments.in",
        billingAddress = "Johari Bazar, Opp HDFC Bank",
        shippingAddress = "Johari Bazar, Opp HDFC Bank",
        city = "Jaipur",
        state = "Rajasthan",
        pincode = "302003",
        country = "India",
        placeOfSupply = "08-Rajasthan",
        contactPerson = "Mr. Vikram Singh",
        creditDays = 15,
        creditLimit = 150000.0,
        openingBalance = 0.0,
        paymentTerms = "Immediate or Net 15",
        transportName = "Jaipur Golden",
        remark = "Recently updated contact info",
        status = "Inactive",
        outstandingAmount = 32500.0
    )
)

fun isValidGST(gst: String): Boolean {
    val pattern = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$".toRegex()
    return pattern.matches(gst)
}

fun generateNextCustomerCode(existing: List<Customer>): String {
    val maxNum = existing.mapNotNull { customer ->
        customer.id.substringAfter("CUST-").toIntOrNull()
    }.maxOrNull() ?: 1000
    return "CUST-${maxNum + 1}"
}

@Composable
fun CustomerMasterScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedStateFilter by remember { mutableStateOf("All States") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var selectedCreditDaysFilter by remember { mutableStateOf("All Credit Days") }
    
    val customerList = remember { mutableStateListOf<Customer>().apply { addAll(initialCustomers) } }
    
    var showFormModal by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    var viewingCustomer by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }
    
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    var sortColumn by remember { mutableStateOf("code") }
    var sortAscending by remember { mutableStateOf(true) }
    
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5
    
    var isAdvancedFilterVisible by remember { mutableStateOf(false) }

    // Simulation of delay when filtering or searching to show loading skeletons
    LaunchedEffect(searchKeyword, selectedStateFilter, selectedStatusFilter, selectedCreditDaysFilter, sortColumn, sortAscending) {
        isLoading = true
        delay(350)
        isLoading = false
        currentPage = 1
    }

    // Apply Searching & Filtering
    val filteredCustomers = customerList.filter { customer ->
        val matchesSearch = searchKeyword.isEmpty() ||
                customer.companyName.contains(searchKeyword, ignoreCase = true) ||
                customer.customerName.contains(searchKeyword, ignoreCase = true) ||
                customer.mobile.contains(searchKeyword, ignoreCase = true) ||
                customer.gstin.contains(searchKeyword, ignoreCase = true)
        
        val matchesState = selectedStateFilter == "All States" || customer.state.equals(selectedStateFilter, ignoreCase = true)
        val matchesStatus = selectedStatusFilter == "All" || customer.status.equals(selectedStatusFilter, ignoreCase = true)
        
        val matchesCredit = when (selectedCreditDaysFilter) {
            "All Credit Days" -> true
            "≤ 30 Days" -> customer.creditDays <= 30
            "31 - 45 Days" -> customer.creditDays in 31..45
            "46 - 60 Days" -> customer.creditDays in 46..60
            "Over 60 Days" -> customer.creditDays > 60
            else -> true
        }
        
        matchesSearch && matchesState && matchesStatus && matchesCredit
    }

    // Apply Sorting
    val sortedCustomers = remember(filteredCustomers, sortColumn, sortAscending) {
        val comparator = when (sortColumn) {
            "code" -> compareBy<Customer> { it.id }
            "company" -> compareBy<Customer> { it.companyName }
            "credit" -> compareBy<Customer> { it.creditDays }
            "outstanding" -> compareBy<Customer> { it.outstandingAmount }
            "status" -> compareBy<Customer> { it.status }
            else -> compareBy<Customer> { it.id }
        }
        if (sortAscending) filteredCustomers.sortedWith(comparator) else filteredCustomers.sortedWith(comparator).reversed()
    }

    // Apply Pagination
    val totalPages = (sortedCustomers.size + itemsPerPage - 1) / itemsPerPage
    val coercedTotalPages = totalPages.coerceAtLeast(1)
    val paginatedCustomers = sortedCustomers.drop((currentPage - 1) * itemsPerPage).take(itemsPerPage)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(16.dp)
                .testTag("customer_master_root"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Breadcrumb
            item {
                Breadcrumb(path = listOf("Home", "Directory", "Customers"))
            }

            // 2. Title and Primary Controls Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Customer Master",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MatteBlack,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Buttons Scrollable Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                editingCustomer = null
                                showFormModal = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Customer", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Import Customer excel database initialized..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Exporting to Excel format (.xlsx)..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Excel", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Generating encrypted customer PDF statement..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Sending print command to spooler..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Search and Advanced Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchComponent(
                            query = searchKeyword,
                            onQueryChange = { searchKeyword = it },
                            placeholder = "Search by Name, Mobile or GST number..."
                        )
                    }
                    OutlinedButton(
                        onClick = { isAdvancedFilterVisible = !isAdvancedFilterVisible },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ExecutiveBlue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isAdvancedFilterVisible) LightBlueAccent else PureWhite,
                            contentColor = ExecutiveBlue
                        )
                    ) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Advanced Filter", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. Advanced Filter Expanded Card
            item {
                if (isAdvancedFilterVisible) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Advanced Directory Filters", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Filter by State", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                FilterComponent(
                                    selectedFilter = selectedStateFilter,
                                    availableFilters = listOf("All States", "Maharashtra", "Delhi", "Punjab", "Rajasthan", "Gujarat"),
                                    onFilterSelected = { selectedStateFilter = it }
                                )
                            }
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Filter by Status", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                FilterComponent(
                                    selectedFilter = selectedStatusFilter,
                                    availableFilters = listOf("All", "Active", "Inactive"),
                                    onFilterSelected = { selectedStatusFilter = it }
                                )
                            }
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Filter by Credit Terms", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                FilterComponent(
                                    selectedFilter = selectedCreditDaysFilter,
                                    availableFilters = listOf("All Credit Days", "≤ 30 Days", "31 - 45 Days", "46 - 60 Days", "Over 60 Days"),
                                    onFilterSelected = { selectedCreditDaysFilter = it }
                                )
                            }
                        }
                    }
                }
            }

            // 5. Scrollable Customers Table
            item {
                ScrollableCustomerTable(
                    customers = paginatedCustomers,
                    isLoading = isLoading,
                    sortColumn = sortColumn,
                    sortAscending = sortAscending,
                    onSort = { selectedCol ->
                        if (sortColumn == selectedCol) {
                            sortAscending = !sortAscending
                        } else {
                            sortColumn = selectedCol
                            sortAscending = true
                        }
                    },
                    onView = { viewingCustomer = it },
                    onEdit = {
                        editingCustomer = it
                        showFormModal = true
                    },
                    onDelete = { customerToDelete = it }
                )
            }

            // 6. Pagination Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val startIdx = if (sortedCustomers.isEmpty()) 0 else (currentPage - 1) * itemsPerPage + 1
                    val endIdx = (currentPage * itemsPerPage).coerceAtMost(sortedCustomers.size)
                    
                    Text(
                        text = "Showing $startIdx-$endIdx of ${sortedCustomers.size} customers",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextDark
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            enabled = currentPage > 1,
                            onClick = { currentPage -= 1 },
                            modifier = Modifier
                                .border(1.dp, if (currentPage > 1) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Page", tint = if (currentPage > 1) ExecutiveBlue else SlateTextLight)
                        }
                        
                        IconButton(
                            enabled = currentPage < coercedTotalPages,
                            onClick = { currentPage += 1 },
                            modifier = Modifier
                                .border(1.dp, if (currentPage < coercedTotalPages) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Next Page", tint = if (currentPage < coercedTotalPages) ExecutiveBlue else SlateTextLight)
                        }
                    }
                }
            }
        }

        // Toasts
        toastMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                NotificationToast(message = msg, onDismiss = { toastMessage = null })
            }
        }

        // Add / Edit Modal
        if (showFormModal) {
            CustomerFormModal(
                customer = editingCustomer,
                existingCustomers = customerList,
                onDismiss = { showFormModal = false },
                onSave = { savedCustomer, keepOpen ->
                    if (editingCustomer != null) {
                        // Editing existing
                        val index = customerList.indexOfFirst { it.id == savedCustomer.id }
                        if (index != -1) {
                            customerList[index] = savedCustomer
                        }
                        toastMessage = "Customer profile updated successfully!"
                    } else {
                        // Creating new
                        customerList.add(savedCustomer)
                        toastMessage = "New customer ${savedCustomer.companyName} registered successfully!"
                    }
                    
                    if (keepOpen) {
                        editingCustomer = null // Reset for next entries in "Save & New"
                    } else {
                        showFormModal = false
                    }
                }
            )
        }

        // View Details Drawer/Modal
        viewingCustomer?.let { customer ->
            CustomerDetailsModal(
                customer = customer,
                onDismiss = { viewingCustomer = null }
            )
        }

        // Delete Confirmation
        customerToDelete?.let { customer ->
            ConfirmationDialog(
                title = "Delete Customer Profile",
                message = "Are you absolutely sure you want to permanently delete the master profile for ${customer.companyName}? This action is legal and irreversible.",
                confirmLabel = "Delete Record",
                dismissLabel = "Keep Record",
                onConfirm = {
                    customerList.remove(customer)
                    toastMessage = "Deleted customer profile ${customer.companyName}"
                    customerToDelete = null
                },
                onDismiss = { customerToDelete = null }
            )
        }
    }
}

@Composable
fun ScrollableCustomerTable(
    customers: List<Customer>,
    isLoading: Boolean,
    sortColumn: String,
    sortAscending: Boolean,
    onSort: (String) -> Unit,
    onView: (Customer) -> Unit,
    onEdit: (Customer) -> Unit,
    onDelete: (Customer) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column(modifier = Modifier.width(1720.dp)) {
                    // Table Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OffWhite)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SortableHeaderCell("Code", "code", sortColumn, sortAscending, onSort, Modifier.width(110.dp))
                        SortableHeaderCell("Company Name", "company", sortColumn, sortAscending, onSort, Modifier.width(220.dp))
                        HeaderCell("Customer Name", Modifier.width(140.dp))
                        HeaderCell("GSTIN No.", Modifier.width(150.dp))
                        HeaderCell("Mobile", Modifier.width(110.dp))
                        HeaderCell("WhatsApp", Modifier.width(110.dp))
                        HeaderCell("Email ID", Modifier.width(160.dp))
                        HeaderCell("State", Modifier.width(120.dp))
                        HeaderCell("City", Modifier.width(110.dp))
                        SortableHeaderCell("Credit Days", "credit", sortColumn, sortAscending, onSort, Modifier.width(110.dp))
                        SortableHeaderCell("Outstanding", "outstanding", sortColumn, sortAscending, onSort, Modifier.width(130.dp))
                        SortableHeaderCell("Status", "status", sortColumn, sortAscending, onSort, Modifier.width(100.dp))
                        HeaderCell("Actions", Modifier.width(110.dp))
                    }
                    Divider(color = DarkSlateBorder, thickness = 1.dp)
                    
                    if (isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(4) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(2.5f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1.5f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1.5f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1f))
                                }
                            }
                        }
                    } else if (customers.isEmpty()) {
                        EmptyState(
                            title = "No Customers Registered",
                            message = "No customer profiles match the current filter criteria",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp)
                        )
                    } else {
                        customers.forEach { customer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(customer.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = ExecutiveBlue, modifier = Modifier.width(110.dp))
                                Text(customer.companyName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MatteBlack, modifier = Modifier.width(220.dp))
                                Text(customer.customerName, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(140.dp))
                                Text(customer.gstin.ifEmpty { "N/A" }, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(150.dp))
                                Text(customer.mobile, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(110.dp))
                                Text(customer.whatsapp, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(110.dp))
                                Text(customer.email.ifEmpty { "N/A" }, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(160.dp))
                                Text(customer.state, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(120.dp))
                                Text(customer.city, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(110.dp))
                                Text("${customer.creditDays} Days", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(110.dp))
                                Text("₹${customer.outstandingAmount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (customer.outstandingAmount > 0) WarningAlert else MatteBlack, modifier = Modifier.width(130.dp))
                                
                                Box(modifier = Modifier.width(100.dp), contentAlignment = Alignment.CenterStart) {
                                    StatusBadge(
                                        status = customer.status,
                                        color = if (customer.status == "Active") PositiveGrowth else SlateTextLight
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.width(110.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { onView(customer) }, modifier = Modifier.size(28.dp).testTag("btn_view_${customer.id}")) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = "View Details", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = { onEdit(customer) }, modifier = Modifier.size(28.dp).testTag("btn_edit_${customer.id}")) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Customer", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = { onDelete(customer) }, modifier = Modifier.size(28.dp).testTag("btn_delete_${customer.id}")) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Customer", tint = WarningAlert, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Divider(color = DarkSlateBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortableHeaderCell(
    label: String,
    columnKey: String,
    currentSortColumn: String,
    sortAscending: Boolean,
    onSort: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onSort(columnKey) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (currentSortColumn == columnKey) ExecutiveBlue else SlateTextDark,
            fontWeight = FontWeight.Bold
        )
        if (currentSortColumn == columnKey) {
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = if (sortAscending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = ExecutiveBlue,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun HeaderCell(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = SlateTextDark,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormModal(
    customer: Customer?,
    existingCustomers: List<Customer>,
    onDismiss: () -> Unit,
    onSave: (Customer, Boolean) -> Unit
) {
    // Generate code if adding new
    val finalCode = remember { customer?.id ?: generateNextCustomerCode(existingCustomers) }
    
    var companyName by remember { mutableStateOf(customer?.companyName ?: "") }
    var customerName by remember { mutableStateOf(customer?.customerName ?: "") }
    var gstin by remember { mutableStateOf(customer?.gstin ?: "") }
    var pan by remember { mutableStateOf(customer?.pan ?: "") }
    var mobile by remember { mutableStateOf(customer?.mobile ?: "") }
    var whatsapp by remember { mutableStateOf(customer?.whatsapp ?: "") }
    var email by remember { mutableStateOf(customer?.email ?: "") }
    var billingAddress by remember { mutableStateOf(customer?.billingAddress ?: "") }
    var shippingAddress by remember { mutableStateOf(customer?.shippingAddress ?: "") }
    var city by remember { mutableStateOf(customer?.city ?: "") }
    var state by remember { mutableStateOf(customer?.state ?: "Maharashtra") }
    var pincode by remember { mutableStateOf(customer?.pincode ?: "") }
    var country by remember { mutableStateOf(customer?.country ?: "India") }
    var placeOfSupply by remember { mutableStateOf(customer?.placeOfSupply ?: "27-Maharashtra") }
    var contactPerson by remember { mutableStateOf(customer?.contactPerson ?: "") }
    var creditDays by remember { mutableStateOf(customer?.creditDays?.toString() ?: "30") }
    var creditLimit by remember { mutableStateOf(customer?.creditLimit?.toString() ?: "200000") }
    var openingBalance by remember { mutableStateOf(customer?.openingBalance?.toString() ?: "0") }
    var paymentTerms by remember { mutableStateOf(customer?.paymentTerms ?: "Net 30 Days") }
    var transportName by remember { mutableStateOf(customer?.transportName ?: "") }
    var remark by remember { mutableStateOf(customer?.remark ?: "") }
    var status by remember { mutableStateOf(customer?.status ?: "Active") }

    // Client-side warning indicators
    val isGstInvalid = gstin.isNotEmpty() && !isValidGST(gstin)
    val hasDuplicateMobile = mobile.isNotEmpty() && existingCustomers.any { it.mobile == mobile && it.id != customer?.id }
    val hasDuplicateGst = gstin.isNotEmpty() && existingCustomers.any { it.gstin == gstin && it.id != customer?.id }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OffWhite
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back", tint = MatteBlack)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (customer != null) "Edit Customer Profile" else "Create Customer Master",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatteBlack
                            )
                            Text(
                                text = "Customer Code: $finalCode",
                                style = MaterialTheme.typography.labelSmall,
                                color = ExecutiveBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close form", tint = MatteBlack)
                    }
                }
                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)

                // Scrollable content area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. General & Identification Info
                    FormSection(title = "General & Identification Info") {
                        ResponsiveFormField(
                            label = "Company Name *",
                            value = companyName,
                            onValueChange = { companyName = it },
                            placeholder = "e.g., Evergreen Textiles Ltd."
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "Customer Name *",
                                value = customerName,
                                onValueChange = { customerName = it },
                                placeholder = "e.g., Amit Sharma",
                                modifier = Modifier.weight(1f)
                            )
                            ResponsiveFormField(
                                label = "Contact Person",
                                value = contactPerson,
                                onValueChange = { contactPerson = it },
                                placeholder = "e.g., Representative Name",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "GST Number",
                                value = gstin,
                                onValueChange = { gstin = it.uppercase() },
                                placeholder = "15-digit GSTIN (e.g., 27AAACE1234F1Z8)",
                                isError = isGstInvalid,
                                warningText = if (isGstInvalid) "Invalid GSTIN format" else if (hasDuplicateGst) "⚠️ Warning: Duplicate GSTIN" else null,
                                modifier = Modifier.weight(1.2f)
                            )
                            ResponsiveFormField(
                                label = "PAN Number",
                                value = pan,
                                onValueChange = { pan = it.uppercase() },
                                placeholder = "10-digit PAN (e.g., AAACE1234F)",
                                modifier = Modifier.weight(0.8f)
                            )
                        }

                        // Status Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Account Status State", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MatteBlack)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val statusOptions = listOf("Active", "Inactive")
                                statusOptions.forEach { opt ->
                                    val isSel = status == opt
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) ExecutiveBlue else PureWhite)
                                            .border(1.dp, if (isSel) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                            .clickable { status = opt }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(opt, color = if (isSel) PureWhite else SlateTextDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 2. Contact & Communication
                    FormSection(title = "Contact & Communication") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "Mobile Number *",
                                value = mobile,
                                onValueChange = { mobile = it },
                                placeholder = "10-digit mobile number",
                                warningText = if (hasDuplicateMobile) "⚠️ Warning: Duplicate mobile" else null,
                                modifier = Modifier.weight(1f)
                            )
                            ResponsiveFormField(
                                label = "WhatsApp Number",
                                value = whatsapp,
                                onValueChange = { whatsapp = it },
                                placeholder = "WhatsApp contact number",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        ResponsiveFormField(
                            label = "Email Address",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "e.g., office@evergreen.com"
                        )
                    }

                    // 3. Billing & Delivery Address
                    FormSection(title = "Billing & Delivery Address") {
                        ResponsiveFormField(
                            label = "Billing Address",
                            value = billingAddress,
                            onValueChange = { billingAddress = it },
                            placeholder = "Legal Billing Address details"
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { shippingAddress = billingAddress }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy address", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Billing to Shipping Address", color = ExecutiveBlue, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }

                        ResponsiveFormField(
                            label = "Shipping Address",
                            value = shippingAddress,
                            onValueChange = { shippingAddress = it },
                            placeholder = "Goods Delivery Address details"
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "City",
                                value = city,
                                onValueChange = { city = it },
                                placeholder = "e.g., Surat",
                                modifier = Modifier.weight(1f)
                            )
                            ResponsiveFormField(
                                label = "State",
                                value = state,
                                onValueChange = { state = it },
                                placeholder = "e.g., Gujarat",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "Pincode",
                                value = pincode,
                                onValueChange = { pincode = it },
                                placeholder = "6-digit ZIP code",
                                modifier = Modifier.weight(1f)
                            )
                            ResponsiveFormField(
                                label = "Country",
                                value = country,
                                onValueChange = { country = it },
                                placeholder = "e.g., India",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        ResponsiveFormField(
                            label = "Place of Supply",
                            value = placeOfSupply,
                            onValueChange = { placeOfSupply = it },
                            placeholder = "State code & name (e.g., 24-Gujarat)"
                        )
                    }

                    // 4. Credit Terms & Financials
                    FormSection(title = "Credit Terms & Financials") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "Credit Days (Int)",
                                value = creditDays,
                                onValueChange = { creditDays = it },
                                placeholder = "e.g., 30",
                                modifier = Modifier.weight(1f)
                            )
                            ResponsiveFormField(
                                label = "Credit Limit (INR)",
                                value = creditLimit,
                                onValueChange = { creditLimit = it },
                                placeholder = "e.g., 500000",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ResponsiveFormField(
                                label = "Opening Balance",
                                value = openingBalance,
                                onValueChange = { openingBalance = it },
                                placeholder = "e.g., 0.00",
                                modifier = Modifier.weight(1f)
                            )
                            ResponsiveFormField(
                                label = "Payment Terms",
                                value = paymentTerms,
                                onValueChange = { paymentTerms = it },
                                placeholder = "e.g., Net 30 days, Net 45 days",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 5. Logistics & Remarks
                    FormSection(title = "Logistics & Remarks") {
                        ResponsiveFormField(
                            label = "Transport Name / Preferred Agent",
                            value = transportName,
                            onValueChange = { transportName = it },
                            placeholder = "e.g., VRL Transport, ARC Carriers"
                        )
                        
                        ResponsiveFormField(
                            label = "System / Business Remark",
                            value = remark,
                            onValueChange = { remark = it },
                            placeholder = "Additional operational details or tags..."
                        )
                    }
                }

                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)
                // Footer Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = SlateTextDark)
                    }

                    val isFormValid = companyName.isNotEmpty() && customerName.isNotEmpty() && mobile.isNotEmpty()

                    // Save Button
                    Button(
                        onClick = {
                            val creditDaysInt = creditDays.toIntOrNull() ?: 30
                            val creditLimitVal = creditLimit.toDoubleOrNull() ?: 200000.0
                            val openingBalanceVal = openingBalance.toDoubleOrNull() ?: 0.0
                            val mockOutstandingVal = if (customer != null) customer.outstandingAmount else openingBalanceVal
                            
                            val freshCustomer = Customer(
                                id = finalCode,
                                companyName = companyName,
                                customerName = customerName,
                                gstin = gstin,
                                pan = pan,
                                mobile = mobile,
                                whatsapp = whatsapp,
                                email = email,
                                billingAddress = billingAddress,
                                shippingAddress = shippingAddress,
                                city = city,
                                state = state,
                                pincode = pincode,
                                country = country,
                                placeOfSupply = placeOfSupply,
                                contactPerson = contactPerson,
                                creditDays = creditDaysInt,
                                creditLimit = creditLimitVal,
                                openingBalance = openingBalanceVal,
                                paymentTerms = paymentTerms,
                                transportName = transportName,
                                remark = remark,
                                status = status,
                                outstandingAmount = mockOutstandingVal
                            )
                            onSave(freshCustomer, false)
                        },
                        enabled = isFormValid,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Save Profile", fontWeight = FontWeight.Bold)
                    }

                    // Save & New Button (only for adding new)
                    if (customer == null) {
                        Button(
                            onClick = {
                                val creditDaysInt = creditDays.toIntOrNull() ?: 30
                                val creditLimitVal = creditLimit.toDoubleOrNull() ?: 200000.0
                                val openingBalanceVal = openingBalance.toDoubleOrNull() ?: 0.0
                                
                                val freshCustomer = Customer(
                                    id = finalCode,
                                    companyName = companyName,
                                    customerName = customerName,
                                    gstin = gstin,
                                    pan = pan,
                                    mobile = mobile,
                                    whatsapp = whatsapp,
                                    email = email,
                                    billingAddress = billingAddress,
                                    shippingAddress = shippingAddress,
                                    city = city,
                                    state = state,
                                    pincode = pincode,
                                    country = country,
                                    placeOfSupply = placeOfSupply,
                                    contactPerson = contactPerson,
                                    creditDays = creditDaysInt,
                                    creditLimit = creditLimitVal,
                                    openingBalance = openingBalanceVal,
                                    paymentTerms = paymentTerms,
                                    transportName = transportName,
                                    remark = remark,
                                    status = status,
                                    outstandingAmount = openingBalanceVal
                                )
                                onSave(freshCustomer, true)
                                
                                // Reset form inputs for next profile
                                companyName = ""
                                customerName = ""
                                gstin = ""
                                pan = ""
                                mobile = ""
                                whatsapp = ""
                                email = ""
                                billingAddress = ""
                                shippingAddress = ""
                                city = ""
                                state = "Maharashtra"
                                pincode = ""
                                country = "India"
                                placeOfSupply = "27-Maharashtra"
                                contactPerson = ""
                                creditDays = "30"
                                creditLimit = "200000"
                                openingBalance = "0"
                                paymentTerms = "Net 30 Days"
                                transportName = ""
                                remark = ""
                                status = "Active"
                            },
                            enabled = isFormValid,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PositiveGrowth),
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Text("Save & New", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
            Divider(color = DarkSlateBorder.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
fun ResponsiveFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isError: Boolean = false,
    warningText: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TradeFitTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            isError = isError
        )
        if (warningText != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(warningText, color = if (isError) WarningAlert else PendingOrange, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun CustomerDetailsModal(
    customer: Customer,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OffWhite
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MatteBlack)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Customer Dossier", style = MaterialTheme.typography.labelSmall, color = SlateTextDark)
                            Text(customer.companyName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MatteBlack)
                    }
                }
                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)
                
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Stats Block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = CardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Outstanding", style = MaterialTheme.typography.labelSmall, color = SlateTextDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${customer.outstandingAmount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = WarningAlert)
                            }
                        }
                        
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = CardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Credit Limit", style = MaterialTheme.typography.labelSmall, color = SlateTextDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${customer.creditLimit}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            }
                        }
                    }
                    
                    // Information Sections
                    DetailsSection(title = "Statutory & Identification") {
                        DetailItem("Customer Code", customer.id)
                        DetailItem("Company Name", customer.companyName)
                        DetailItem("Primary Contact", customer.customerName)
                        DetailItem("GSTIN Number", customer.gstin)
                        DetailItem("PAN Number", customer.pan)
                        DetailItem("Place of Supply", customer.placeOfSupply)
                    }
                    
                    DetailsSection(title = "Contact & Addresses") {
                        DetailItem("Mobile", customer.mobile)
                        DetailItem("WhatsApp", customer.whatsapp)
                        DetailItem("Email ID", customer.email)
                        DetailItem("Billing Address", customer.billingAddress)
                        DetailItem("Shipping Address", customer.shippingAddress)
                        DetailItem("City / State", "${customer.city}, ${customer.state} - ${customer.pincode}")
                        DetailItem("Country", customer.country)
                    }
                    
                    DetailsSection(title = "Financials & Logistics") {
                        DetailItem("Credit Days Allowed", "${customer.creditDays} Days")
                        DetailItem("Opening Balance", "₹${customer.openingBalance}")
                        DetailItem("Payment Terms", customer.paymentTerms)
                        DetailItem("Preferred Transport", customer.transportName)
                        DetailItem("Remarks", customer.remark)
                        DetailItem("Status State", customer.status)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DarkSlateBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                content()
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
        Text(value.ifEmpty { "N/A" }, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.5f))
    }
}



// ==========================================
// 4. FABRIC MASTER SCREEN (PRODUCTION-READY SPORTWEAR & TEXTILE EDITION)
// ==========================================

data class Fabric(
    val code: String,
    val name: String,
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
    val status: String = "Active",
    val currentStock: Double = 0.0,
    val specialFeatures: List<String> = emptyList()
) {
    val weave: String get() = construction
}

val initialFabrics = listOf(
    Fabric(
        code = "FAB-SPO-001",
        name = "AeroDry Poly Spandex",
        brand = "FitTex",
        category = "Sportswear",
        fabricType = "Knitted",
        construction = "75D/72F Interlock",
        composition = "88% Polyester, 12% Spandex",
        gsm = 180,
        width = 60.0,
        weightUnit = "Kg",
        colour = "Volt Green",
        shadeNumber = "SH-782",
        finish = "Moisture Management & Anti-Odor",
        stretchType = "4 Way Stretch",
        texture = "Smooth Matte",
        season = "Summer",
        hsnCode = "5407",
        gstPercentage = 5.0,
        ratePerKg = 320.0,
        ratePerMeter = 110.0,
        minStock = 500.0,
        description = "High performance micro-polyester spandex blend, ideal for activewear, running t-shirts and gym apparel.",
        imageUrl = "volt_green",
        status = "Active",
        currentStock = 1250.0,
        specialFeatures = listOf("Quick Dry", "Moisture Wicking", "4 Way Stretch", "Breathable")
    ),
    Fabric(
        code = "FAB-SPO-002",
        name = "ThermaShield Brushed Fleece",
        brand = "WarmFlex",
        category = "Winter Sportswear",
        fabricType = "Knitted",
        construction = "30s + 75D Fleece with Brush",
        composition = "95% Polyester, 5% Elastane",
        gsm = 280,
        width = 62.0,
        weightUnit = "Kg",
        colour = "Melange Grey",
        shadeNumber = "SH-102",
        finish = "Anti-Pilling Brushed Finish",
        stretchType = "2 Way Stretch",
        texture = "Soft Brushed Fleece Inner",
        season = "Winter",
        hsnCode = "6001",
        gstPercentage = 12.0,
        ratePerKg = 450.0,
        ratePerMeter = 180.0,
        minStock = 300.0,
        description = "Premium warmth fleece fabric with anti-pilling outer face. Perfect for winter hoodies, jackets and joggers.",
        imageUrl = "melange_grey",
        status = "Active",
        currentStock = 820.0,
        specialFeatures = listOf("Thermal", "Soft Touch", "Anti Pilling", "Breathable")
    ),
    Fabric(
        code = "FAB-TEX-003",
        name = "Nylon Taslon Ripstop",
        brand = "Duraguard",
        category = "Outdoor Textiles",
        fabricType = "Woven",
        construction = "70D * 160D Ripstop",
        composition = "100% Nylon",
        gsm = 120,
        width = 58.0,
        weightUnit = "Meter",
        colour = "Navy Blue",
        shadeNumber = "SH-409",
        finish = "Water Repellent & PU Coated",
        stretchType = "No Stretch",
        texture = "Ripstop Grid Texture",
        season = "All Weather",
        hsnCode = "5407",
        gstPercentage = 12.0,
        ratePerKg = 0.0,
        ratePerMeter = 135.0,
        minStock = 1000.0,
        description = "Highly durable Nylon Taslon with ripstop reinforcement and PU coating for water resistance. Perfect for windbreakers, tents, and functional tracksuits.",
        imageUrl = "navy_blue",
        status = "Active",
        currentStock = 2400.0,
        specialFeatures = listOf("Waterproof", "Water Repellent", "Breathable", "Anti Static")
    ),
    Fabric(
        code = "FAB-SPO-004",
        name = "HydraVent Pique Mesh",
        brand = "FitTex",
        category = "Sportswear",
        fabricType = "Knitted",
        construction = "100D/144F Pique",
        composition = "100% Polyester",
        gsm = 145,
        width = 60.0,
        weightUnit = "Kg",
        colour = "Crimson Red",
        shadeNumber = "SH-954",
        finish = "Silicone Softener",
        stretchType = "4 Way Stretch",
        texture = "Honeycomb Mesh",
        season = "Summer",
        hsnCode = "6006",
        gstPercentage = 5.0,
        ratePerKg = 290.0,
        ratePerMeter = 95.0,
        minStock = 400.0,
        description = "Open honeycomb pique design for maximum air flow. Anti-bacterial treated, perfect for premium athletic polo t-shirts.",
        imageUrl = "crimson_red",
        status = "Inactive",
        currentStock = 150.0,
        specialFeatures = listOf("Quick Dry", "Moisture Wicking", "Anti Bacterial", "Breathable")
    )
)

fun generateNextFabricCode(existing: List<Fabric>): String {
    val maxNum = existing.mapNotNull { f ->
        f.code.substringAfter("FAB-SPO-").substringAfter("FAB-TEX-").substringAfter("FAB-").toIntOrNull()
    }.maxOrNull() ?: 4
    return "FAB-SPO-${String.format("%03d", maxNum + 1)}"
}

@Composable
fun FabricMasterScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedCompositionFilter by remember { mutableStateOf("All Compositions") }
    var selectedWidthFilter by remember { mutableStateOf("All Widths") }
    var selectedStatusFilter by remember { mutableStateOf("All Statuses") }
    
    val fabricList = remember { mutableStateListOf<Fabric>().apply { addAll(initialFabrics) } }
    
    var showFormModal by remember { mutableStateOf(false) }
    var editingFabric by remember { mutableStateOf<Fabric?>(null) }
    var viewingFabric by remember { mutableStateOf<Fabric?>(null) }
    var fabricToDelete by remember { mutableStateOf<Fabric?>(null) }
    
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    var sortColumn by remember { mutableStateOf("code") }
    var sortAscending by remember { mutableStateOf(true) }
    
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5
    
    var isAdvancedFilterVisible by remember { mutableStateOf(false) }

    // Simulation delay to demonstrate professional loading skeleton UX
    LaunchedEffect(searchKeyword, selectedCompositionFilter, selectedWidthFilter, selectedStatusFilter, sortColumn, sortAscending) {
        isLoading = true
        delay(350)
        isLoading = false
        currentPage = 1
    }

    // Comprehensive Searching and Filtering
    val filteredFabrics = fabricList.filter { fabric ->
        val matchesSearch = searchKeyword.isEmpty() ||
                fabric.name.contains(searchKeyword, ignoreCase = true) ||
                fabric.gsm.toString().contains(searchKeyword) ||
                fabric.colour.contains(searchKeyword, ignoreCase = true) ||
                fabric.category.contains(searchKeyword, ignoreCase = true) ||
                fabric.code.contains(searchKeyword, ignoreCase = true)
        
        val matchesComposition = selectedCompositionFilter == "All Compositions" ||
                fabric.composition.contains(selectedCompositionFilter, ignoreCase = true)
                
        val matchesWidth = when (selectedWidthFilter) {
            "All Widths" -> true
            "58 inches" -> fabric.width == 58.0
            "60 inches" -> fabric.width == 60.0
            "62 inches" -> fabric.width == 62.0
            else -> true
        }
        
        val matchesStatus = selectedStatusFilter == "All Statuses" ||
                fabric.status.equals(selectedStatusFilter, ignoreCase = true)
        
        matchesSearch && matchesComposition && matchesWidth && matchesStatus
    }

    // Sorting
    val sortedFabrics = remember(filteredFabrics, sortColumn, sortAscending) {
        val comparator = when (sortColumn) {
            "code" -> compareBy<Fabric> { it.code }
            "name" -> compareBy<Fabric> { it.name }
            "gsm" -> compareBy<Fabric> { it.gsm }
            "stock" -> compareBy<Fabric> { it.currentStock }
            "rate" -> compareBy<Fabric> { it.ratePerKg }
            "status" -> compareBy<Fabric> { it.status }
            else -> compareBy<Fabric> { it.code }
        }
        if (sortAscending) filteredFabrics.sortedWith(comparator) else filteredFabrics.sortedWith(comparator).reversed()
    }

    // Pagination
    val totalPages = (sortedFabrics.size + itemsPerPage - 1) / itemsPerPage
    val coercedTotalPages = totalPages.coerceAtLeast(1)
    val paginatedFabrics = sortedFabrics.drop((currentPage - 1) * itemsPerPage).take(itemsPerPage)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(16.dp)
                .testTag("fabric_master_root"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Breadcrumb Navigation
            item {
                Breadcrumb(path = listOf("Home", "Inventory", "Fabric Master"))
            }

            // 2. Title & Action Toolbar (Designed primarily for Desktop 1920x1080 resolution first)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Fabric Master catalog",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MatteBlack,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                editingFabric = null
                                showFormModal = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Fabric", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Importing fabric parameters spreadsheet..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Excel", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Exporting complete Fabric quality index to .xlsx..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Excel", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Generating high-fidelity Fabric Catalog PDF..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Spooling print request for catalog specifications..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Search and Advanced Toggle Panel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchComponent(
                            query = searchKeyword,
                            onQueryChange = { searchKeyword = it },
                            placeholder = "Search by quality name, GSM, color, category..."
                        )
                    }
                    OutlinedButton(
                        onClick = { isAdvancedFilterVisible = !isAdvancedFilterVisible },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ExecutiveBlue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isAdvancedFilterVisible) LightBlueAccent else PureWhite,
                            contentColor = ExecutiveBlue
                        )
                    ) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Advanced Filter", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. Expanded Filter Board
            item {
                if (isAdvancedFilterVisible) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Textile Analytics Filters", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Filter by Composition", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                FilterComponent(
                                    selectedFilter = selectedCompositionFilter,
                                    availableFilters = listOf("All Compositions", "Polyester", "Nylon", "Cotton", "Spandex", "Elastane"),
                                    onFilterSelected = { selectedCompositionFilter = it }
                                )
                            }
                            
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Filter by Finished Width", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                FilterComponent(
                                    selectedFilter = selectedWidthFilter,
                                    availableFilters = listOf("All Widths", "58 inches", "60 inches", "62 inches"),
                                    onFilterSelected = { selectedWidthFilter = it }
                                )
                            }
                            
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Filter by Status", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                FilterComponent(
                                    selectedFilter = selectedStatusFilter,
                                    availableFilters = listOf("All Statuses", "Active", "Inactive"),
                                    onFilterSelected = { selectedStatusFilter = it }
                                )
                            }
                        }
                    }
                }
            }

            // 5. Professional ERP Data Table (designed for 1920x1080 first)
            item {
                ScrollableFabricTable(
                    fabrics = paginatedFabrics,
                    isLoading = isLoading,
                    sortColumn = sortColumn,
                    sortAscending = sortAscending,
                    onSort = { selectedCol ->
                        if (sortColumn == selectedCol) {
                            sortAscending = !sortAscending
                        } else {
                            sortColumn = selectedCol
                            sortAscending = true
                        }
                    },
                    onView = { viewingFabric = it },
                    onEdit = {
                        editingFabric = it
                        showFormModal = true
                    },
                    onDelete = { fabricToDelete = it }
                )
            }

            // 6. Pagination Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val startIdx = if (sortedFabrics.isEmpty()) 0 else (currentPage - 1) * itemsPerPage + 1
                    val endIdx = (currentPage * itemsPerPage).coerceAtMost(sortedFabrics.size)
                    
                    Text(
                        text = "Showing $startIdx-$endIdx of ${sortedFabrics.size} fabric qualities",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextDark
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            enabled = currentPage > 1,
                            onClick = { currentPage -= 1 },
                            modifier = Modifier
                                .border(1.dp, if (currentPage > 1) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Page", tint = if (currentPage > 1) ExecutiveBlue else SlateTextLight)
                        }
                        
                        IconButton(
                            enabled = currentPage < coercedTotalPages,
                            onClick = { currentPage += 1 },
                            modifier = Modifier
                                .border(1.dp, if (currentPage < coercedTotalPages) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Next Page", tint = if (currentPage < coercedTotalPages) ExecutiveBlue else SlateTextLight)
                        }
                    }
                }
            }
        }

        // Toasts Layer
        toastMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                NotificationToast(message = msg, onDismiss = { toastMessage = null })
            }
        }

        // Add / Edit Modal Sheet
        if (showFormModal) {
            FabricFormModal(
                fabric = editingFabric,
                existingFabrics = fabricList,
                onDismiss = { showFormModal = false },
                onSave = { savedFabric, keepOpen ->
                    if (editingFabric != null) {
                        val index = fabricList.indexOfFirst { it.code == savedFabric.code }
                        if (index != -1) {
                            fabricList[index] = savedFabric
                        }
                        toastMessage = "Fabric specification updated successfully!"
                    } else {
                        fabricList.add(savedFabric)
                        toastMessage = "${savedFabric.name} added to standard catalog!"
                    }
                    
                    if (keepOpen) {
                        editingFabric = null // Reset for next entries in "Save & New"
                    } else {
                        showFormModal = false
                    }
                }
            )
        }

        // View Details Sheet
        viewingFabric?.let { fabric ->
            FabricDetailsModal(
                fabric = fabric,
                onDismiss = { viewingFabric = null }
            )
        }

        // Delete Confirm Box
        fabricToDelete?.let { fabric ->
            ConfirmationDialog(
                title = "Remove Quality Record",
                message = "Are you sure you want to permanently delete the standard configuration for ${fabric.name}? Doing so may disrupt active stock roll assignments.",
                confirmLabel = "Delete Config",
                dismissLabel = "Keep Quality",
                onConfirm = {
                    fabricList.remove(fabric)
                    toastMessage = "Deleted ${fabric.name} from quality database"
                    fabricToDelete = null
                },
                onDismiss = { fabricToDelete = null }
            )
        }
    }
}

@Composable
fun ScrollableFabricTable(
    fabrics: List<Fabric>,
    isLoading: Boolean,
    sortColumn: String,
    sortAscending: Boolean,
    onSort: (String) -> Unit,
    onView: (Fabric) -> Unit,
    onEdit: (Fabric) -> Unit,
    onDelete: (Fabric) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column(modifier = Modifier.width(1900.dp)) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OffWhite)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SortableHeaderCell("Fabric Code", "code", sortColumn, sortAscending, onSort, Modifier.width(110.dp))
                        SortableHeaderCell("Fabric Name", "name", sortColumn, sortAscending, onSort, Modifier.width(220.dp))
                        HeaderCell("Category", Modifier.width(140.dp))
                        HeaderCell("Construction", Modifier.width(150.dp))
                        HeaderCell("Composition", Modifier.width(180.dp))
                        SortableHeaderCell("GSM", "gsm", sortColumn, sortAscending, onSort, Modifier.width(100.dp))
                        HeaderCell("Width (in)", Modifier.width(100.dp))
                        HeaderCell("Colour", Modifier.width(110.dp))
                        HeaderCell("Finish Details", Modifier.width(160.dp))
                        HeaderCell("HSN Code", Modifier.width(100.dp))
                        HeaderCell("GST %", Modifier.width(80.dp))
                        SortableHeaderCell("Rate/Kg", "rate", sortColumn, sortAscending, onSort, Modifier.width(100.dp))
                        HeaderCell("Rate/Meter", Modifier.width(110.dp))
                        SortableHeaderCell("Current Stock", "stock", sortColumn, sortAscending, onSort, Modifier.width(120.dp))
                        SortableHeaderCell("Status", "status", sortColumn, sortAscending, onSort, Modifier.width(100.dp))
                        HeaderCell("Actions", Modifier.width(120.dp))
                    }
                    Divider(color = DarkSlateBorder, thickness = 1.dp)
                    
                    if (isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(4) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(2.5f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1.5f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1.5f))
                                    LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1f))
                                }
                            }
                        }
                    } else if (fabrics.isEmpty()) {
                        EmptyState(
                            title = "No Fabric Standard Found",
                            message = "No fabric records match standard database selection filters",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp)
                        )
                    } else {
                        fabrics.forEach { fabric ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(fabric.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = ExecutiveBlue, modifier = Modifier.width(110.dp))
                                Text(fabric.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MatteBlack, modifier = Modifier.width(220.dp))
                                Text(fabric.category, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(140.dp))
                                Text(fabric.construction, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(150.dp))
                                Text(fabric.composition, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(180.dp))
                                Text("${fabric.gsm} GSM", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(100.dp))
                                Text("${fabric.width}\"", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(100.dp))
                                
                                Row(modifier = Modifier.width(110.dp), verticalAlignment = Alignment.CenterVertically) {
                                    val swatchColor = getMockColorSwatch(fabric.colour)
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(swatchColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(fabric.colour, style = MaterialTheme.typography.bodySmall, color = MatteBlack)
                                }
                                
                                Text(fabric.finish.ifEmpty { "Standard Soft" }, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(160.dp))
                                Text(fabric.hsnCode, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(100.dp))
                                Text("${fabric.gstPercentage}%", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(80.dp))
                                Text(if (fabric.ratePerKg > 0) "₹${fabric.ratePerKg}" else "—", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(100.dp))
                                Text(if (fabric.ratePerMeter > 0) "₹${fabric.ratePerMeter}" else "—", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(110.dp))
                                
                                val stockUnit = if (fabric.weightUnit == "Kg") "Kgs" else "Mtrs"
                                Text("${fabric.currentStock} $stockUnit", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (fabric.currentStock < fabric.minStock) WarningAlert else PositiveGrowth, modifier = Modifier.width(120.dp))
                                
                                Box(modifier = Modifier.width(100.dp), contentAlignment = Alignment.CenterStart) {
                                    StatusBadge(
                                        status = fabric.status,
                                        color = if (fabric.status == "Active") PositiveGrowth else SlateTextLight
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.width(120.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { onView(fabric) }, modifier = Modifier.size(28.dp).testTag("btn_view_fab_${fabric.code}")) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = "View Details", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = { onEdit(fabric) }, modifier = Modifier.size(28.dp).testTag("btn_edit_fab_${fabric.code}")) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Quality", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = { onDelete(fabric) }, modifier = Modifier.size(28.dp).testTag("btn_delete_fab_${fabric.code}")) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Config", tint = WarningAlert, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Divider(color = DarkSlateBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricFormModal(
    fabric: Fabric?,
    existingFabrics: List<Fabric>,
    onDismiss: () -> Unit,
    onSave: (Fabric, Boolean) -> Unit
) {
    val finalCode = remember { fabric?.code ?: generateNextFabricCode(existingFabrics) }
    
    var name by remember { mutableStateOf(fabric?.name ?: "") }
    var brand by remember { mutableStateOf(fabric?.brand ?: "") }
    var category by remember { mutableStateOf(fabric?.category ?: "Sportswear") }
    var fabricType by remember { mutableStateOf(fabric?.fabricType ?: "Knitted") }
    var construction by remember { mutableStateOf(fabric?.construction ?: "") }
    var composition by remember { mutableStateOf(fabric?.composition ?: "") }
    var gsm by remember { mutableStateOf(fabric?.gsm?.toString() ?: "180") }
    var width by remember { mutableStateOf(fabric?.width?.toString() ?: "58") }
    var weightUnit by remember { mutableStateOf(fabric?.weightUnit ?: "Kg") }
    var colour by remember { mutableStateOf(fabric?.colour ?: "") }
    var shadeNumber by remember { mutableStateOf(fabric?.shadeNumber ?: "") }
    var finish by remember { mutableStateOf(fabric?.finish ?: "") }
    var stretchType by remember { mutableStateOf(fabric?.stretchType ?: "4 Way Stretch") }
    var texture by remember { mutableStateOf(fabric?.texture ?: "") }
    var season by remember { mutableStateOf(fabric?.season ?: "Summer") }
    var hsnCode by remember { mutableStateOf(fabric?.hsnCode ?: "5407") }
    var gstPercentage by remember { mutableStateOf(fabric?.gstPercentage?.toString() ?: "5.0") }
    var ratePerKg by remember { mutableStateOf(fabric?.ratePerKg?.toString() ?: "0") }
    var ratePerMeter by remember { mutableStateOf(fabric?.ratePerMeter?.toString() ?: "0") }
    var minStock by remember { mutableStateOf(fabric?.minStock?.toString() ?: "500") }
    var description by remember { mutableStateOf(fabric?.description ?: "") }
    var imageUrl by remember { mutableStateOf(fabric?.imageUrl ?: "volt_green") }
    var status by remember { mutableStateOf(fabric?.status ?: "Active") }
    
    val selectedSpecialFeatures = remember { mutableStateListOf<String>().apply { 
        fabric?.specialFeatures?.let { addAll(it) } 
    } }

    // Warnings and Validators
    val isNameDuplicate = name.isNotEmpty() && existingFabrics.any { it.name.equals(name, ignoreCase = true) && it.code != fabric?.code }
    val isGstInvalid = gstPercentage.toDoubleOrNull() == null || gstPercentage.toDouble() < 0.0
    val isGsmInvalid = gsm.toIntOrNull() == null || gsm.toInt() <= 0
    val isWidthInvalid = width.toDoubleOrNull() == null || width.toDouble() <= 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OffWhite
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back", tint = MatteBlack)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (fabric != null) "Edit Fabric Specifications" else "Configure Fabric Quality",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatteBlack
                            )
                            Text(
                                text = "Fabric Master Code: $finalCode",
                                style = MaterialTheme.typography.labelSmall,
                                color = ExecutiveBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Form", tint = MatteBlack)
                    }
                }
                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)

                // Scrollable content area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Warning message block if duplicate fabric name detected
                    if (isNameDuplicate) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarningAlert.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, WarningAlert)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WarningAlert)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "A quality named '$name' already exists in the catalog! Saving will create a duplicate record.",
                                    color = WarningAlert,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Grid-like layout using side-by-side rows for Desktop efficiency
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column (60% weight) - Text and Parameter fields
                        Column(
                            modifier = Modifier.weight(1.5f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Section 1: Basic Specifications
                            FormSection(title = "Primary Specifications") {
                                ResponsiveFormField(
                                    label = "Fabric Quality Name *",
                                    value = name,
                                    onValueChange = { name = it },
                                    placeholder = "e.g., AeroDry Poly Spandex"
                                )
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "Brand / Label",
                                        value = brand,
                                        onValueChange = { brand = it },
                                        placeholder = "e.g., FitTex",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Category Type",
                                        value = category,
                                        onValueChange = { category = it },
                                        placeholder = "e.g., Sportswear",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "Fabric Structure Type",
                                        value = fabricType,
                                        onValueChange = { fabricType = it },
                                        placeholder = "e.g., Knitted or Woven",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Construction Spec",
                                        value = construction,
                                        onValueChange = { construction = it },
                                        placeholder = "e.g., 75D/72F Interlock",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Section 2: Technical Specifications
                            FormSection(title = "Technical & Weft Parameters") {
                                ResponsiveFormField(
                                    label = "Composition Formula *",
                                    value = composition,
                                    onValueChange = { composition = it },
                                    placeholder = "e.g., 88% Polyester, 12% Spandex"
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "GSM Weight *",
                                        value = gsm,
                                        onValueChange = { gsm = it },
                                        placeholder = "e.g., 180",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Finished Width (inches) *",
                                        value = width,
                                        onValueChange = { width = it },
                                        placeholder = "e.g., 60",
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    // Weight unit selection
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Weight unit", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PureWhite)
                                                .border(1.dp, DarkSlateBorder, RoundedCornerShape(8.dp)),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .background(if (weightUnit == "Kg") ExecutiveBlue else PureWhite)
                                                    .clickable { weightUnit = "Kg" },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Kg", color = if (weightUnit == "Kg") PureWhite else MatteBlack, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .background(if (weightUnit == "Meter") ExecutiveBlue else PureWhite)
                                                    .clickable { weightUnit = "Meter" },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Meter", color = if (weightUnit == "Meter") PureWhite else MatteBlack, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }

                            // Section 3: Color, Stretch and Shade Finish
                            FormSection(title = "Aesthetics & Textures") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "Finished Colour *",
                                        value = colour,
                                        onValueChange = { colour = it },
                                        placeholder = "e.g., Volt Green",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Shade Card Number",
                                        value = shadeNumber,
                                        onValueChange = { shadeNumber = it },
                                        placeholder = "e.g., SH-782",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "Finish Description",
                                        value = finish,
                                        onValueChange = { finish = it },
                                        placeholder = "e.g., Moisture Management",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Stretch Type Style",
                                        value = stretchType,
                                        onValueChange = { stretchType = it },
                                        placeholder = "e.g., 4 Way Stretch",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "Texture Feel",
                                        value = texture,
                                        onValueChange = { texture = it },
                                        placeholder = "e.g., Honeycomb Grid",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Season Focus",
                                        value = season,
                                        onValueChange = { season = it },
                                        placeholder = "e.g., Summer or All-weather",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Section 4: HSN and Commerce Pricing
                            FormSection(title = "Commerce, Tariff & Limits") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "HSN Code spec *",
                                        value = hsnCode,
                                        onValueChange = { hsnCode = it },
                                        placeholder = "e.g., 5407",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "GST Percentage (%) *",
                                        value = gstPercentage,
                                        onValueChange = { gstPercentage = it },
                                        placeholder = "5.0 or 12.0",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField(
                                        label = "Rate per Kilogram (₹)",
                                        value = ratePerKg,
                                        onValueChange = { ratePerKg = it },
                                        placeholder = "₹/Kg",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Rate per Meter (₹)",
                                        value = ratePerMeter,
                                        onValueChange = { ratePerMeter = it },
                                        placeholder = "₹/Meter",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ResponsiveFormField(
                                        label = "Min Buffer Stock Limit",
                                        value = minStock,
                                        onValueChange = { minStock = it },
                                        placeholder = "e.g., 500",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Right Column (40% weight) - Interactive Features and Mock Visual Swatches
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            
                            // Fabric Image Upload Mock & Preview
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Fabric Visual Representation", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                    
                                    // Live Color Canvas Texture Preview Box
                                    val swatchColor = getMockColorSwatch(colour.ifEmpty { "Default" })
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(swatchColor)
                                            .border(1.dp, DarkSlateBorder, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Draw a simple repeating canvas pattern or graphic text depending on selected features
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = if (colour.lowercase().contains("grey") || colour.lowercase().contains("slate")) PureWhite else PureWhite.copy(alpha = 0.8f),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Text(
                                                text = "${colour.ifEmpty { "SELECT A COLOR" }}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = PureWhite
                                            )
                                            Text(
                                                text = "Composition: ${composition.ifEmpty { "Not set" }}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PureWhite.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    // Upload Image Mock Actions
                                    Text("Mock Image Actions", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                // Pre-fill with a random sample standard color style
                                                val list = listOf("Volt Green", "Crimson Red", "Navy Blue", "Melange Grey", "Royal Gold", "Soft Lavender")
                                                colour = list.random()
                                                shadeNumber = "SH-${(100..999).random()}"
                                                imageUrl = colour.lowercase().replace(" ", "_")
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = LightBlueAccent, contentColor = ExecutiveBlue),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Generate Swatch", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                // Trigger simulation of uploading camera assets
                                                imageUrl = "custom_camera_upload"
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, ExecutiveBlue),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                                        ) {
                                            Text("Upload Image", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Special Features Multiple Selection Checklist
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Sportswear Performance Features", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                    
                                    val featuresList = listOf(
                                        "Water Repellent", "Waterproof", "Quick Dry", "Moisture Wicking",
                                        "Anti Bacterial", "Anti Static", "UPF 50+", "Breathable",
                                        "4 Way Stretch", "2 Way Stretch", "Anti Pilling", "Thermal", "Soft Touch"
                                    )

                                    // Lazy vertical or grid list for checklist items
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        featuresList.forEach { feat ->
                                            val isChecked = selectedSpecialFeatures.contains(feat)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        if (isChecked) {
                                                            selectedSpecialFeatures.remove(feat)
                                                        } else {
                                                            selectedSpecialFeatures.add(feat)
                                                        }
                                                    }
                                                    .padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isChecked,
                                                    onCheckedChange = { checked ->
                                                        if (checked == true) {
                                                            selectedSpecialFeatures.add(feat)
                                                        } else {
                                                            selectedSpecialFeatures.remove(feat)
                                                        }
                                                    },
                                                    colors = CheckboxDefaults.colors(checkedColor = ExecutiveBlue)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(feat, style = MaterialTheme.typography.bodySmall, color = MatteBlack)
                                            }
                                        }
                                    }
                                }
                            }

                            // Status Selection
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Status of Quality", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (status == "Active") PositiveGrowth else OffWhite)
                                                .clickable { status = "Active" }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text("Active", color = if (status == "Active") PureWhite else SlateTextDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (status == "Inactive") SlateTextLight else OffWhite)
                                                .clickable { status = "Inactive" }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text("Inactive", color = if (status == "Inactive") PureWhite else SlateTextDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }

                            // Form Field for general Description
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Catalog Description", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = description,
                                        onValueChange = { description = it },
                                        placeholder = { Text("Describe application focus, touch feel, handfeel, draping or other textile specs...", style = MaterialTheme.typography.bodySmall) },
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ExecutiveBlue,
                                            unfocusedBorderColor = DarkSlateBorder
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)

                // Footer Save Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp),
                        border = BorderStroke(1.dp, ExecutiveBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        enabled = name.isNotEmpty() && gsm.isNotEmpty() && width.isNotEmpty() && composition.isNotEmpty(),
                        onClick = {
                            val saved = Fabric(
                                code = finalCode,
                                name = name,
                                brand = brand,
                                category = category,
                                fabricType = fabricType,
                                construction = construction,
                                composition = composition,
                                gsm = gsm.toIntOrNull() ?: 180,
                                width = width.toDoubleOrNull() ?: 58.0,
                                weightUnit = weightUnit,
                                colour = colour.ifEmpty { "Standard Grey" },
                                shadeNumber = shadeNumber,
                                finish = finish,
                                stretchType = stretchType,
                                texture = texture,
                                season = season,
                                hsnCode = hsnCode,
                                gstPercentage = gstPercentage.toDoubleOrNull() ?: 5.0,
                                ratePerKg = ratePerKg.toDoubleOrNull() ?: 0.0,
                                ratePerMeter = ratePerMeter.toDoubleOrNull() ?: 0.0,
                                minStock = minStock.toDoubleOrNull() ?: 500.0,
                                description = description,
                                imageUrl = imageUrl,
                                status = status,
                                currentStock = fabric?.currentStock ?: 0.0, // Retain existing inventory
                                specialFeatures = selectedSpecialFeatures.toList()
                            )
                            onSave(saved, true) // Clear and keep open for consecutive quick entries
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                    ) {
                        Text("Save & New", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        enabled = name.isNotEmpty() && gsm.isNotEmpty() && width.isNotEmpty() && composition.isNotEmpty(),
                        onClick = {
                            val saved = Fabric(
                                code = finalCode,
                                name = name,
                                brand = brand,
                                category = category,
                                fabricType = fabricType,
                                construction = construction,
                                composition = composition,
                                gsm = gsm.toIntOrNull() ?: 180,
                                width = width.toDoubleOrNull() ?: 58.0,
                                weightUnit = weightUnit,
                                colour = colour.ifEmpty { "Standard Grey" },
                                shadeNumber = shadeNumber,
                                finish = finish,
                                stretchType = stretchType,
                                texture = texture,
                                season = season,
                                hsnCode = hsnCode,
                                gstPercentage = gstPercentage.toDoubleOrNull() ?: 5.0,
                                ratePerKg = ratePerKg.toDoubleOrNull() ?: 0.0,
                                ratePerMeter = ratePerMeter.toDoubleOrNull() ?: 0.0,
                                minStock = minStock.toDoubleOrNull() ?: 500.0,
                                description = description,
                                imageUrl = imageUrl,
                                status = status,
                                currentStock = fabric?.currentStock ?: 0.0,
                                specialFeatures = selectedSpecialFeatures.toList()
                            )
                            onSave(saved, false)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                    ) {
                        Text("Save Standard", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricDetailsModal(
    fabric: Fabric,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = OffWhite
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Fabric Specifications Specs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                        Text("Config ID: ${fabric.code}", style = MaterialTheme.typography.labelSmall, color = ExecutiveBlue, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MatteBlack)
                    }
                }
                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)

                // Scrollable details board
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Large Banner Swatch
                    val swatchColor = getMockColorSwatch(fabric.colour)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(swatchColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(fabric.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PureWhite)
                            Text("Brand: ${fabric.brand.ifEmpty { "TradeFit Basic" }}", style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.8f))
                        }
                    }

                    // Properties Grid
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Textile Specifications", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            
                            GridPropertyRow("Composition", fabric.composition, "GSM Density", "${fabric.gsm} GSM")
                            GridPropertyRow("Finished Width", "${fabric.width} inches", "Weave style", fabric.construction)
                            GridPropertyRow("Colour Tone", fabric.colour, "Shade Code", fabric.shadeNumber.ifEmpty { "Default" })
                            GridPropertyRow("Material Class", fabric.fabricType, "Focus Season", fabric.season)
                            GridPropertyRow("Tariff HSN", fabric.hsnCode, "GST Bracket", "${fabric.gstPercentage}%")
                        }
                    }

                    // Commercial Stock Details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Commercial & Stock Controls", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            
                            val stockUnit = if (fabric.weightUnit == "Kg") "Kgs" else "Mtrs"
                            GridPropertyRow("Rate Per Kg", "₹${fabric.ratePerKg}", "Rate Per Meter", "₹${fabric.ratePerMeter}")
                            GridPropertyRow("Current Inventory", "${fabric.currentStock} $stockUnit", "Min Safety Stock", "${fabric.minStock} $stockUnit")
                        }
                    }

                    // Special features chip row
                    if (fabric.specialFeatures.isNotEmpty()) {
                        Text("Performance Features", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MatteBlack)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            fabric.specialFeatures.forEach { feat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(LightBlueAccent)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = feat,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ExecutiveBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // General Description
                    if (fabric.description.isNotEmpty()) {
                        Text("Description", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MatteBlack)
                        Text(fabric.description, style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
                    }
                }
            }
        }
    }
}

// Simple layout flow-row helper for chip layouts
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Basic simplified grid columns layout to bypass Compose experimental FlowRow constraints
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun GridPropertyRow(label1: String, val1: String, label2: String, val2: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label1.uppercase(), style = MaterialTheme.typography.labelSmall, color = SlateTextLight, fontWeight = FontWeight.Bold)
            Text(val1.ifEmpty { "N/A" }, style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label2.uppercase(), style = MaterialTheme.typography.labelSmall, color = SlateTextLight, fontWeight = FontWeight.Bold)
            Text(val2.ifEmpty { "N/A" }, style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
        }
    }
}

// Simple swatch mapping based on color name
fun getMockColorSwatch(color: String): Color {
    val term = color.lowercase()
    return when {
        term.contains("volt") || term.contains("green") -> Color(0xFF62D345)
        term.contains("navy") || term.contains("blue") -> Color(0xFF1B365D)
        term.contains("crimson") || term.contains("red") -> Color(0xFFD32F2F)
        term.contains("grey") || term.contains("gray") || term.contains("melange") -> Color(0xFF7A869A)
        term.contains("gold") || term.contains("yellow") -> Color(0xFFD4AF37)
        term.contains("lavender") || term.contains("purple") -> Color(0xFF9E7BFF)
        else -> Color(0xFF4A5568)
    }
}

@Composable
fun FabricCardItem(fabric: Fabric) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fabric_card_${fabric.code}"),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(fabric.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                    Text("Weave Spec: ${fabric.weave}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ExecutiveBlue)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(fabric.code, style = MaterialTheme.typography.labelSmall, color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(OffWhite).padding(8.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DENSITY / GSM", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                        Text("${fabric.gsm} GSM", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MatteBlack)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(OffWhite).padding(8.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STANDARD WIDTH", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                        Text("${fabric.width}\"", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MatteBlack)
                    }
                }
            }
        }
    }
}


// ==========================================
// 5. STOCK SCREEN (PRODUCTION-READY CORE INVENTORY)
// ==========================================

data class StockEntry(
    val id: String,
    val entryDate: String,
    val fabricCode: String,
    val fabricName: String,
    val category: String,
    val colour: String,
    val gsm: Int,
    val width: Double,
    val composition: String,
    val lotNumber: String,
    val rollNumber: String,
    val numberOfRolls: Int,
    val grossWeight: Double,
    val tareWeight: Double,
    val netWeight: Double,
    val ratePerKg: Double,
    val totalValue: Double,
    val supplierName: String,
    val purchaseInvoiceNo: String,
    val warehouse: String,
    val rackNumber: String,
    val remarks: String,
    val status: String // "Available", "Reserved", "Dispatched", "QC Pending"
)

val initialStockEntries = listOf(
    StockEntry(
        id = "STK-2026-001",
        entryDate = "2026-07-06",
        fabricCode = "FAB-SPO-001",
        fabricName = "AeroDry Poly Spandex",
        category = "Sportswear",
        colour = "Volt Green",
        gsm = 180,
        width = 60.0,
        composition = "88% Polyester, 12% Spandex",
        lotNumber = "LOT-509A",
        rollNumber = "ROLL-101",
        numberOfRolls = 10,
        grossWeight = 125.0,
        tareWeight = 5.0,
        netWeight = 120.0,
        ratePerKg = 320.0,
        totalValue = 38400.0,
        supplierName = "FitTex Industries",
        purchaseInvoiceNo = "PINV-9023",
        warehouse = "Surat Whse",
        rackNumber = "RACK-A12",
        remarks = "Standard stock roll lot, quality checked",
        status = "Available"
    ),
    StockEntry(
        id = "STK-2026-002",
        entryDate = "2026-07-05",
        fabricCode = "FAB-SPO-002",
        fabricName = "ThermaShield Brushed Fleece",
        category = "Winter Sportswear",
        colour = "Melange Grey",
        gsm = 280,
        width = 62.0,
        composition = "95% Polyester, 5% Elastane",
        lotNumber = "LOT-382B",
        rollNumber = "ROLL-202",
        numberOfRolls = 1,
        grossWeight = 46.5,
        tareWeight = 1.5,
        netWeight = 45.0,
        ratePerKg = 450.0,
        totalValue = 20250.0,
        supplierName = "WarmFlex Textiles",
        purchaseInvoiceNo = "PINV-7811",
        warehouse = "Delhi Whse",
        rackNumber = "RACK-B04",
        remarks = "Super soft inner brushed winter series",
        status = "Available"
    ),
    StockEntry(
        id = "STK-2026-003",
        entryDate = "2026-07-04",
        fabricCode = "FAB-TEX-003",
        fabricName = "Nylon Taslon Ripstop",
        category = "Outdoor Textiles",
        colour = "Navy Blue",
        gsm = 120,
        width = 58.0,
        composition = "100% Nylon",
        lotNumber = "LOT-703C",
        rollNumber = "ROLL-303",
        numberOfRolls = 5,
        grossWeight = 202.0,
        tareWeight = 2.0,
        netWeight = 200.0,
        ratePerKg = 135.0,
        totalValue = 27000.0,
        supplierName = "Duraguard Mills",
        purchaseInvoiceNo = "PINV-4410",
        warehouse = "Surat Whse",
        rackNumber = "RACK-C08",
        remarks = "Water repellent treated, pre-reserved for corporate order",
        status = "Reserved"
    ),
    StockEntry(
        id = "STK-2026-004",
        entryDate = "2026-07-06",
        fabricCode = "FAB-SPO-004",
        fabricName = "HydraVent Pique Mesh",
        category = "Sportswear",
        colour = "Crimson Red",
        gsm = 145,
        width = 60.0,
        composition = "100% Polyester",
        lotNumber = "LOT-102D",
        rollNumber = "ROLL-404",
        numberOfRolls = 3,
        grossWeight = 93.0,
        tareWeight = 3.0,
        netWeight = 90.0,
        ratePerKg = 290.0,
        totalValue = 26100.0,
        supplierName = "FitTex Industries",
        purchaseInvoiceNo = "PINV-1102",
        warehouse = "Ludhiana Whse",
        rackNumber = "RACK-D11",
        remarks = "Moisture management tests pending in QC lab",
        status = "QC Pending"
    )
)

fun generateNextStockId(existing: List<StockEntry>): String {
    val maxNum = existing.mapNotNull { s ->
        s.id.substringAfter("STK-2026-").substringAfter("STK-").toIntOrNull()
    }.maxOrNull() ?: 4
    return "STK-2026-${String.format("%03d", maxNum + 1)}"
}

@Composable
fun StockScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFabricFilter by remember { mutableStateOf("All Fabrics") }
    var selectedColourFilter by remember { mutableStateOf("All Colours") }
    var selectedWarehouseFilter by remember { mutableStateOf("All Warehouses") }
    var selectedLotFilter by remember { mutableStateOf("All Lots") }
    var selectedStatusFilter by remember { mutableStateOf("All Statuses") }
    var selectedDateRange by remember { mutableStateOf("All Dates") }
    
    var currentReportView by remember { mutableStateOf("All Stock") }
    
    val stockList = remember { mutableStateListOf<StockEntry>().apply { addAll(initialStockEntries) } }
    
    var showFormModal by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<StockEntry?>(null) }
    var viewingEntry by remember { mutableStateOf<StockEntry?>(null) }
    var printingLabelEntry by remember { mutableStateOf<StockEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<StockEntry?>(null) }
    
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    var sortColumn by remember { mutableStateOf("id") }
    var sortAscending by remember { mutableStateOf(false) }
    
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5
    
    var isAdvancedFilterVisible by remember { mutableStateOf(false) }

    // Analytics Calculation
    val totalAvailableStockWeight = stockList.filter { it.status == "Available" || it.status == "QC Pending" }.sumOf { it.netWeight }
    val totalReservedStockWeight = stockList.filter { it.status == "Reserved" }.sumOf { it.netWeight }
    val todayInwardWeight = stockList.filter { it.entryDate == "2026-07-06" }.sumOf { it.netWeight }
    val todayOutwardWeight = 120.0 // Realistic simulated outbound weight
    val totalInventoryValue = stockList.sumOf { it.totalValue }
    val totalRollsCount = stockList.sumOf { it.numberOfRolls }

    // Simulated Loading for robust Enterprise feeling
    LaunchedEffect(searchKeyword, selectedFabricFilter, selectedColourFilter, selectedWarehouseFilter, selectedLotFilter, selectedStatusFilter, selectedDateRange, currentReportView, sortColumn, sortAscending) {
        isLoading = true
        delay(300)
        isLoading = false
        currentPage = 1
    }

    // Filter Logic
    val filteredStock = stockList.filter { entry ->
        // Search filter
        val matchesSearch = searchKeyword.isEmpty() ||
                entry.fabricName.contains(searchKeyword, ignoreCase = true) ||
                entry.id.contains(searchKeyword, ignoreCase = true) ||
                entry.lotNumber.contains(searchKeyword, ignoreCase = true) ||
                entry.rollNumber.contains(searchKeyword, ignoreCase = true) ||
                entry.supplierName.contains(searchKeyword, ignoreCase = true)
                
        // Dropdown filters
        val matchesFabric = selectedFabricFilter == "All Fabrics" || entry.fabricName == selectedFabricFilter
        val matchesColour = selectedColourFilter == "All Colours" || entry.colour == selectedColourFilter
        val matchesWarehouse = selectedWarehouseFilter == "All Warehouses" || entry.warehouse == selectedWarehouseFilter
        val matchesLot = selectedLotFilter == "All Lots" || entry.lotNumber == selectedLotFilter
        val matchesStatus = selectedStatusFilter == "All Statuses" || entry.status.equals(selectedStatusFilter, ignoreCase = true)
        
        val matchesDate = when (selectedDateRange) {
            "Today" -> entry.entryDate == "2026-07-06"
            "Last 7 Days" -> entry.entryDate >= "2026-06-30"
            else -> true
        }

        // Quick Report tabs filter
        val matchesReport = when (currentReportView) {
            "Today's Stock" -> entry.entryDate == "2026-07-06"
            "Current Stock" -> entry.status == "Available" || entry.status == "QC Pending"
            "Low Stock" -> entry.netWeight < 100.0
            "Out of Stock" -> entry.netWeight <= 0.0
            "Warehouse Wise Stock" -> entry.warehouse.isNotEmpty()
            "Fabric Wise Stock" -> entry.fabricName.isNotEmpty()
            else -> true
        }

        matchesSearch && matchesFabric && matchesColour && matchesWarehouse && matchesLot && matchesStatus && matchesDate && matchesReport
    }

    // Sorting Logic
    val sortedStock = remember(filteredStock, sortColumn, sortAscending) {
        val comparator = when (sortColumn) {
            "id" -> compareBy<StockEntry> { it.id }
            "date" -> compareBy<StockEntry> { it.entryDate }
            "name" -> compareBy<StockEntry> { it.fabricName }
            "lot" -> compareBy<StockEntry> { it.lotNumber }
            "roll" -> compareBy<StockEntry> { it.rollNumber }
            "qty" -> compareBy<StockEntry> { it.numberOfRolls }
            "weight" -> compareBy<StockEntry> { it.netWeight }
            "rate" -> compareBy<StockEntry> { it.ratePerKg }
            "warehouse" -> compareBy<StockEntry> { it.warehouse }
            "status" -> compareBy<StockEntry> { it.status }
            else -> compareBy<StockEntry> { it.id }
        }
        if (sortAscending) filteredStock.sortedWith(comparator) else filteredStock.sortedWith(comparator).reversed()
    }

    // Pagination
    val totalPages = (sortedStock.size + itemsPerPage - 1) / itemsPerPage
    val coercedTotalPages = totalPages.coerceAtLeast(1)
    val paginatedStock = sortedStock.drop((currentPage - 1) * itemsPerPage).take(itemsPerPage)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(16.dp)
                .testTag("stock_root"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Breadcrumb
            item {
                Breadcrumb(path = listOf("Home", "Inventory", "Stock Management"))
            }

            // 2. Toolbar & Main Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Core Roll Inventory Ledger",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MatteBlack,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                editingEntry = null
                                showFormModal = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Stock Entry", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Importing raw supplier shipment dispatch log..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Logs", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { toastMessage = "Exporting current stock reconciliation log..." },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ExecutiveBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Sheet", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Analytics Dashboard Grid (6 Metrics Cards)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        title = "Available Stock",
                        value = "${String.format("%.1f", totalAvailableStockWeight)} Kgs",
                        subtitle = "Ready for Delivery",
                        icon = Icons.Default.CheckCircle,
                        color = PositiveGrowth,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        title = "Reserved Stock",
                        value = "${String.format("%.1f", totalReservedStockWeight)} Kgs",
                        subtitle = "Allocated to Orders",
                        icon = Icons.Default.Warning,
                        color = PendingOrange,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        title = "Today's Inward",
                        value = "${String.format("%.1f", todayInwardWeight)} Kgs",
                        subtitle = "Newly Logged",
                        icon = Icons.Default.ArrowUpward,
                        color = ExecutiveBlue,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        title = "Today's Outward",
                        value = "${String.format("%.1f", todayOutwardWeight)} Kgs",
                        subtitle = "Pending Loadout",
                        icon = Icons.Default.ArrowDownward,
                        color = WarningAlert,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        title = "Inventory Value",
                        value = "₹${String.format("%,.0f", totalInventoryValue)}",
                        subtitle = "Cost Basis (INR)",
                        icon = Icons.Default.ReceiptLong,
                        color = MatteBlack,
                        modifier = Modifier.weight(1.2f)
                    )
                    DashboardCard(
                        title = "Total Rolls",
                        value = "$totalRollsCount",
                        subtitle = "Active Roll Slates",
                        icon = Icons.Default.Layers,
                        color = ExecutiveBlue,
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }

            // 4. Quick Reports Tabs Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val reports = listOf(
                            "All Stock",
                            "Today's Stock",
                            "Current Stock",
                            "Low Stock",
                            "Out of Stock",
                            "Warehouse Wise Stock",
                            "Fabric Wise Stock"
                        )
                        reports.forEach { r ->
                            val isSelected = currentReportView == r
                            Button(
                                onClick = { currentReportView = r },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) ExecutiveBlue else OffWhite,
                                    contentColor = if (isSelected) PureWhite else SlateTextDark
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(r, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 5. Search, Date and Filter Toolbars
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchComponent(
                            query = searchKeyword,
                            onQueryChange = { searchKeyword = it },
                            placeholder = "Search by ID, lot, roll, fabric or supplier..."
                        )
                    }
                    OutlinedButton(
                        onClick = { isAdvancedFilterVisible = !isAdvancedFilterVisible },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ExecutiveBlue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isAdvancedFilterVisible) LightBlueAccent else PureWhite,
                            contentColor = ExecutiveBlue
                        )
                    ) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Advanced Filters", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Expanded filters block
            item {
                if (isAdvancedFilterVisible) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Refined Logistics Filters", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Fabric Quality", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    val fabricsList = listOf("All Fabrics") + stockList.map { it.fabricName }.distinct()
                                    FilterComponent(
                                        selectedFilter = selectedFabricFilter,
                                        availableFilters = fabricsList,
                                        onFilterSelected = { selectedFabricFilter = it }
                                    )
                                }
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Colour", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    val coloursList = listOf("All Colours") + stockList.map { it.colour }.distinct()
                                    FilterComponent(
                                        selectedFilter = selectedColourFilter,
                                        availableFilters = coloursList,
                                        onFilterSelected = { selectedColourFilter = it }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Warehouse Location", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    val whseList = listOf("All Warehouses", "Surat Whse", "Delhi Whse", "Ludhiana Whse", "Mumbai Whse")
                                    FilterComponent(
                                        selectedFilter = selectedWarehouseFilter,
                                        availableFilters = whseList,
                                        onFilterSelected = { selectedWarehouseFilter = it }
                                    )
                                }
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Lot Code", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    val lotList = listOf("All Lots") + stockList.map { it.lotNumber }.distinct()
                                    FilterComponent(
                                        selectedFilter = selectedLotFilter,
                                        availableFilters = lotList,
                                        onFilterSelected = { selectedLotFilter = it }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Date Filter", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    FilterComponent(
                                        selectedFilter = selectedDateRange,
                                        availableFilters = listOf("All Dates", "Today", "Last 7 Days"),
                                        onFilterSelected = { selectedDateRange = it }
                                    )
                                }
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("QC & Reservation Status", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    FilterComponent(
                                        selectedFilter = selectedStatusFilter,
                                        availableFilters = listOf("All Statuses", "Available", "Reserved", "Dispatched", "QC Pending"),
                                        onFilterSelected = { selectedStatusFilter = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Professional ERP Data Table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardBorder()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            Column(modifier = Modifier.width(1900.dp)) {
                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(OffWhite)
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SortableHeaderCell("Stock ID", "id", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(110.dp))
                                    SortableHeaderCell("Date", "date", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(110.dp))
                                    SortableHeaderCell("Fabric Name", "name", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(230.dp))
                                    SortableHeaderCell("Lot Number", "lot", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(130.dp))
                                    SortableHeaderCell("Roll Number", "roll", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(130.dp))
                                    SortableHeaderCell("Roll Qty", "qty", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(110.dp))
                                    SortableHeaderCell("Weight (Kg)", "weight", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(130.dp))
                                    SortableHeaderCell("Rate/Kg", "rate", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(120.dp))
                                    HeaderCell("Total Value", Modifier.width(140.dp))
                                    SortableHeaderCell("Warehouse", "warehouse", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(150.dp))
                                    SortableHeaderCell("Status", "status", sortColumn, sortAscending, { sortColumn = it; sortAscending = !sortAscending }, Modifier.width(120.dp))
                                    HeaderCell("Actions", Modifier.width(220.dp))
                                }
                                Divider(color = DarkSlateBorder, thickness = 1.dp)

                                if (isLoading) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        repeat(4) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1f))
                                                LoadingSkeleton(modifier = Modifier.height(24.dp).weight(2.5f))
                                                LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1.5f))
                                                LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1.5f))
                                                LoadingSkeleton(modifier = Modifier.height(24.dp).weight(1f))
                                            }
                                        }
                                    }
                                } else if (paginatedStock.isEmpty()) {
                                    EmptyState(
                                        title = "No Inventory Entries Found",
                                        message = "No matching roll entries or transaction configurations in ledger",
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp)
                                    )
                                } else {
                                    paginatedStock.forEach { entry ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(entry.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = ExecutiveBlue, modifier = Modifier.width(110.dp))
                                            Text(entry.entryDate, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(110.dp))
                                            
                                            Column(modifier = Modifier.width(230.dp)) {
                                                Text(entry.fabricName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MatteBlack)
                                                Text(entry.composition, style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                            }
                                            
                                            Text(entry.lotNumber, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Medium, modifier = Modifier.width(130.dp))
                                            Text(entry.rollNumber, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(130.dp))
                                            Text("${entry.numberOfRolls} Rolls", style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.width(110.dp))
                                            
                                            Text("${entry.netWeight} Kgs", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MatteBlack, modifier = Modifier.width(130.dp))
                                            Text("₹${entry.ratePerKg}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.width(120.dp))
                                            Text("₹${String.format("%,.0f", entry.totalValue)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue, modifier = Modifier.width(140.dp))
                                            
                                            Column(modifier = Modifier.width(150.dp)) {
                                                Text(entry.warehouse, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MatteBlack)
                                                Text(entry.rackNumber.ifEmpty { "Unassigned" }, style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                            }

                                            Box(modifier = Modifier.width(120.dp), contentAlignment = Alignment.CenterStart) {
                                                val statusColor = when (entry.status) {
                                                    "Available" -> PositiveGrowth
                                                    "Reserved" -> PendingOrange
                                                    "QC Pending" -> Color(0xFFD4AF37)
                                                    else -> SlateTextLight
                                                }
                                                StatusBadge(status = entry.status, color = statusColor)
                                            }

                                            Row(
                                                modifier = Modifier.width(220.dp),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(onClick = { viewingEntry = entry }, modifier = Modifier.size(28.dp).testTag("btn_view_stk_${entry.id}")) {
                                                    Icon(imageVector = Icons.Default.Info, contentDescription = "View details", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(onClick = { editingEntry = entry; showFormModal = true }, modifier = Modifier.size(28.dp).testTag("btn_edit_stk_${entry.id}")) {
                                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit details", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(
                                                    onClick = {
                                                        // Duplicate logic
                                                        val duplicated = entry.copy(
                                                            id = generateNextStockId(stockList),
                                                            rollNumber = "ROLL-${100 + stockList.size + 1}"
                                                        )
                                                        stockList.add(duplicated)
                                                        toastMessage = "Duplicated stock entry as ${duplicated.id}!"
                                                    },
                                                    modifier = Modifier.size(28.dp).testTag("btn_dup_stk_${entry.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicate Entry", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(onClick = { printingLabelEntry = entry }, modifier = Modifier.size(28.dp).testTag("btn_print_stk_${entry.id}")) {
                                                    Icon(imageVector = Icons.Default.Print, contentDescription = "Print Label", tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(onClick = { entryToDelete = entry }, modifier = Modifier.size(28.dp).testTag("btn_delete_stk_${entry.id}")) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete entry", tint = WarningAlert, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                        Divider(color = DarkSlateBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Pagination row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val startIdx = if (sortedStock.isEmpty()) 0 else (currentPage - 1) * itemsPerPage + 1
                    val endIdx = (currentPage * itemsPerPage).coerceAtMost(sortedStock.size)
                    
                    Text(
                        text = "Showing $startIdx-$endIdx of ${sortedStock.size} stock roll slates",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextDark
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            enabled = currentPage > 1,
                            onClick = { currentPage -= 1 },
                            modifier = Modifier
                                .border(1.dp, if (currentPage > 1) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Page", tint = if (currentPage > 1) ExecutiveBlue else SlateTextLight)
                        }
                        
                        IconButton(
                            enabled = currentPage < coercedTotalPages,
                            onClick = { currentPage += 1 },
                            modifier = Modifier
                                .border(1.dp, if (currentPage < coercedTotalPages) ExecutiveBlue else DarkSlateBorder, RoundedCornerShape(8.dp))
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Next Page", tint = if (currentPage < coercedTotalPages) ExecutiveBlue else SlateTextLight)
                        }
                    }
                }
            }
        }

        // Form Dialog (Full Screen modal)
        if (showFormModal) {
            StockFormModal(
                entry = editingEntry,
                existingEntries = stockList,
                onDismiss = { showFormModal = false },
                onSave = { savedEntry, keepOpen ->
                    if (editingEntry != null) {
                        val idx = stockList.indexOfFirst { it.id == savedEntry.id }
                        if (idx != -1) {
                            stockList[idx] = savedEntry
                        }
                        toastMessage = "Stock configuration modified successfully!"
                    } else {
                        stockList.add(savedEntry)
                        toastMessage = "${savedEntry.id} recorded in central ledger!"
                    }

                    if (keepOpen) {
                        editingEntry = null // Reset form to let them enter next rolls quickly
                    } else {
                        showFormModal = false
                    }
                }
            )
        }

        // Details Modal
        viewingEntry?.let { entry ->
            StockDetailsModal(
                entry = entry,
                onDismiss = { viewingEntry = null },
                onPrintLabel = {
                    viewingEntry = null
                    printingLabelEntry = entry
                }
            )
        }

        // Label Preview Modal
        printingLabelEntry?.let { entry ->
            BarcodeLabelModal(
                entry = entry,
                onDismiss = { printingLabelEntry = null }
            )
        }

        // Delete Confirm Dialog
        entryToDelete?.let { entry ->
            ConfirmationDialog(
                title = "Remove Inventory Slate",
                message = "Are you sure you want to delete roll log ${entry.id} (${entry.fabricName})? This action will permanently remove this stock weight from available counts.",
                confirmLabel = "Confirm Delete",
                dismissLabel = "Cancel",
                onConfirm = {
                    stockList.remove(entry)
                    toastMessage = "Deleted ${entry.id} from active ledger"
                    entryToDelete = null
                },
                onDismiss = { entryToDelete = null }
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = SlateTextLight, fontWeight = FontWeight.Bold)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MatteBlack)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockFormModal(
    entry: StockEntry?,
    existingEntries: List<StockEntry>,
    onDismiss: () -> Unit,
    onSave: (StockEntry, Boolean) -> Unit
) {
    val finalId = remember { entry?.id ?: generateNextStockId(existingEntries) }

    // Core state setup
    var entryDate by remember { mutableStateOf(entry?.entryDate ?: "2026-07-06") }
    var selectedFabricName by remember { mutableStateOf(entry?.fabricName ?: initialFabrics.first().name) }
    
    // Automatically retrieve default configuration based on selected fabric
    val matchedFabric = initialFabrics.firstOrNull { it.name == selectedFabricName } ?: initialFabrics.first()
    
    var category by remember { mutableStateOf(entry?.category ?: matchedFabric.category) }
    var colour by remember { mutableStateOf(entry?.colour ?: matchedFabric.colour) }
    var gsm by remember { mutableStateOf(entry?.gsm?.toString() ?: matchedFabric.gsm.toString()) }
    var width by remember { mutableStateOf(entry?.width?.toString() ?: matchedFabric.width.toString()) }
    var composition by remember { mutableStateOf(entry?.composition ?: matchedFabric.composition) }
    
    var lotNumber by remember { mutableStateOf(entry?.lotNumber ?: "") }
    var rollNumber by remember { mutableStateOf(entry?.rollNumber ?: "") }
    var numberOfRolls by remember { mutableStateOf(entry?.numberOfRolls?.toString() ?: "1") }
    
    var grossWeight by remember { mutableStateOf(entry?.grossWeight?.toString() ?: "") }
    var tareWeight by remember { mutableStateOf(entry?.tareWeight?.toString() ?: "1.5") }
    var ratePerKg by remember { mutableStateOf(entry?.ratePerKg?.toString() ?: matchedFabric.ratePerKg.toString()) }
    
    var supplierName by remember { mutableStateOf(entry?.supplierName ?: "") }
    var purchaseInvoiceNo by remember { mutableStateOf(entry?.purchaseInvoiceNo ?: "") }
    var warehouse by remember { mutableStateOf(entry?.warehouse ?: "Surat Whse") }
    var rackNumber by remember { mutableStateOf(entry?.rackNumber ?: "") }
    var remarks by remember { mutableStateOf(entry?.remarks ?: "") }
    var status by remember { mutableStateOf(entry?.status ?: "Available") }

    // Live calculations
    val grossVal = grossWeight.toDoubleOrNull() ?: 0.0
    val tareVal = tareWeight.toDoubleOrNull() ?: 0.0
    val netWeight = (grossVal - tareVal).coerceAtLeast(0.0)
    
    val rateVal = ratePerKg.toDoubleOrNull() ?: 0.0
    val totalValue = netWeight * rateVal

    // Live validation
    val isRollDuplicate = rollNumber.isNotEmpty() && existingEntries.any { it.rollNumber == rollNumber && it.id != entry?.id }
    val isLotDuplicate = lotNumber.isNotEmpty() && existingEntries.any { it.lotNumber == lotNumber && it.id != entry?.id }
    val isWeightValid = grossVal > tareVal && grossVal > 0.0
    val hasRequiredFields = selectedFabricName.isNotEmpty() && lotNumber.isNotEmpty() && rollNumber.isNotEmpty() && grossWeight.isNotEmpty() && supplierName.isNotEmpty()

    // Sync form values on fabric selection change
    LaunchedEffect(selectedFabricName) {
        val f = initialFabrics.firstOrNull { it.name == selectedFabricName }
        if (f != null && entry == null) {
            category = f.category
            colour = f.colour
            gsm = f.gsm.toString()
            width = f.width.toString()
            composition = f.composition
            ratePerKg = f.ratePerKg.toString()
            rollNumber = "ROLL-${100 + existingEntries.size + 1}"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OffWhite
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back", tint = MatteBlack)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (entry != null) "Edit Stock Parameters" else "Register Stock Roll Entry",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatteBlack
                            )
                            Text(
                                text = "Stock ID: $finalId",
                                style = MaterialTheme.typography.labelSmall,
                                color = ExecutiveBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Form", tint = MatteBlack)
                    }
                }
                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)

                // Main Form Content Scroll Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Alert banners
                    if (isRollDuplicate) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarningAlert.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, WarningAlert)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAlert)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Duplicate Roll Number: '$rollNumber' already exists in the system. Saving this might overwrite physical allocation.",
                                    color = WarningAlert,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (isLotDuplicate && !isRollDuplicate) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PendingOrange.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, PendingOrange)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PendingOrange)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Note: Lot number '$lotNumber' matches existing fabric production. This is perfectly normal for multi-roll consignments.",
                                    color = PendingOrange,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left form column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FormSection(title = "Product Allocation") {
                                // Fabric Catalog Dropdown Selection
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Select Fabric *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    var expanded by remember { mutableStateOf(false) }
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedButton(
                                            onClick = { expanded = true },
                                            modifier = Modifier.fillMaxWidth().height(52.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, DarkSlateBorder),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = PureWhite, contentColor = MatteBlack)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(selectedFabricName, style = MaterialTheme.typography.bodyMedium)
                                                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier.background(PureWhite)
                                        ) {
                                            initialFabrics.forEach { f ->
                                                DropdownMenuItem(
                                                    text = { Text(f.name) },
                                                    onClick = {
                                                        selectedFabricName = f.name
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField("Category", category, { category = it }, modifier = Modifier.weight(1f))
                                    ResponsiveFormField("Colour", colour, { colour = it }, modifier = Modifier.weight(1f))
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField("GSM Weight", gsm, { gsm = it }, modifier = Modifier.weight(1f))
                                    ResponsiveFormField("Finished Width (in)", width, { width = it }, modifier = Modifier.weight(1f))
                                }

                                ResponsiveFormField("Composition", composition, { composition = it })
                            }

                            FormSection(title = "Consignment Logistics") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField("Lot Number *", lotNumber, { lotNumber = it }, placeholder = "e.g., LOT-509A", modifier = Modifier.weight(1f))
                                    ResponsiveFormField("Roll Number *", rollNumber, { rollNumber = it }, placeholder = "e.g., ROLL-101", modifier = Modifier.weight(1f))
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField("Number of Rolls", numberOfRolls, { numberOfRolls = it }, modifier = Modifier.weight(1f))
                                    ResponsiveFormField("Entry Date", entryDate, { entryDate = it }, modifier = Modifier.weight(1f))
                                }

                                ResponsiveFormField("Supplier Name *", supplierName, { supplierName = it }, placeholder = "e.g., FitTex Industries")
                                ResponsiveFormField("Purchase Invoice No.", purchaseInvoiceNo, { purchaseInvoiceNo = it }, placeholder = "e.g., INV-9023")
                            }
                        }

                        // Right column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FormSection(title = "Weight Parameters & Pricing") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ResponsiveFormField("Gross Weight (Kg) *", grossWeight, { grossWeight = it }, placeholder = "e.g., 125.0", modifier = Modifier.weight(1f))
                                    ResponsiveFormField("Tare Weight (Kg) *", tareWeight, { tareWeight = it }, placeholder = "e.g., 1.5", modifier = Modifier.weight(1f))
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Calculated Net Weight", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(LightBlueAccent)
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = "${String.format("%.2f", netWeight)} Kgs",
                                                fontWeight = FontWeight.Bold,
                                                color = ExecutiveBlue,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                    ResponsiveFormField("Rate Per Kg (₹) *", ratePerKg, { ratePerKg = it }, modifier = Modifier.weight(1f))
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Reconciled Inventory Value", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(ExecutiveBlue.copy(alpha = 0.1f))
                                            .border(1.dp, ExecutiveBlue, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = "₹${String.format("%,.2f", totalValue)}",
                                            fontWeight = FontWeight.Bold,
                                            color = ExecutiveBlue,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }

                            FormSection(title = "Location & Warehouse") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Warehouse Location", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                        var whseExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            OutlinedButton(
                                                onClick = { whseExpanded = true },
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, DarkSlateBorder),
                                                colors = ButtonDefaults.outlinedButtonColors(containerColor = PureWhite, contentColor = MatteBlack)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(warehouse)
                                                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            DropdownMenu(
                                                expanded = whseExpanded,
                                                onDismissRequest = { whseExpanded = false },
                                                modifier = Modifier.background(PureWhite)
                                            ) {
                                                val whses = listOf("Surat Whse", "Delhi Whse", "Ludhiana Whse", "Mumbai Whse")
                                                whses.forEach { w ->
                                                    DropdownMenuItem(
                                                        text = { Text(w) },
                                                        onClick = {
                                                            warehouse = w
                                                            whseExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    ResponsiveFormField("Rack Number", rackNumber, { rackNumber = it }, placeholder = "e.g., RACK-A12", modifier = Modifier.weight(1f))
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Stock Status", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                        var statusExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            OutlinedButton(
                                                onClick = { statusExpanded = true },
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, DarkSlateBorder),
                                                colors = ButtonDefaults.outlinedButtonColors(containerColor = PureWhite, contentColor = MatteBlack)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(status)
                                                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            DropdownMenu(
                                                expanded = statusExpanded,
                                                onDismissRequest = { statusExpanded = false },
                                                modifier = Modifier.background(PureWhite)
                                            ) {
                                                val statuses = listOf("Available", "Reserved", "Dispatched", "QC Pending")
                                                statuses.forEach { s ->
                                                    DropdownMenuItem(
                                                        text = { Text(s) },
                                                        onClick = {
                                                            status = s
                                                            statusExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                ResponsiveFormField("Remarks / Log Details", remarks, { remarks = it }, placeholder = "Internal warehouse routing specifications...")
                            }
                        }
                    }
                }

                // Action Buttons Footer
                HorizontalDivider(color = DarkSlateBorder, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DarkSlateBorder),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Cancel", color = MatteBlack)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Save & New Button
                    OutlinedButton(
                        enabled = hasRequiredFields && isWeightValid,
                        onClick = {
                            val nextEntry = StockEntry(
                                id = finalId,
                                entryDate = entryDate,
                                fabricCode = matchedFabric.code,
                                fabricName = selectedFabricName,
                                category = category,
                                colour = colour,
                                gsm = gsm.toIntOrNull() ?: 180,
                                width = width.toDoubleOrNull() ?: 58.0,
                                composition = composition,
                                lotNumber = lotNumber,
                                rollNumber = rollNumber,
                                numberOfRolls = numberOfRolls.toIntOrNull() ?: 1,
                                grossWeight = grossVal,
                                tareWeight = tareVal,
                                netWeight = netWeight,
                                ratePerKg = rateVal,
                                totalValue = totalValue,
                                supplierName = supplierName,
                                purchaseInvoiceNo = purchaseInvoiceNo,
                                warehouse = warehouse,
                                rackNumber = rackNumber,
                                remarks = remarks,
                                status = status
                            )
                            onSave(nextEntry, true)
                            // Clear roll details for continuous entries
                            rollNumber = "ROLL-${100 + existingEntries.size + 2}"
                            grossWeight = ""
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ExecutiveBlue),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Save & New", color = ExecutiveBlue)
                    }

                    Button(
                        enabled = hasRequiredFields && isWeightValid,
                        onClick = {
                            val nextEntry = StockEntry(
                                id = finalId,
                                entryDate = entryDate,
                                fabricCode = matchedFabric.code,
                                fabricName = selectedFabricName,
                                category = category,
                                colour = colour,
                                gsm = gsm.toIntOrNull() ?: 180,
                                width = width.toDoubleOrNull() ?: 58.0,
                                composition = composition,
                                lotNumber = lotNumber,
                                rollNumber = rollNumber,
                                numberOfRolls = numberOfRolls.toIntOrNull() ?: 1,
                                grossWeight = grossVal,
                                tareWeight = tareVal,
                                netWeight = netWeight,
                                ratePerKg = rateVal,
                                totalValue = totalValue,
                                supplierName = supplierName,
                                purchaseInvoiceNo = purchaseInvoiceNo,
                                warehouse = warehouse,
                                rackNumber = rackNumber,
                                remarks = remarks,
                                status = status
                            )
                            onSave(nextEntry, false)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Save Entry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsModal(
    entry: StockEntry,
    onDismiss: () -> Unit,
    onPrintLabel: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = PureWhite
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Roll Metadata & Logistics Detail", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MatteBlack)
                        Text("Stock ID: ${entry.id}", style = MaterialTheme.typography.labelSmall, color = ExecutiveBlue, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MatteBlack)
                    }
                }
                
                Divider(color = DarkSlateBorder)

                // Grid layout with two columns
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailItem("Fabric Specification", entry.fabricName)
                        DetailItem("Category Group", entry.category)
                        DetailItem("Shade / Colour", entry.colour)
                        DetailItem("Composition Structure", entry.composition)
                        DetailItem("Weight Grade (GSM)", "${entry.gsm} GSM")
                        DetailItem("Finished Width", "${entry.width} Inches")
                    }
                    
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailItem("Production Lot Code", entry.lotNumber)
                        DetailItem("Assigned Roll Number", entry.rollNumber)
                        DetailItem("Consigned Rolls count", "${entry.numberOfRolls} Rolls")
                        DetailItem("Gross / Tare Weight", "${entry.grossWeight} / ${entry.tareWeight} Kgs")
                        DetailItem("Calculated Net Weight", "${entry.netWeight} Kgs")
                        DetailItem("Valuation Rate / Kg", "₹${entry.ratePerKg}")
                    }
                }

                Divider(color = DarkSlateBorder)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailItem("Supplier Company", entry.supplierName)
                        DetailItem("Purchase Invoice Ref", entry.purchaseInvoiceNo.ifEmpty { "N/A" })
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailItem("Assigned Warehouse", entry.warehouse)
                        DetailItem("Specific Rack Bin", entry.rackNumber.ifEmpty { "Not assigned" })
                    }
                }

                if (entry.remarks.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(OffWhite)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("INTERNAL LOG remarks", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(entry.remarks, style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DarkSlateBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dismiss", color = MatteBlack)
                    }

                    Button(
                        onClick = onPrintLabel,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View & Print Label", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeLabelModal(
    entry: StockEntry,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .width(420.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = PureWhite
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thermal Sticker Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MatteBlack)
                    }
                }

                // Highly realistic physical sticky barcode label card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.5.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Sticker Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TradeFit ERP", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            Text("TEXTILE COMPLIANT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        
                        Divider(color = Color.Black, thickness = 1.5.dp)

                        // Fabric Title
                        Column {
                            Text("QUALITY / ARTICLE NAME", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                            Text(entry.fabricName.uppercase(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, color = Color.Black)
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text("COMPOSITION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                Text(entry.composition, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Column(modifier = Modifier.weight(0.8f)) {
                                Text("WEIGHT GRADE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                Text("${entry.gsm} GSM | ${entry.width}\"", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("LOT NUMBER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                Text(entry.lotNumber, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ROLL IDENTIFIER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                Text(entry.rollNumber, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            }
                        }

                        Divider(color = Color.Black, thickness = 1.dp)

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1.3f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column {
                                        Text("NET WT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                        Text("${entry.netWeight} Kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.Black)
                                    }
                                    Column {
                                        Text("GROSS WT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                        Text("${entry.grossWeight} Kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(0.7f)) {
                                Text("WAREHOUSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                Text(entry.warehouse.uppercase(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                Text("BIN: ${entry.rackNumber.ifEmpty { "UNSPECIFIED" }}", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // High fidelity generated barcode visualization using dashes and weights
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Barcode visual representation
                            Text(
                                text = "||| || | | |||| || || | ||| || ||| || ||| | ||| || |",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "*${entry.id}*",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 2.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("PRINT TIME: 2026-07-06 00:55", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = Color.Gray)
                            Text("VERIFIED OK", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = Color.Gray)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Thermal Sticker Print", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class DeliveryChallanItem(
    val id: String = "",
    val fabricName: String = "",
    val colour: String = "",
    val gsm: Int = 0,
    val width: Double = 0.0,
    val lotNumber: String = "",
    val rollNumbers: List<String> = emptyList(),
    val numberOfRolls: Int = 0,
    val weightKg: Double = 0.0,
    val ratePerKg: Double = 0.0,
    val amount: Double = 0.0
)

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
    val items: List<DeliveryChallanItem> = emptyList(),
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
    val status: String = "Draft"
)

// ==========================================
// 6. DELIVERY CHALLAN SCREEN
// ==========================================

data class ProformaInvoiceItem(
    val id: String = "",
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
    val amount: Double = 0.0
)

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
    val items: List<ProformaInvoiceItem> = emptyList(),
    val totalRolls: Int = 0,
    val totalWeight: Double = 0.0,
    val subTotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val freightCharges: Double = 0.0,
    val packingCharges: Double = 0.0,
    val otherCharges: Double = 0.0,
    val taxableAmount: Double = 0.0,
    val cgstRate: Double = 9.0,
    val cgstAmount: Double = 0.0,
    val sgstRate: Double = 9.0,
    val sgstAmount: Double = 0.0,
    val igstRate: Double = 18.0,
    val igstAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val termsAndConditions: String = "",
    val bankDetails: String = "Bank Name: HDFC Bank Ltd\nAccount No: 50200012345678\nIFSC: HDFC0001234\nBranch: Ring Road, Surat",
    val upiId: String = "tradefit@upi",
    val preparedBy: String = "",
    val authorizedSignature: String = "For TradeFit Industries",
    val status: String = "Draft",
    val remarks: String = ""
)

object TradeFitSharedState {
    val deliveryChallans = mutableStateListOf<DeliveryChallan>().apply {
        addAll(initialDeliveryChallans)
    }

    val proformaInvoices = mutableStateListOf<ProformaInvoice>().apply {
        add(
            ProformaInvoice(
                id = "PI-000001",
                date = "2026-07-01",
                validityDate = "2026-07-15",
                referenceNumber = "REF-291A",
                customerId = "CUST-1001",
                companyName = "Evergreen Textiles Ltd.",
                gstNumber = "27AAACE1234F1Z8",
                billingAddress = "102, Nariman Point, Industrial Area",
                shippingAddress = "Plot 45, GIDC Estate, Sachin",
                transportName = "VRL Logistics",
                items = listOf(
                    ProformaInvoiceItem(
                        id = "PI-ITEM-001",
                        fabricName = "AeroDry Poly Spandex",
                        colour = "Volt Green",
                        gsm = 180,
                        width = 60.0,
                        lotNumber = "LOT-509A",
                        rollNumbers = listOf("ROLL-101", "ROLL-102"),
                        rollQty = 2,
                        weightKg = 48.0,
                        ratePerKg = 320.0,
                        discountPercent = 5.0,
                        amount = 14592.0
                    )
                ),
                totalRolls = 2,
                totalWeight = 48.0,
                subTotal = 15360.0,
                discountAmount = 768.0,
                freightCharges = 1200.0,
                packingCharges = 350.0,
                otherCharges = 150.0,
                taxableAmount = 16300.0,
                cgstRate = 9.0,
                cgstAmount = 1467.0,
                sgstRate = 9.0,
                sgstAmount = 1467.0,
                grandTotal = 19234.0,
                termsAndConditions = "1. Goods once sold are not returnable.\n2. Payment terms: 100% advance.",
                preparedBy = "Karan Patel",
                status = "Generated",
                remarks = "Direct order pre-booked"
            )
        )
        add(
            ProformaInvoice(
                id = "PI-000002",
                date = "2026-07-02",
                validityDate = "2026-07-16",
                referenceNumber = "REF-330B",
                customerId = "CUST-1002",
                companyName = "Maharani Silks & Fabrics",
                gstNumber = "07BBBDF5678A2Z3",
                billingAddress = "72, Chandni Chowk Commercial Block, New Delhi",
                shippingAddress = "Gate 4, Textile Logistics Depot, Delhi",
                transportName = "Delhi Fast Freight",
                items = listOf(
                    ProformaInvoiceItem(
                        id = "PI-ITEM-002",
                        fabricName = "ThermaShield Brushed Fleece",
                        colour = "Melange Grey",
                        gsm = 280,
                        width = 62.0,
                        lotNumber = "LOT-382B",
                        rollNumbers = listOf("ROLL-202"),
                        rollQty = 1,
                        weightKg = 45.0,
                        ratePerKg = 450.0,
                        discountPercent = 0.0,
                        amount = 20250.0
                    )
                ),
                totalRolls = 1,
                totalWeight = 45.0,
                subTotal = 20250.0,
                discountAmount = 0.0,
                freightCharges = 800.0,
                packingCharges = 150.0,
                otherCharges = 0.0,
                taxableAmount = 21200.0,
                igstRate = 18.0,
                igstAmount = 3816.0,
                grandTotal = 25016.0,
                termsAndConditions = "1. Interest @18% p.a. will be charged for delayed payment.",
                preparedBy = "Anita Roy",
                status = "Draft",
                remarks = "Quotation under review"
            )
        )
    }

    var pendingConversionChallan by mutableStateOf<DeliveryChallan?>(null)
    var openFormPrefilled by mutableStateOf(false)
}

@Composable
fun DeliveryChallanScreen(
    modifier: Modifier = Modifier,
    onNavigateToProforma: () -> Unit = {}
) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Statuses") }
    val filters = listOf("All Statuses", "Draft", "Approved", "Dispatched", "Cancelled")

    // Dynamic list of Delivery Challans from shared state
    val challans = TradeFitSharedState.deliveryChallans

    // Modal state controllers
    var showEditor by remember { mutableStateOf(false) }
    var editingChallan by remember { mutableStateOf<DeliveryChallan?>(null) }
    var viewingChallan by remember { mutableStateOf<DeliveryChallan?>(null) }
    var printingLabelChallan by remember { mutableStateOf<DeliveryChallan?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<DeliveryChallan?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Launch a coroutine to clear toast after delay
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
            .testTag("challan_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header row with title and action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Breadcrumb(path = listOf("Home", "Logistics", "Delivery Challans"))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Delivery Challans (Goods Dispatch Note)",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MatteBlack,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage customer deliveries, track vehicle dispatches, and generate challan receipts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextDark
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            editingChallan = null
                            showEditor = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                        modifier = Modifier.testTag("add_challan_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create", tint = PureWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Delivery Challan", fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }

            // Summary KPIs section (Desktop-first style row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val totalWeight = challans.filter { it.status != "Cancelled" }.sumOf { it.totalWeight }
                val totalAmount = challans.filter { it.status != "Cancelled" }.sumOf { it.grandTotal }
                val activeCount = challans.count { it.status == "Approved" || it.status == "Dispatched" }
                val draftCount = challans.count { it.status == "Draft" }

                DashboardKPICard(
                    title = "Total Value Dispatched",
                    value = "₹${String.format("%,.2f", totalAmount)}",
                    subtitle = "Excluding cancelled orders",
                    icon = Icons.Default.MonetizationOn,
                    color = PositiveGrowth,
                    modifier = Modifier.weight(1f)
                )
                DashboardKPICard(
                    title = "Total Dispatch Weight",
                    value = "${String.format("%,.2f", totalWeight)} Kg",
                    subtitle = "Across approved challans",
                    icon = Icons.Default.Scale,
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                DashboardKPICard(
                    title = "Dispatched / Approved",
                    value = "$activeCount Notes",
                    subtitle = "Ready for delivery",
                    icon = Icons.Default.LocalShipping,
                    color = ProgressiveOrange,
                    modifier = Modifier.weight(1f)
                )
                DashboardKPICard(
                    title = "Pending Drafts",
                    value = "$draftCount Drafts",
                    subtitle = "Yet to be authorized",
                    icon = Icons.Default.Edit,
                    color = SlateTextLight,
                    modifier = Modifier.weight(1f)
                )
            }

            // Advanced Filters Panel
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
                            placeholder = "Search by Challan No, Customer, Vehicle or LR No..."
                        )
                    }

                    Box(modifier = Modifier.weight(1.5f)) {
                        FilterComponent(
                            selectedFilter = selectedFilter,
                            availableFilters = filters,
                            onFilterSelected = { selectedFilter = it }
                        )
                    }
                }
            }

            // Data Table List View
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardBorder()
            ) {
                val filtered = challans.filter {
                    (selectedFilter == "All Statuses" || it.status.equals(selectedFilter, ignoreCase = true)) &&
                    (it.id.contains(searchKeyword, ignoreCase = true) || 
                     it.companyName.contains(searchKeyword, ignoreCase = true) ||
                     it.vehicleNumber.contains(searchKeyword, ignoreCase = true) ||
                     it.lrNumber.contains(searchKeyword, ignoreCase = true))
                }

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "No Delivery Challans Found",
                            message = "No dispatched notes found matching your filter criteria."
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Table header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(OffWhite)
                                .padding(horizontal = 24.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Challan Code", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                            Text("Date", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                            Text("Customer Company", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.8f))
                            Text("Vehicle No", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                            Text("Rolls / Weight", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.2f))
                            Text("Grand Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                            Text("Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(0.8f))
                            Text("Actions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.8f))
                        }

                        HorizontalDivider(color = DarkSlateBorder)

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filtered) { challan ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(challan.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = ExecutiveBlue, modifier = Modifier.weight(1f))
                                    Text(challan.date, style = MaterialTheme.typography.bodyMedium, color = MatteBlack, modifier = Modifier.weight(0.8f))
                                    Text(challan.companyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MatteBlack, modifier = Modifier.weight(1.8f))
                                    Text(challan.vehicleNumber.ifEmpty { "N/A" }, style = MaterialTheme.typography.bodyMedium, color = SlateTextDark, modifier = Modifier.weight(1f))
                                    Text("${challan.totalRolls} Rolls / ${challan.totalWeight} Kg", style = MaterialTheme.typography.bodyMedium, color = MatteBlack, modifier = Modifier.weight(1.2f))
                                    Text("₹${String.format("%,.2f", challan.grandTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack, modifier = Modifier.weight(1f))
                                    
                                    Box(modifier = Modifier.weight(0.8f)) {
                                        val badgeColor = when (challan.status) {
                                            "Approved" -> PositiveGrowth
                                            "Dispatched" -> ElectricBlue
                                            "Draft" -> SlateTextLight
                                            "Cancelled" -> WarningAlert
                                            else -> SlateTextDark
                                        }
                                        StatusBadge(status = challan.status.uppercase(), color = badgeColor)
                                    }

                                    // Action buttons for Table Row
                                    Row(
                                        modifier = Modifier.weight(1.8f),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { viewingChallan = challan },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Visibility, contentDescription = "View Details", tint = ExecutiveBlue, modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                editingChallan = challan
                                                showEditor = true
                                            },
                                            enabled = challan.status == "Draft" || challan.status == "Approved",
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = if (challan.status == "Draft" || challan.status == "Approved") MatteBlack else SlateTextLight, modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                // Create a carbon copy with a new sequential ID
                                                val nextId = "CH-${String.format("%06d", challans.size + 1)}"
                                                val duplicated = challan.copy(
                                                    id = nextId,
                                                    date = "2026-07-06",
                                                    status = "Draft"
                                                )
                                                challans.add(duplicated)
                                                toastMessage = "Challan ${challan.id} duplicated successfully as draft $nextId"
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = ProgressiveOrange, modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = { printingLabelChallan = challan },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Print, contentDescription = "Print Dispatch Label", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = { showDeleteConfirm = challan },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WarningAlert, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                                HorizontalDivider(color = DarkSlateBorder.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            TablePagination()
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

        // 1. Full Screen Editor Modal (Creation & Editing)
        if (showEditor) {
            ChallanFormEditor(
                challan = editingChallan,
                challansCount = challans.size,
                onDismiss = { showEditor = false },
                onSave = { updated ->
                    val index = challans.indexOfFirst { it.id == updated.id }
                    if (index >= 0) {
                        challans[index] = updated
                        toastMessage = "Challan ${updated.id} updated successfully."
                    } else {
                        challans.add(updated)
                        toastMessage = "Challan ${updated.id} created successfully."
                    }
                    showEditor = false
                }
            )
        }

        // 2. View printed invoice sheet Modal
        viewingChallan?.let { challan ->
            ChallanPreviewSheet(
                challan = challan,
                onDismiss = { viewingChallan = null },
                onConvertProforma = {
                    viewingChallan = null
                    TradeFitSharedState.pendingConversionChallan = challan
                    TradeFitSharedState.openFormPrefilled = true
                    toastMessage = "Delivery Challan ${challan.id} successfully converted to Proforma Invoice!"
                    onNavigateToProforma()
                },
                onDownloadPdf = {
                    toastMessage = "Download initiated... PDF compiled successfully for ${challan.id}."
                },
                onSharePdf = {
                    toastMessage = "Share Link generated: tradefit-erp://share/challan/${challan.id}"
                }
            )
        }

        // 3. Print Label Modal
        printingLabelChallan?.let { challan ->
            ChallanPrintLabelModal(
                challan = challan,
                onDismiss = { printingLabelChallan = null }
            )
        }

        // 4. Delete Confirmation Dialog
        showDeleteConfirm?.let { challan ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                shape = RoundedCornerShape(16.dp),
                containerColor = PureWhite,
                title = { Text("Delete Delivery Challan", fontWeight = FontWeight.Bold, color = MatteBlack) },
                text = { Text("Are you sure you want to delete challan ${challan.id} for ${challan.companyName}? This action is permanent and cannot be undone.", color = SlateTextDark) },
                confirmButton = {
                    Button(
                        onClick = {
                            challans.remove(challan)
                            toastMessage = "Challan ${challan.id} has been permanently deleted."
                            showDeleteConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAlert)
                    ) {
                        Text("Delete permanently", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) {
                        Text("Cancel", color = SlateTextDark)
                    }
                }
            )
        }
    }
}

// KPI Dashboard Card
@Composable
fun DashboardKPICard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, fontWeight = FontWeight.Medium)
                Text(value, style = MaterialTheme.typography.headlineSmall, color = MatteBlack, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            }
        }
    }
}

// Full ERP Multi-Row Editor
@Composable
fun ChallanFormEditor(
    challan: DeliveryChallan?,
    challansCount: Int,
    onDismiss: () -> Unit,
    onSave: (DeliveryChallan) -> Unit
) {
    // Generate auto challan number if creating new
    val challanId = challan?.id ?: "CH-${String.format("%06d", challansCount + 1)}"

    var date by remember { mutableStateOf(challan?.date ?: "2026-07-06") }
    var deliveryDate by remember { mutableStateOf(challan?.deliveryDate ?: "2026-07-08") }

    // Selected Customer Details
    var selectedCustId by remember { mutableStateOf(challan?.customerId ?: "") }
    var companyName by remember { mutableStateOf(challan?.companyName ?: "") }
    var gstNumber by remember { mutableStateOf(challan?.gstNumber ?: "") }
    var mobile by remember { mutableStateOf(challan?.mobile ?: "") }
    var billingAddress by remember { mutableStateOf(challan?.billingAddress ?: "") }
    var shippingAddress by remember { mutableStateOf(challan?.shippingAddress ?: "") }

    // Transport Details
    var transportName by remember { mutableStateOf(challan?.transportName ?: "") }
    var vehicleNumber by remember { mutableStateOf(challan?.vehicleNumber ?: "") }
    var driverName by remember { mutableStateOf(challan?.driverName ?: "") }
    var driverMobile by remember { mutableStateOf(challan?.driverMobile ?: "") }
    var lrNumber by remember { mutableStateOf(challan?.lrNumber ?: "") }
    var dispatchFrom by remember { mutableStateOf(challan?.dispatchFrom ?: "") }
    var dispatchTo by remember { mutableStateOf(challan?.dispatchTo ?: "") }
    var eWayBill by remember { mutableStateOf(challan?.eWayBill ?: "") }

    // Multi-row Fabric Items Grid State
    val items = remember {
        mutableStateListOf<DeliveryChallanItem>().apply {
            if (challan != null) {
                addAll(challan.items)
            } else {
                // Add one default empty row
                add(
                    DeliveryChallanItem(
                        id = "ITEM-1",
                        fabricName = "",
                        colour = "",
                        gsm = 0,
                        width = 0.0,
                        lotNumber = "",
                        rollNumbers = emptyList(),
                        numberOfRolls = 0,
                        weightKg = 0.0,
                        ratePerKg = 0.0,
                        amount = 0.0
                    )
                )
            }
        }
    }

    // Pricing summary parameters
    var discountType by remember { mutableStateOf(challan?.discountPercentOrAmt ?: "Percent") }
    var discountValueStr by remember { mutableStateOf(challan?.discountValue?.toString() ?: "0.0") }
    var freightChargesStr by remember { mutableStateOf(challan?.freightCharges?.toString() ?: "0.0") }
    var packingChargesStr by remember { mutableStateOf(challan?.packingCharges?.toString() ?: "0.0") }
    var otherChargesStr by remember { mutableStateOf(challan?.otherCharges?.toString() ?: "0.0") }

    var remarks by remember { mutableStateOf(challan?.remarks ?: "") }
    var termsAndConditions by remember { mutableStateOf(challan?.termsAndConditions ?: "1. Goods once dispatched cannot be returned without written permission. 2. Any discrepancies must be reported within 24 hours.") }
    var preparedBy by remember { mutableStateOf(challan?.preparedBy ?: "Administrator") }
    var isDraftState by remember { mutableStateOf(challan?.status == "Draft" || challan == null) }

    // State for Search Dropdowns
    var showCustomerSearch by remember { mutableStateOf(false) }
    var activeItemIndexForSearch by remember { mutableStateOf<Int?>(null) }
    var activeItemIndexForRolls by remember { mutableStateOf<Int?>(null) }

    // Validation Alert Message
    var validationError by remember { mutableStateOf<String?>(null) }

    // Dynamic Live Calculations
    val totalRolls = items.sumOf { it.numberOfRolls }
    val totalWeight = items.sumOf { it.weightKg }
    val subTotal = items.sumOf { it.amount }

    val discountVal = discountValueStr.toDoubleOrNull() ?: 0.0
    val discountAmt = if (discountType == "Percent") {
        (subTotal * discountVal / 100.0)
    } else {
        discountVal
    }

    val freight = freightChargesStr.toDoubleOrNull() ?: 0.0
    val packing = packingChargesStr.toDoubleOrNull() ?: 0.0
    val other = otherChargesStr.toDoubleOrNull() ?: 0.0

    val taxableAmount = subTotal - discountAmt + freight + packing + other
    val grandTotal = taxableAmount

    // Custom Search Popups
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
                    .testTag("challan_editor_modal"),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Editor Topbar Header
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
                                text = if (challan == null) "Create Delivery Challan" else "Modify Delivery Challan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MatteBlack
                            )
                            Text(
                                text = "Document ID: $challanId",
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
                                Text("Discard Draft", color = SlateTextDark)
                            }

                            Button(
                                onClick = {
                                    // Validate fields
                                    if (companyName.isEmpty()) {
                                        validationError = "Customer selection is required. Please pick a customer."
                                        return@Button
                                    }
                                    if (items.isEmpty() || items.any { it.fabricName.isEmpty() || it.weightKg <= 0.0 }) {
                                        validationError = "At least one valid fabric item is required with positive weight."
                                        return@Button
                                    }

                                    // Duplicate roll check across items
                                    val allSelectedRolls = items.flatMap { it.rollNumbers }
                                    val duplicates = allSelectedRolls.groupBy { it }.filter { it.value.size > 1 }.keys
                                    if (duplicates.isNotEmpty()) {
                                        validationError = "Warning: Duplicate roll numbers selected in this challan: ${duplicates.joinToString(", ")}"
                                        return@Button
                                    }

                                    // Build updated model
                                    val newChallan = DeliveryChallan(
                                        id = challanId,
                                        date = date,
                                        deliveryDate = deliveryDate,
                                        customerId = selectedCustId,
                                        companyName = companyName,
                                        gstNumber = gstNumber,
                                        mobile = mobile,
                                        billingAddress = billingAddress,
                                        shippingAddress = shippingAddress,
                                        transportName = transportName,
                                        vehicleNumber = vehicleNumber,
                                        driverName = driverName,
                                        driverMobile = driverMobile,
                                        lrNumber = lrNumber,
                                        dispatchFrom = dispatchFrom,
                                        dispatchTo = dispatchTo,
                                        eWayBill = eWayBill,
                                        items = items.toList(),
                                        totalRolls = totalRolls,
                                        totalWeight = totalWeight,
                                        subTotal = subTotal,
                                        discountPercentOrAmt = discountType,
                                        discountValue = discountVal,
                                        discountAmount = discountAmt,
                                        freightCharges = freight,
                                        packingCharges = packing,
                                        otherCharges = other,
                                        taxableAmount = taxableAmount,
                                        grandTotal = grandTotal,
                                        remarks = remarks,
                                        termsAndConditions = termsAndConditions,
                                        preparedBy = preparedBy,
                                        status = if (isDraftState) "Draft" else "Approved"
                                    )
                                    onSave(newChallan)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                            ) {
                                Text(if (challan == null) "Generate & Authorize" else "Save Changes", color = PureWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = DarkSlateBorder)

                    // Error Alert Bar
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
                            IconButton(onClick = { validationError = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = WarningAlert, modifier = Modifier.size(16.dp))
                            }
                        }
                        HorizontalDivider(color = WarningAlert.copy(alpha = 0.3f))
                    }

                    // Content Scroll Area (Two Column ERP Desktop First View)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // COLUMN 1: Metadata / Customer / Transport Logistics
                        Column(
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxHeight()
                                .background(OffWhite)
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Document Dates
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Dispatch Parameters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(
                                                value = date,
                                                onValueChange = { date = it },
                                                label = "Challan Date",
                                                placeholder = "YYYY-MM-DD"
                                            )
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(
                                                value = deliveryDate,
                                                onValueChange = { deliveryDate = it },
                                                label = "Target Delivery Date",
                                                placeholder = "YYYY-MM-DD"
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isDraftState,
                                            onCheckedChange = { isDraftState = it }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Save as Draft (Unapproved)", style = MaterialTheme.typography.bodyMedium, color = MatteBlack, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            // Customer selection Card
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
                                        Text("Customer Assignment *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                        TextButton(onClick = { showCustomerSearch = true }) {
                                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Search Customer", fontWeight = FontWeight.Bold, color = ExecutiveBlue)
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
                                            Text("Click here to select an active customer", style = MaterialTheme.typography.bodyMedium, color = SlateTextLight, fontWeight = FontWeight.Medium)
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            TradeFitTextField(
                                                value = companyName,
                                                onValueChange = { companyName = it },
                                                label = "Customer Company Name",
                                                placeholder = "Pick customer"
                                            )
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Box(modifier = Modifier.weight(1.2f)) {
                                                    TradeFitTextField(
                                                        value = gstNumber,
                                                        onValueChange = { gstNumber = it },
                                                        label = "GST Number",
                                                        placeholder = "27XXXXX..."
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = mobile,
                                                        onValueChange = { mobile = it },
                                                        label = "Contact Mobile",
                                                        placeholder = "Phone number"
                                                    )
                                                }
                                            }

                                            TradeFitTextField(
                                                value = billingAddress,
                                                onValueChange = { billingAddress = it },
                                                label = "Billing Address",
                                                placeholder = "Registered billing address"
                                            )

                                            TradeFitTextField(
                                                value = shippingAddress,
                                                onValueChange = { shippingAddress = it },
                                                label = "Shipping Destination Address",
                                                placeholder = "Warehouse delivery location"
                                            )
                                        }
                                    }
                                }
                            }

                            // Transport Details Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Logistics & Transport Parameters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = transportName, onValueChange = { transportName = it }, label = "Transport Agency", placeholder = "e.g., Safe Cargo")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = vehicleNumber, onValueChange = { vehicleNumber = it }, label = "Vehicle Number", placeholder = "e.g., GJ05-AX-1011")
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = driverName, onValueChange = { driverName = it }, label = "Driver Name", placeholder = "Driver's identity")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = driverMobile, onValueChange = { driverMobile = it }, label = "Driver Mobile", placeholder = "Phone contact")
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = lrNumber, onValueChange = { lrNumber = it }, label = "LR / Lorry Receipt No", placeholder = "Receipt refer")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = eWayBill, onValueChange = { eWayBill = it }, label = "E-Way Bill Number (Optional)", placeholder = "Govt dispatch clearance")
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = dispatchFrom, onValueChange = { dispatchFrom = it }, label = "Dispatch From Location", placeholder = "e.g., Surat Warehouse")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = dispatchTo, onValueChange = { dispatchTo = it }, label = "Dispatch To Destination", placeholder = "e.g., Customer Shed")
                                        }
                                    }
                                }
                            }
                        }

                        // COLUMN 2: Items Grid table, computations, remarks
                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Fabric Items Card List
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
                                        Text("Fabric Dispatches Grid", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                        
                                        Button(
                                            onClick = {
                                                val nextIdx = items.size + 1
                                                items.add(
                                                    DeliveryChallanItem(
                                                        id = "ITEM-$nextIdx",
                                                        fabricName = "",
                                                        colour = "",
                                                        gsm = 0,
                                                        width = 0.0,
                                                        lotNumber = "",
                                                        rollNumbers = emptyList(),
                                                        numberOfRolls = 0,
                                                        weightKg = 0.0,
                                                        ratePerKg = 0.0,
                                                        amount = 0.0
                                                    )
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Add", tint = PureWhite, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Add Item Row", color = PureWhite, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }

                                    // Render rows
                                    items.forEachIndexed { index, item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = OffWhite),
                                            border = BorderStroke(1.dp, DarkSlateBorder.copy(alpha = 0.6f))
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                // Top action buttons row
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Item Row #${index + 1}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = ExecutiveBlue)
                                                    
                                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        // Duplicate Action
                                                        TextButton(
                                                            onClick = {
                                                                val copy = item.copy(id = "ITEM-${items.size + 1}")
                                                                items.add(copy)
                                                            },
                                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                        ) {
                                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = ProgressiveOrange)
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Duplicate", style = MaterialTheme.typography.labelSmall, color = ProgressiveOrange)
                                                        }

                                                        // Delete Action
                                                        TextButton(
                                                            onClick = {
                                                                items.removeAt(index)
                                                            },
                                                            enabled = items.size > 1,
                                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                        ) {
                                                            Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(14.dp), tint = if (items.size > 1) WarningAlert else SlateTextLight)
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Remove", style = MaterialTheme.typography.labelSmall, color = if (items.size > 1) WarningAlert else SlateTextLight)
                                                        }
                                                    }
                                                }

                                                // Row contents: Selector Dropdown or fields
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                    Box(modifier = Modifier.weight(1.5f)) {
                                                        TradeFitTextField(
                                                            value = item.fabricName,
                                                            onValueChange = { newVal ->
                                                                items[index] = item.copy(fabricName = newVal)
                                                            },
                                                            label = "Fabric Specification *",
                                                            placeholder = "Search / Type fabric",
                                                            trailingIcon = {
                                                                IconButton(onClick = { activeItemIndexForSearch = index }) {
                                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select stock", tint = SlateTextDark)
                                                                }
                                                            }
                                                        )
                                                    }

                                                    Box(modifier = Modifier.weight(1f)) {
                                                        TradeFitTextField(
                                                            value = item.lotNumber,
                                                            onValueChange = { items[index] = item.copy(lotNumber = it) },
                                                            label = "Lot Number *",
                                                            placeholder = "e.g. LOT-509A"
                                                        )
                                                    }
                                                }

                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        TradeFitTextField(
                                                            value = item.colour,
                                                            onValueChange = { items[index] = item.copy(colour = it) },
                                                            label = "Colour",
                                                            placeholder = "Volt Green"
                                                        )
                                                    }
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        TradeFitTextField(
                                                            value = if (item.gsm > 0) item.gsm.toString() else "",
                                                            onValueChange = { items[index] = item.copy(gsm = it.toIntOrNull() ?: 0) },
                                                            label = "GSM",
                                                            placeholder = "180"
                                                        )
                                                    }
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        TradeFitTextField(
                                                            value = if (item.width > 0.0) item.width.toString() else "",
                                                            onValueChange = { items[index] = item.copy(width = it.toDoubleOrNull() ?: 0.0) },
                                                            label = "Width (in)",
                                                            placeholder = "60.0"
                                                        )
                                                    }
                                                }

                                                // Roll multi-select + numeric calculations
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    // Roll selections
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1.5f)
                                                            .border(1.dp, DarkSlateBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                            .background(PureWhite)
                                                            .clickable { activeItemIndexForRolls = index }
                                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column {
                                                                Text("Assign Roll Numbers *", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                                                Spacer(modifier = Modifier.height(2.dp))
                                                                if (item.rollNumbers.isEmpty()) {
                                                                    Text("No Rolls selected", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                                                } else {
                                                                    Text(item.rollNumbers.joinToString(", "), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = ExecutiveBlue)
                                                                }
                                                            }
                                                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Roll selector", tint = SlateTextDark)
                                                        }
                                                    }

                                                    Box(modifier = Modifier.weight(0.8f)) {
                                                        TradeFitTextField(
                                                            value = if (item.numberOfRolls > 0) item.numberOfRolls.toString() else "0",
                                                            onValueChange = { /* Auto filled from list size */ },
                                                            label = "Roll Count",
                                                            readOnly = true,
                                                            placeholder = "0"
                                                        )
                                                    }

                                                    Box(modifier = Modifier.weight(1f)) {
                                                        TradeFitTextField(
                                                            value = if (item.weightKg > 0.0) item.weightKg.toString() else "",
                                                            onValueChange = {
                                                                val w = it.toDoubleOrNull() ?: 0.0
                                                                items[index] = item.copy(weightKg = w, amount = w * item.ratePerKg)
                                                            },
                                                            label = "Weight (Kg) *",
                                                            placeholder = "e.g., 48.0"
                                                        )
                                                    }

                                                    Box(modifier = Modifier.weight(1f)) {
                                                        TradeFitTextField(
                                                            value = if (item.ratePerKg > 0.0) item.ratePerKg.toString() else "",
                                                            onValueChange = {
                                                                val r = it.toDoubleOrNull() ?: 0.0
                                                                items[index] = item.copy(ratePerKg = r, amount = item.weightKg * r)
                                                            },
                                                            label = "Rate/Kg *",
                                                            placeholder = "e.g., 320"
                                                        )
                                                    }

                                                    Box(modifier = Modifier.weight(1.2f)) {
                                                        TradeFitTextField(
                                                            value = "₹${String.format("%.2f", item.amount)}",
                                                            onValueChange = { },
                                                            label = "Amount",
                                                            readOnly = true,
                                                            placeholder = "0.00"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Computations Pricing block
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("Dispatch Computations Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Total dispatches rolls count:", style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
                                                Text("$totalRolls Rolls", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Sum total net weight:", style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
                                                Text("$totalWeight Kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Items valuation subtotal:", style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
                                                Text("₹${String.format("%,.2f", subTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            // Discount input
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Box(modifier = Modifier.width(90.dp)) {
                                                    // Simple toggle
                                                    Row(
                                                        modifier = Modifier
                                                            .border(1.dp, DarkSlateBorder, RoundedCornerShape(4.dp))
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .clickable {
                                                                discountType = if (discountType == "Percent") "Amount" else "Percent"
                                                            }
                                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(if (discountType == "Percent") "% Disc" else "₹ Disc", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(Icons.Default.SwapHoriz, contentDescription = "Toggle", modifier = Modifier.size(12.dp), tint = ExecutiveBlue)
                                                    }
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = discountValueStr,
                                                        onValueChange = { discountValueStr = it },
                                                        label = "Value",
                                                        placeholder = "0.0"
                                                    )
                                                }
                                                Text("= ₹${String.format("%.2f", discountAmt)}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark, fontWeight = FontWeight.SemiBold)
                                            }

                                            // Freight Charges
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = freightChargesStr,
                                                        onValueChange = { freightChargesStr = it },
                                                        label = "Freight Charges (₹)",
                                                        placeholder = "0.0"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = packingChargesStr,
                                                        onValueChange = { packingChargesStr = it },
                                                        label = "Packing Charges (₹)",
                                                        placeholder = "0.0"
                                                    )
                                                }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    TradeFitTextField(
                                                        value = otherChargesStr,
                                                        onValueChange = { otherChargesStr = it },
                                                        label = "Other Charges (₹)",
                                                        placeholder = "0.0"
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    HorizontalDivider(color = DarkSlateBorder)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Calculated Taxable Value", style = MaterialTheme.typography.bodySmall, color = SlateTextLight, fontWeight = FontWeight.Medium)
                                            Text("Grand Total Receivable", style = MaterialTheme.typography.titleMedium, color = MatteBlack, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "₹${String.format("%,.2f", grandTotal)}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = ExecutiveBlue,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }

                            // Footer parameters
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardBorder()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Declaration & Footer Text", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)

                                    TradeFitTextField(value = remarks, onValueChange = { remarks = it }, label = "Remarks / Dispatch Notes", placeholder = "Specific logistics details")
                                    TradeFitTextField(value = termsAndConditions, onValueChange = { termsAndConditions = it }, label = "Terms & Conditions", placeholder = "Standard legal terms")
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = preparedBy, onValueChange = { preparedBy = it }, label = "Prepared By Staff", placeholder = "Staff user identity")
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            TradeFitTextField(value = "Authorized Signature Placeholder", onValueChange = { }, label = "Signatory Stamp", readOnly = true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive Search Popup Dialogs
    // 1. Customer Search Popup Dialog
    if (showCustomerSearch) {
        Dialog(onDismissRequest = { showCustomerSearch = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = CardBorder()
            ) {
                var custQuery by remember { mutableStateOf("") }
                val filteredCust = initialCustomers.filter {
                    it.companyName.contains(custQuery, ignoreCase = true) ||
                    it.gstin.contains(custQuery, ignoreCase = true)
                }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Search Active Customers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showCustomerSearch = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    TradeFitTextField(
                        value = custQuery,
                        onValueChange = { custQuery = it },
                        label = "Filter by Company Name / GSTIN",
                        placeholder = "Type company name..."
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCust) { cust ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OffWhite, RoundedCornerShape(8.dp))
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
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(cust.companyName, fontWeight = FontWeight.Bold, color = MatteBlack, style = MaterialTheme.typography.bodyMedium)
                                    Text("GSTIN: ${cust.gstin} | Mobile: ${cust.mobile}", color = SlateTextDark, style = MaterialTheme.typography.bodySmall)
                                    Text("Billing: ${cust.billingAddress}", color = SlateTextLight, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 2. Fabric Item Selector Dialog
    activeItemIndexForSearch?.let { itemIdx ->
        Dialog(onDismissRequest = { activeItemIndexForSearch = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = CardBorder()
            ) {
                var fabricQuery by remember { mutableStateOf("") }
                // Deduplicate stock standard items by name
                val uniqueStockFabrics = initialStockEntries.filter {
                    it.fabricName.contains(fabricQuery, ignoreCase = true) ||
                    it.lotNumber.contains(fabricQuery, ignoreCase = true)
                }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Search Fabric Inventory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { activeItemIndexForSearch = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    TradeFitTextField(
                        value = fabricQuery,
                        onValueChange = { fabricQuery = it },
                        label = "Filter by Fabric Quality / Lot Number",
                        placeholder = "Type fabric quality..."
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uniqueStockFabrics) { stk ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OffWhite, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val activeItem = items[itemIdx]
                                        // Pick and populate details from Stock
                                        items[itemIdx] = activeItem.copy(
                                            fabricName = stk.fabricName,
                                            colour = stk.colour,
                                            gsm = stk.gsm,
                                            width = stk.width,
                                            lotNumber = stk.lotNumber,
                                            ratePerKg = stk.ratePerKg,
                                            amount = activeItem.weightKg * stk.ratePerKg
                                        )
                                        activeItemIndexForSearch = null
                                    }
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(stk.fabricName, fontWeight = FontWeight.Bold, color = MatteBlack, style = MaterialTheme.typography.bodyMedium)
                                        Text(stk.colour, style = MaterialTheme.typography.bodyMedium, color = ExecutiveBlue)
                                    }
                                    Text("Lot: ${stk.lotNumber} | GSM: ${stk.gsm} | Width: ${stk.width} In", color = SlateTextDark, style = MaterialTheme.typography.bodySmall)
                                    Text("Rate / Kg: ₹${stk.ratePerKg} | Whse: ${stk.warehouse} | Rack: ${stk.rackNumber}", color = SlateTextLight, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. Roll Numbers Multi-selector Dialog
    activeItemIndexForRolls?.let { itemIdx ->
        Dialog(onDismissRequest = { activeItemIndexForRolls = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = CardBorder()
            ) {
                val item = items[itemIdx]
                // Mock roll numbers database associated with the active fabric lot
                val availableRolls = remember(item.fabricName, item.lotNumber) {
                    val rollsCount = if (item.fabricName.contains("AeroDry", ignoreCase = true)) 10
                    else if (item.fabricName.contains("ThermaShield", ignoreCase = true)) 6
                    else if (item.fabricName.contains("Supima", ignoreCase = true)) 8
                    else 5

                    val prefix = if (item.fabricName.contains("AeroDry", ignoreCase = true)) "ROLL-10"
                    else if (item.fabricName.contains("ThermaShield", ignoreCase = true)) "ROLL-20"
                    else "ROLL-30"

                    val baseWeight = if (item.fabricName.contains("AeroDry", ignoreCase = true)) 24.0
                    else if (item.fabricName.contains("ThermaShield", ignoreCase = true)) 45.0
                    else 30.0

                    (1..rollsCount).map { i ->
                        Pair("$prefix$i", baseWeight + (i % 3) * 1.5)
                    }
                }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Assign Roll Codes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Fabric: ${item.fabricName.ifEmpty { "N/A" }}", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                        }
                        IconButton(onClick = { activeItemIndexForRolls = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(color = DarkSlateBorder)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableRolls) { rollData ->
                            val rollCode = rollData.first
                            val rollW = rollData.second
                            val isSelected = item.rollNumbers.contains(rollCode)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ExecutiveBlue.copy(alpha = 0.12f) else OffWhite)
                                    .clickable {
                                        val updatedRolls = if (isSelected) {
                                            item.rollNumbers.filter { it != rollCode }
                                        } else {
                                            item.rollNumbers + rollCode
                                        }
                                        // Calculate sum of weights for selected rolls
                                        val calculatedWeight = updatedRolls.sumOf { r ->
                                            availableRolls.firstOrNull { it.first == r }?.second ?: 0.0
                                        }

                                        items[itemIdx] = item.copy(
                                            rollNumbers = updatedRolls,
                                            numberOfRolls = updatedRolls.size,
                                            weightKg = calculatedWeight,
                                            amount = calculatedWeight * item.ratePerKg
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(rollCode, fontWeight = FontWeight.Bold, color = MatteBlack)
                                    Text("Standard Weight: $rollW Kg", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        val updatedRolls = if (isSelected) {
                                            item.rollNumbers.filter { it != rollCode }
                                        } else {
                                            item.rollNumbers + rollCode
                                        }
                                        val calculatedWeight = updatedRolls.sumOf { r ->
                                            availableRolls.firstOrNull { it.first == r }?.second ?: 0.0
                                        }
                                        items[itemIdx] = item.copy(
                                            rollNumbers = updatedRolls,
                                            numberOfRolls = updatedRolls.size,
                                            weightKg = calculatedWeight,
                                            amount = calculatedWeight * item.ratePerKg
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { activeItemIndexForRolls = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                    ) {
                        Text("Apply Selected Rolls", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 2. Delivery Challan Print & PDF Review Modal sheet layout
@Composable
fun ChallanPreviewSheet(
    challan: DeliveryChallan,
    onDismiss: () -> Unit,
    onConvertProforma: () -> Unit,
    onDownloadPdf: () -> Unit,
    onSharePdf: () -> Unit
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
                    .testTag("challan_preview_modal"),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(12.dp),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Modal Action bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OffWhite)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Goods Dispatch Document Preview - ${challan.id}",
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
                            IconButton(onClick = { /* Print triggered */ }) {
                                Icon(Icons.Default.Print, contentDescription = "Trigger Print", tint = SlateTextDark)
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))

                            Button(
                                onClick = onConvertProforma,
                                colors = ButtonDefaults.buttonColors(containerColor = ProgressiveOrange)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Convert", tint = PureWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Convert to Proforma", color = PureWhite, fontWeight = FontWeight.Bold)
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

                    // Printed Invoice/Challan layout form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Commercial Letterhead
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("TRADEFIT ENTERPRISES & ERP", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = ExecutiveBlue)
                                Text("Integrated Textile Logistics & Apparel Warehouse Operations", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                Text("Shed 4A-4B, Industrial GIDC Apparel Hub, Surat, Gujarat - 395006", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                                Text("GSTIN: 24AAACT9923P1ZX | Email: accounts@tradefit.com", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ExecutiveBlue.copy(alpha = 0.15f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("DELIVERY CHALLAN", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Challan No: ${challan.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                Text("Date: ${challan.date}", style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
                            }
                        }

                        HorizontalDivider(color = MatteBlack, thickness = 2.dp)

                        // Customer Details and Transport info (Side-by-side columns)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            // Consignee details
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("CONSIGNEE / BILLED TO:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                Text(challan.companyName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                                Text("GSTIN: ${challan.gstNumber.ifEmpty { "N/A" }}", style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.SemiBold)
                                Text("Contact Mobile: ${challan.mobile}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Billing Address:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                Text(challan.billingAddress, style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Shipping Destination Address:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                Text(challan.shippingAddress, style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                            }

                            // Logistics transport details
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("LOGISTICS & TRANSPORT RECEIPT:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                DetailItemRow("Transport Name:", challan.transportName.ifEmpty { "Direct Dispatch" })
                                DetailItemRow("Vehicle Number:", challan.vehicleNumber.ifEmpty { "N/A" })
                                DetailItemRow("Driver's Name:", challan.driverName.ifEmpty { "N/A" })
                                DetailItemRow("Driver's Contact:", challan.driverMobile.ifEmpty { "N/A" })
                                DetailItemRow("LR / Way Bill Ref:", challan.lrNumber.ifEmpty { "N/A" })
                                DetailItemRow("E-Way Bill Number:", challan.eWayBill.ifEmpty { "Not Required / Exempt" })
                                DetailItemRow("Dispatch Location:", "${challan.dispatchFrom} -> ${challan.dispatchTo}")
                                DetailItemRow("Target Delivery Date:", challan.deliveryDate)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Grid items table
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("DISPATCHED GOODS CATALOG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Custom printed styled table
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OffWhite)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("S.No", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f))
                                Text("Fabric Quality / Specs", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.5f))
                                Text("Lot / Roll Code", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                                Text("Rolls Count", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                                Text("Weight (Kg)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                                Text("Rate/Kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                                Text("Valuation (₹)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                            }
                            HorizontalDivider(color = MatteBlack)

                            challan.items.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text((idx + 1).toString(), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.4f))
                                    Column(modifier = Modifier.weight(2.5f)) {
                                        Text(item.fabricName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
                                        Text("${item.colour} | ${item.gsm} GSM | ${item.width} inches", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                    }
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text("Lot: ${item.lotNumber}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("Rolls: ${item.rollNumbers.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                    }
                                    Text(item.numberOfRolls.toString(), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.8f))
                                    Text("${item.weightKg} Kg", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.8f))
                                    Text("₹${item.ratePerKg}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.8f))
                                    Text("₹${String.format("%,.2f", item.amount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.2f))
                                }
                                HorizontalDivider(color = DarkSlateBorder.copy(alpha = 0.5f))
                            }
                        }

                        // Bottom financial block + terms and conditions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            // Terms & conditions left side
                            Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("DECLARATION & TERMS:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                Text(challan.termsAndConditions, style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Remarks / Special logistics dispatch notes:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                Text(challan.remarks.ifEmpty { "No specific remarks or constraints noted." }, style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                            }

                            // Math totals list
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("COMPUTATION STATEMENT:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SlateTextLight)
                                SummaryMathRow("Total Dispatch Rolls:", "${challan.totalRolls} Rolls")
                                SummaryMathRow("Total Dispatch Net Weight:", "${challan.totalWeight} Kgs")
                                SummaryMathRow("Consigned Items Subtotal:", "₹${String.format("%,.2f", challan.subTotal)}")
                                if (challan.discountAmount > 0.0) {
                                    SummaryMathRow("Discount Applied (${challan.discountPercentOrAmt}):", "-₹${String.format("%,.2f", challan.discountAmount)}")
                                }
                                SummaryMathRow("Freight Agency Charges:", "₹${String.format("%,.2f", challan.freightCharges)}")
                                SummaryMathRow("Packing / Handling Charges:", "₹${String.format("%,.2f", challan.packingCharges)}")
                                SummaryMathRow("Other Misc Surcharge:", "₹${String.format("%,.2f", challan.otherCharges)}")
                                HorizontalDivider(color = DarkSlateBorder)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Grand Receivable Total:", fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyLarge, color = MatteBlack)
                                    Text("₹${String.format("%,.2f", challan.grandTotal)}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = ExecutiveBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Signature block
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Prepared By:", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(challan.preparedBy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MatteBlack)
                                HorizontalDivider(modifier = Modifier.width(180.dp), color = SlateTextLight)
                                Text("Dispatch Coordinator Signature", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Receiver's Acknowledgement Stamp:", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                Spacer(modifier = Modifier.height(32.dp))
                                HorizontalDivider(modifier = Modifier.width(220.dp), color = SlateTextLight)
                                Text("Customer / Driver Seal & Sign", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("For TRADEFIT ENTERPRISES:", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                                Spacer(modifier = Modifier.height(32.dp))
                                HorizontalDivider(modifier = Modifier.width(200.dp), color = SlateTextLight)
                                Text("Authorized Signatory Sign", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. Printing Label Modal
@Composable
fun ChallanPrintLabelModal(
    challan: DeliveryChallan,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("challan_print_label_modal"),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(12.dp),
            border = CardBorder()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Print Dispatch Thermal Labels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Send thermal dispatch tags for the ${challan.totalRolls} physical rolls assigned to dispatch note ${challan.id}.", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)

                HorizontalDivider(color = DarkSlateBorder)

                // Render barcode sticker visual
                Card(
                    colors = CardDefaults.cardColors(containerColor = OffWhite),
                    border = BorderStroke(1.dp, SlateTextLight.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TRADEFIT CARGO TAG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                            Text("Roll: 1 of ${challan.totalRolls}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }

                        Text("DOC REF: ${challan.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                        Text("Consignee: ${challan.companyName}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        
                        val firstItem = challan.items.firstOrNull()
                        if (firstItem != null) {
                            Text("Fabric: ${firstItem.fabricName}", style = MaterialTheme.typography.bodySmall)
                            Text("Colour: ${firstItem.colour} | Lot: ${firstItem.lotNumber}", style = MaterialTheme.typography.bodySmall)
                            Text("Roll Code: ${firstItem.rollNumbers.firstOrNull() ?: "ROLL-N/A"}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Fake barcode lines
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(Color.Black)
                        ) {}
                        Text("*${challan.id}*", modifier = Modifier.align(Alignment.CenterHorizontally), style = MaterialTheme.typography.labelSmall)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = SlateTextDark)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print", tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print All Labels", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, fontWeight = FontWeight.Medium)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SummaryMathRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.SemiBold)
    }
}

val initialDeliveryChallans = listOf(
    DeliveryChallan(
        id = "CH-000001",
        date = "2026-07-06",
        deliveryDate = "2026-07-08",
        customerId = "CUST-1001",
        companyName = "Evergreen Textiles Ltd.",
        gstNumber = "27AAACE1234F1Z8",
        mobile = "9876543210",
        billingAddress = "Plot 42, GIDC Apparel Park, Surat, Gujarat",
        shippingAddress = "Shed 12, Logistics Zone, Surat, Gujarat",
        transportName = "Safe Cargo Carriers",
        vehicleNumber = "GJ05-AX-1011",
        driverName = "Rajesh Kumar",
        driverMobile = "9812345678",
        lrNumber = "LR-90234",
        dispatchFrom = "Surat Whse",
        dispatchTo = "Customer Warehouse",
        eWayBill = "EWB-9081234901",
        items = listOf(
            DeliveryChallanItem(
                id = "ITEM-001",
                fabricName = "AeroDry Poly Spandex",
                colour = "Volt Green",
                gsm = 180,
                width = 60.0,
                lotNumber = "LOT-509A",
                rollNumbers = listOf("ROLL-101", "ROLL-102"),
                numberOfRolls = 2,
                weightKg = 48.0,
                ratePerKg = 320.0,
                amount = 15360.0
            )
        ),
        totalRolls = 2,
        totalWeight = 48.0,
        subTotal = 15360.0,
        discountPercentOrAmt = "Percent",
        discountValue = 5.0,
        discountAmount = 768.0,
        freightCharges = 1200.0,
        packingCharges = 350.0,
        otherCharges = 150.0,
        taxableAmount = 16300.0,
        grandTotal = 16300.0,
        remarks = "Deliver before evening dispatch cut-off time",
        termsAndConditions = "1. Goods once dispatched cannot be returned without written permission. 2. Any discrepancies must be reported within 24 hours.",
        preparedBy = "Karan Patel (Logistics Lead)",
        status = "Dispatched"
    ),
    DeliveryChallan(
        id = "CH-000002",
        date = "2026-07-05",
        deliveryDate = "2026-07-07",
        customerId = "CUST-1002",
        companyName = "Maharani Silks & Fabrics",
        gstNumber = "07AAACM4567M2Z4",
        mobile = "9312345678",
        billingAddress = "72, Chandni Chowk Commercial Block, New Delhi",
        shippingAddress = "Gate 4, Textile Logistics Depot, Delhi",
        transportName = "Delhi Fast Freight",
        vehicleNumber = "DL01-BC-8923",
        driverName = "Sukhvinder Singh",
        driverMobile = "9898765432",
        lrNumber = "LR-78110",
        dispatchFrom = "Delhi Whse",
        dispatchTo = "Delhi Retail Depot",
        eWayBill = "EWB-1120239401",
        items = listOf(
            DeliveryChallanItem(
                id = "ITEM-002",
                fabricName = "ThermaShield Brushed Fleece",
                colour = "Melange Grey",
                gsm = 280,
                width = 62.0,
                lotNumber = "LOT-382B",
                rollNumbers = listOf("ROLL-202"),
                numberOfRolls = 1,
                weightKg = 45.0,
                ratePerKg = 450.0,
                amount = 20250.0
            )
        ),
        totalRolls = 1,
        totalWeight = 45.0,
        subTotal = 20250.0,
        discountPercentOrAmt = "Amount",
        discountValue = 250.0,
        discountAmount = 250.0,
        freightCharges = 800.0,
        packingCharges = 150.0,
        otherCharges = 0.0,
        taxableAmount = 20950.0,
        grandTotal = 20950.0,
        remarks = "Brushed fleece premium selection",
        termsAndConditions = "Standard TradeFit Logistics terms applied.",
        preparedBy = "Anita Roy (Sales Coordinator)",
        status = "Approved"
    )
)


// ==========================================
// 7. PROFORMA INVOICE SCREEN
// ==========================================
// Note: The complete, desktop-first Proforma Invoice module has been refactored
// and is now fully implemented in com/example/ui/screens/ProformaInvoiceModule.kt.
// This prevents token limits and maintains modularity.



// ==========================================
// 8. TAX INVOICE SCREEN
// ==========================================
@Composable
fun TaxInvoiceScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Invoices") }
    val filters = listOf("All Invoices", "PAID", "PENDING")
    var showForm by remember { mutableStateOf(false) }

    val invoices = remember {
        listOf(
            Invoice("TX-2026-501", "Evergreen Textiles Ltd.", "₹1,85,400", "₹9,270 CGST / ₹9,270 SGST", "PAID", PositiveGrowth),
            Invoice("TX-2026-502", "Maharani Silks & Fabrics", "₹95,200", "₹17,136 IGST (Interstate)", "PENDING", PendingOrange)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(16.dp)
            .testTag("invoice_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Breadcrumb(path = listOf("Home", "Finance", "Tax Invoices"))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tax Invoices (Official Legal Statements)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MatteBlack,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showForm = !showForm },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text(if (showForm) "Close Form" else "+ Generate Tax Invoice", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showForm) {
            item {
                TaxInvoiceCreatorForm(onAdd = { showForm = false })
            }
        }

        item {
            SearchComponent(
                query = searchKeyword,
                onQueryChange = { searchKeyword = it },
                placeholder = "Search invoices by ID or customer company..."
            )
        }

        item {
            FilterComponent(
                selectedFilter = selectedFilter,
                availableFilters = filters,
                onFilterSelected = { selectedFilter = it }
            )
        }

        item {
            val filtered = invoices.filter {
                (selectedFilter == "All Invoices" || it.status.equals(selectedFilter, ignoreCase = true)) &&
                (it.id.contains(searchKeyword, ignoreCase = true) || it.customer.contains(searchKeyword, ignoreCase = true))
            }

            DataTable(
                headers = listOf("Invoice ID", "Customer Company", "Gross Amount", "Taxation Breakout", "Status")
            ) {
                if (filtered.isEmpty()) {
                    EmptyState(title = "No Invoices Found", message = "No tax invoices found matching parameters")
                } else {
                    filtered.forEach { invoice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(invoice.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text(invoice.customer, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.5f))
                            Text(invoice.subtotal, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(invoice.taxBreakout, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1.5f))
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                StatusBadge(status = invoice.status, color = invoice.color)
                            }
                        }
                        Divider(color = DarkSlateBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }

        item {
            TablePagination()
        }
    }
}

data class Invoice(val id: String, val customer: String, val subtotal: String, val taxBreakout: String, val status: String, val color: Color)

@Composable
fun TaxInvoiceCreatorForm(onAdd: () -> Unit) {
    var customer by remember { mutableStateOf("") }
    var baseVal by remember { mutableStateOf("") }
    var gstRate by remember { mutableStateOf("12") } // dropdown standard placeholder
    var stateType by remember { mutableStateOf("Intrastate (CGST + SGST)") }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("invoice_form"),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Create Legal Tax Invoice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
            TradeFitTextField(value = customer, onValueChange = { customer = it }, label = "Active Customer GST Registration", placeholder = "Select customer with GSTIN")
            TradeFitTextField(value = baseVal, onValueChange = { baseVal = it }, label = "Base Taxable Value (₹)", placeholder = "e.g., 100000")
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TradeFitTextField(value = gstRate, onValueChange = { gstRate = it }, label = "GST Standard % Rate", placeholder = "e.g., 5, 12, 18", modifier = Modifier.weight(1f))
                TradeFitTextField(value = stateType, onValueChange = { stateType = it }, label = "Tax Dispatch Mode", placeholder = "Intra / Interstate", modifier = Modifier.weight(1.5f))
            }

            // Calculation preview simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightBlueAccent)
                    .padding(12.dp)
            ) {
                Column {
                    Text("Live Invoice Calculation Breakout Preview:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Subtotal Taxable Value: ₹${baseVal.ifEmpty { "0" }}", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                    val percent = gstRate.toFloatOrNull() ?: 12f
                    val baseFloat = baseVal.toFloatOrNull() ?: 0f
                    val tax = (baseFloat * (percent / 100f))
                    Text("• GST Surcharge (${percent}%): ₹$tax", style = MaterialTheme.typography.bodySmall, color = SlateTextDark)
                    Text("• Estimated Net Payable Amount: ₹${baseFloat + tax}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                }
            }

            TradeFitButton(text = "Authorize Invoice & Generate PDFs", onClick = onAdd, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TaxInvoiceCardItem(invoice: Invoice) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("invoice_card_${invoice.id}"),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(invoice.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(invoice.color.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(invoice.status, style = MaterialTheme.typography.labelSmall, color = invoice.color, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(invoice.customer, style = MaterialTheme.typography.bodyLarge, color = SlateTextDark, fontWeight = FontWeight.SemiBold)
            Text(invoice.taxBreakout, style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkSlateBorder)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Invoice Net Payable", style = MaterialTheme.typography.labelSmall, color = SlateTextLight)
                    Text(invoice.subtotal, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ExecutiveBlue)
                }
                OutlinedButton(
                    onClick = { /* Export PDF stub */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ExecutiveBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExecutiveBlue),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// 9. REPORTS SCREEN
// ==========================================
@Composable
fun ReportsScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Reports") }
    val filters = listOf("All Reports", "Taxation", "Inventory", "Sales")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(16.dp)
            .testTag("reports_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Breadcrumb(path = listOf("Home", "Analytics", "Reports"))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analytical Reports & Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    color = MatteBlack,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { /* Action */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text("Refresh Analytics", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("report_chart_card"),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GST Taxation Liability Flow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                    Text("Aggregated monthly input/output tax credits over current fiscal cycle", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                    Spacer(modifier = Modifier.height(16.dp))
                    CanvasStockChart(dataPoints = listOf(20f, 32f, 41f, 38f, 55f, 63f, 52f, 75f))
                }
            }
        }

        item {
            SearchComponent(
                query = searchKeyword,
                onQueryChange = { searchKeyword = it },
                placeholder = "Search reports by name or identifier..."
            )
        }

        item {
            FilterComponent(
                selectedFilter = selectedFilter,
                availableFilters = filters,
                onFilterSelected = { selectedFilter = it }
            )
        }

        item {
            val reportsList = listOf(
                ReportEntry("REP-TAX-01", "GST-1 Surcharge Reconciliation Report", "Taxation", "Export CSV", Icons.Default.Assessment),
                ReportEntry("REP-STK-02", "Roll Ageing and Idle Inventory Ledger", "Inventory", "Review Online", Icons.Default.Storage),
                ReportEntry("REP-SAL-03", "Customer-wise Fabric Turnover Analytics", "Sales", "Print Report", Icons.Default.Group)
            )
            val filtered = reportsList.filter {
                (selectedFilter == "All Reports" || it.type == selectedFilter) &&
                (it.name.contains(searchKeyword, ignoreCase = true) || it.code.contains(searchKeyword, ignoreCase = true))
            }

            DataTable(
                headers = listOf("Report Code", "Report Title Description", "Category", "Action Options")
            ) {
                if (filtered.isEmpty()) {
                    EmptyState(title = "No Reports Found", message = "No analytical reports match criteria")
                } else {
                    filtered.forEach { report ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(report.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.8f)) {
                                Icon(imageVector = report.icon, contentDescription = null, tint = ExecutiveBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(report.name, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Medium)
                            }
                            Text(report.type, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                            TextButton(
                                onClick = { /* Report dispatch */ },
                                colors = ButtonDefaults.textButtonColors(contentColor = ExecutiveBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(report.action, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        Divider(color = DarkSlateBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }

        item {
            TablePagination()
        }
    }
}

data class ReportEntry(val code: String, val name: String, val type: String, val action: String, val icon: ImageVector)

@Composable
fun ReportRowItem(report: ReportEntry) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("report_row_${report.code}"),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(LightBlueAccent), contentAlignment = Alignment.Center) {
                    Icon(imageVector = report.icon, contentDescription = null, tint = ExecutiveBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(report.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                    Text("${report.type} • ${report.code}", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                }
            }
            TextButton(onClick = { /* Report dispatch */ }, colors = ButtonDefaults.textButtonColors(contentColor = ExecutiveBlue)) {
                Text(report.action, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ==========================================
// 10. SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var companyName by remember { mutableStateOf("TradeFit Textiles Pvt Ltd") }
    var baseGstin by remember { mutableStateOf("24AAACT8819G1Z2") }
    var firebaseSync by remember { mutableStateOf(true) }
    var notificationSetting by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Breadcrumb(path = listOf("Home", "System", "Configurations"))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TradeFit ERP System Configurations",
                style = MaterialTheme.typography.titleLarge,
                color = MatteBlack,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { /* Action */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
            ) {
                Text("Save Configurations", fontWeight = FontWeight.Bold)
            }
        }

        // Organization Info Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("settings_org_card"),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enterprise Profile Identity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                TradeFitTextField(value = companyName, onValueChange = { companyName = it }, label = "Company Business Name", placeholder = "Enter trade register name")
                TradeFitTextField(value = baseGstin, onValueChange = { baseGstin = it }, label = "Primary Dispatch GSTIN", placeholder = "GSTIN of HQ Warehouse")
            }
        }

        // Integration Status
        Card(
            modifier = Modifier.fillMaxWidth().testTag("settings_sync_card"),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Database & Firebase Synclink", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MatteBlack)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cloud Firestore Synchronization", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MatteBlack)
                        Text("Sync local transactions, customer masters & stock ledger with cloud databases automatically", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                    }
                    Switch(checked = firebaseSync, onCheckedChange = { firebaseSync = it }, colors = SwitchDefaults.colors(checkedThumbColor = ExecutiveBlue, checkedTrackColor = LightBlueAccent))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Active Dispatch Alerts", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MatteBlack)
                        Text("Send push alerts when goods dispatch challans are authorized", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
                    }
                    Switch(checked = notificationSetting, onCheckedChange = { notificationSetting = it }, colors = SwitchDefaults.colors(checkedThumbColor = ExecutiveBlue, checkedTrackColor = LightBlueAccent))
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = DarkSlateBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(PositiveGrowth))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Firebase Cloud Services Connection Status: ACTIVE", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PositiveGrowth)
                }
            }
        }

        // Version & Developer branding
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TradeFit ERP Client Shell Version 1.0.0", style = MaterialTheme.typography.labelSmall, color = SlateTextLight, fontWeight = FontWeight.Bold)
                Text("Developed with Jetpack Compose & Google Firebase", style = MaterialTheme.typography.bodySmall, color = SlateTextLight)
            }
        }
    }
}

// ==========================================
// BREADCRUMB COMPOSABLE
// ==========================================
@Composable
fun Breadcrumb(path: List<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        path.forEachIndexed { index, segment ->
            Text(
                text = segment,
                style = MaterialTheme.typography.bodySmall,
                color = if (index == path.size - 1) ExecutiveBlue else SlateTextLight,
                fontWeight = if (index == path.size - 1) FontWeight.Bold else FontWeight.Normal
            )
            if (index < path.size - 1) {
                Text(
                    text = "  ›  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextLight
                )
            }
        }
    }
}

// ==========================================
// PAGINATION COMPOSABLE
// ==========================================
@Composable
fun TablePagination(
    currentPage: Int = 1,
    totalPages: Int = 5,
    onPageSelected: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Showing 1 to 5 of 25 records",
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextLight
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Arrow button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
                    .border(1.dp, DarkSlateBorder, RoundedCornerShape(8.dp))
                    .clickable { if (currentPage > 1) onPageSelected(currentPage - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", fontWeight = FontWeight.Bold, color = MatteBlack)
            }

            (1..totalPages).forEach { page ->
                val isSelected = page == currentPage
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ExecutiveBlue else PureWhite)
                        .border(
                            1.dp,
                            if (isSelected) ExecutiveBlue else DarkSlateBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onPageSelected(page) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = page.toString(),
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureWhite else MatteBlack,
                        fontSize = 12.sp
                    )
                }
            }

            // Right Arrow button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
                    .border(1.dp, DarkSlateBorder, RoundedCornerShape(8.dp))
                    .clickable { if (currentPage < totalPages) onPageSelected(currentPage + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("›", fontWeight = FontWeight.Bold, color = MatteBlack)
            }
        }
    }
}

// ==========================================
// 11. PAYMENTS SCREEN
// ==========================================
@Composable
fun PaymentsScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Completed", "Pending", "Failed")

    val mockPayments = listOf(
        MockPayment("TXN-9011", "Evergreen Textiles Ltd.", "Bank Transfer", "₹1,45,000", "Completed", PositiveGrowth),
        MockPayment("TXN-9012", "Maharani Silks & Fabrics", "UPI / GPay", "₹75,000", "Completed", PositiveGrowth),
        MockPayment("TXN-9013", "Vardhman Hosiery Co.", "NEFT Transfer", "₹2,10,000", "Pending", PendingOrange),
        MockPayment("TXN-9014", "Royal Garments House", "Cash Payment", "₹32,500", "Failed", WarningAlert)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(16.dp)
            .testTag("payments_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Breadcrumb(path = listOf("Home", "Finance", "Payments"))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment Transactions Log",
                    style = MaterialTheme.typography.titleLarge,
                    color = MatteBlack,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { /* Action */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text("+ Record Receipt", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            SearchComponent(
                query = searchKeyword,
                onQueryChange = { searchKeyword = it },
                placeholder = "Search by Transaction ID or Customer name..."
            )
        }

        item {
            FilterComponent(
                selectedFilter = selectedFilter,
                availableFilters = filters,
                onFilterSelected = { selectedFilter = it }
            )
        }

        item {
            val filtered = mockPayments.filter {
                (selectedFilter == "All" || it.status.equals(selectedFilter, ignoreCase = true)) &&
                (it.customer.contains(searchKeyword, ignoreCase = true) || it.id.contains(searchKeyword, ignoreCase = true))
            }

            DataTable(
                headers = listOf("Payment ID", "Customer Name", "Method", "Amount", "Status")
            ) {
                if (filtered.isEmpty()) {
                    EmptyState(
                        title = "No Transactions Found",
                        message = "Try clearing search keywords or choosing a different filter category"
                    )
                } else {
                    filtered.forEach { payment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(payment.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text(payment.customer, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Text(payment.method, style = MaterialTheme.typography.bodySmall, color = SlateTextDark, modifier = Modifier.weight(1f))
                            Text(payment.amount, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                StatusBadge(status = payment.status, color = payment.color)
                            }
                        }
                        Divider(color = DarkSlateBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }

        item {
            TablePagination()
        }
    }
}

data class MockPayment(val id: String, val customer: String, val method: String, val amount: String, val status: String, val color: Color)

// ==========================================
// 12. LEDGER SCREEN
// ==========================================
@Composable
fun LedgerScreen(modifier: Modifier = Modifier) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Transactions") }
    val filters = listOf("All Transactions", "Debits (Dr)", "Credits (Cr)")

    val mockLedgers = listOf(
        MockLedger("06 Jul 2026", "Cotton Twill Sales invoice #INV-2041", "₹0.00", "₹1,45,000", "₹1,45,000 Cr"),
        MockLedger("05 Jul 2026", "Bank Transfer Received CUST-1001", "₹1,45,000", "₹0.00", "₹0.00 Bal"),
        MockLedger("04 Jul 2026", "Silk Satin Sales invoice #INV-2042", "₹0.00", "₹75,000", "₹75,000 Cr"),
        MockLedger("03 Jul 2026", "Linen Delivery Freight Surcharge #CH-9021", "₹12,500", "₹0.00", "₹87,500 Dr")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(16.dp)
            .testTag("ledger_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Breadcrumb(path = listOf("Home", "Finance", "General Ledger"))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "General Finance Ledger",
                    style = MaterialTheme.typography.titleLarge,
                    color = MatteBlack,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { /* Action */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                ) {
                    Text("Export Statement", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            SearchComponent(
                query = searchKeyword,
                onQueryChange = { searchKeyword = it },
                placeholder = "Search ledger narrative..."
            )
        }

        item {
            FilterComponent(
                selectedFilter = selectedFilter,
                availableFilters = filters,
                onFilterSelected = { selectedFilter = it }
            )
        }

        item {
            val filtered = mockLedgers.filter {
                (selectedFilter == "All Transactions" ||
                 (selectedFilter == "Debits (Dr)" && it.debit != "₹0.00") ||
                 (selectedFilter == "Credits (Cr)" && it.credit != "₹0.00")) &&
                it.particulars.contains(searchKeyword, ignoreCase = true)
            }

            DataTable(
                headers = listOf("Date", "Narrative Particulars", "Debit (Dr)", "Credit (Cr)", "Cumulative")
            ) {
                if (filtered.isEmpty()) {
                    EmptyState(
                        title = "No Ledger Entries",
                        message = "No financial ledger transactions match your queries"
                    )
                } else {
                    filtered.forEach { ledger ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ledger.date, style = MaterialTheme.typography.bodySmall, color = SlateTextLight, modifier = Modifier.weight(1f))
                            Text(ledger.particulars, style = MaterialTheme.typography.bodySmall, color = MatteBlack, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.5f))
                            Text(ledger.debit, style = MaterialTheme.typography.bodySmall, color = if (ledger.debit != "₹0.00") WarningAlert else SlateTextLight, modifier = Modifier.weight(1f))
                            Text(ledger.credit, style = MaterialTheme.typography.bodySmall, color = if (ledger.credit != "₹0.00") PositiveGrowth else SlateTextLight, modifier = Modifier.weight(1f))
                            Text(ledger.balance, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MatteBlack, modifier = Modifier.weight(1f))
                        }
                        Divider(color = DarkSlateBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }

        item {
            TablePagination()
        }
    }
}

data class MockLedger(val date: String, val particulars: String, val debit: String, val credit: String, val balance: String)

