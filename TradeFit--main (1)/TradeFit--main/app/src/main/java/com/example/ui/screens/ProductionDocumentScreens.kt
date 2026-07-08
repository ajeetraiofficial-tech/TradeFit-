package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backend.model.DeliveryChallan as BackendDeliveryChallan
import com.example.backend.model.ProformaInvoice as BackendProformaInvoice
import com.example.backend.model.TaxInvoice as BackendTaxInvoice
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PositiveGrowth
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.WarningAlert
import kotlinx.coroutines.launch

@Composable
fun DeliveryChallanScreen(
    modifier: Modifier = Modifier,
    onNavigateToProforma: () -> Unit = {}
) {
    val vm: DocumentWorkflowViewModel = viewModel()
    val ui by vm.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var editing by remember { mutableStateOf<DocumentFormState?>(null) }
    val context = LocalContext.current

    DocumentScreenFrame(
        title = "Delivery Challan",
        subtitle = "Create, approve, dispatch, print and convert goods dispatch notes.",
        loading = ui.loading,
        saving = ui.saving,
        message = ui.message,
        error = ui.error,
        onClearMessage = vm::clearMessage,
        onRefresh = vm::refresh,
        onAdd = { editing = DocumentFormState() },
        modifier = modifier
    ) {
        DocumentSearchFilter(query, { query = it }, filter, listOf("All", "Draft", "Approved", "Dispatched"), { filter = it })
        val items = ui.challans.filter {
            (filter == "All" || it.status == filter) &&
                (it.id.contains(query, true) || it.companyName.contains(query, true) || it.lrNumber.contains(query, true))
        }
        if (items.isEmpty()) EmptyDocumentState("No delivery challans", "Create the first challan after selecting customer, fabric and roll.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp)) {
            items(items, key = { it.id }) { challan ->
                DeliveryChallanCard(
                    challan = challan,
                    onEdit = { editing = challan.toForm() },
                    onDelete = { vm.deleteChallan(challan.id) },
                    onApprove = { vm.approveChallan(challan.id) },
                    onDispatch = { vm.dispatchChallan(challan.id) },
                    onConvert = {
                        vm.convertChallanToProforma(challan)
                        onNavigateToProforma()
                    },
                    onPdf = { Toast.makeText(context, DocumentPdfActions.createDeliveryChallanPdf(context, challan).name, Toast.LENGTH_SHORT).show() },
                    onShare = { DocumentPdfActions.share(context, DocumentPdfActions.createDeliveryChallanPdf(context, challan)) },
                    onPrint = { DocumentPdfActions.print(context, DocumentPdfActions.createDeliveryChallanPdf(context, challan)) }
                )
            }
        }
    }

    if (editing != null) {
        DocumentEditorSheet(
            title = if (editing?.id.isNullOrBlank()) "Create Delivery Challan" else "Edit Delivery Challan",
            initial = editing!!,
            ui = ui,
            includeDispatch = true,
            includePayment = false,
            onDismiss = { editing = null },
            onSave = {
                vm.saveChallan(it)
                editing = null
            }
        )
    }
}

@Composable
fun ProformaInvoiceScreen(modifier: Modifier = Modifier) {
    val vm: DocumentWorkflowViewModel = viewModel()
    val ui by vm.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var editing by remember { mutableStateOf<DocumentFormState?>(null) }
    val context = LocalContext.current

    DocumentScreenFrame(
        title = "Proforma Invoice",
        subtitle = "Convert challans to quotations, edit, share, print and convert to tax invoices.",
        loading = ui.loading,
        saving = ui.saving,
        message = ui.message,
        error = ui.error,
        onClearMessage = vm::clearMessage,
        onRefresh = vm::refresh,
        onAdd = { editing = DocumentFormState() },
        modifier = modifier
    ) {
        DocumentSearchFilter(query, { query = it }, filter, listOf("All", "Draft", "Converted", "Approved"), { filter = it })
        val items = ui.proformas.filter {
            (filter == "All" || it.status == filter) &&
                (it.id.contains(query, true) || it.companyName.contains(query, true) || it.referenceNumber.contains(query, true))
        }
        if (items.isEmpty()) EmptyDocumentState("No proforma invoices", "Convert an approved challan or create a direct proforma.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp)) {
            items(items, key = { it.id }) { invoice ->
                ProformaCard(
                    invoice = invoice,
                    onEdit = { editing = invoice.toForm() },
                    onDelete = { vm.deleteProforma(invoice.id) },
                    onConvert = { vm.convertProformaToTaxInvoice(invoice) },
                    onPdf = { Toast.makeText(context, DocumentPdfActions.createProformaPdf(context, invoice).name, Toast.LENGTH_SHORT).show() },
                    onShare = { DocumentPdfActions.share(context, DocumentPdfActions.createProformaPdf(context, invoice)) },
                    onPrint = { DocumentPdfActions.print(context, DocumentPdfActions.createProformaPdf(context, invoice)) }
                )
            }
        }
    }

    if (editing != null) {
        DocumentEditorSheet(
            title = if (editing?.id.isNullOrBlank()) "Create Proforma Invoice" else "Edit Proforma Invoice",
            initial = editing!!,
            ui = ui,
            includeDispatch = false,
            includePayment = false,
            onDismiss = { editing = null },
            onSave = {
                vm.saveProforma(it)
                editing = null
            }
        )
    }
}

@Composable
fun TaxInvoiceScreen(modifier: Modifier = Modifier) {
    val vm: DocumentWorkflowViewModel = viewModel()
    val ui by vm.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var editing by remember { mutableStateOf<DocumentFormState?>(null) }
    val context = LocalContext.current

    DocumentScreenFrame(
        title = "Tax Invoice",
        subtitle = "GST invoices with payment status, outstanding amount, PDF, share and print.",
        loading = ui.loading,
        saving = ui.saving,
        message = ui.message,
        error = ui.error,
        onClearMessage = vm::clearMessage,
        onRefresh = vm::refresh,
        onAdd = { editing = DocumentFormState() },
        modifier = modifier
    ) {
        DocumentSearchFilter(query, { query = it }, filter, listOf("All", "Unpaid", "Partially Paid", "Paid"), { filter = it })
        val items = ui.taxInvoices.filter {
            (filter == "All" || it.status == filter) &&
                (it.id.contains(query, true) || it.invoiceNumber.contains(query, true) || it.companyName.contains(query, true))
        }
        if (items.isEmpty()) EmptyDocumentState("No tax invoices", "Convert a proforma or create a direct GST invoice.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp)) {
            items(items, key = { it.id }) { invoice ->
                TaxInvoiceCard(
                    invoice = invoice,
                    onEdit = { editing = invoice.toForm() },
                    onDelete = { vm.deleteTaxInvoice(invoice.id) },
                    onPdf = { Toast.makeText(context, DocumentPdfActions.createTaxInvoicePdf(context, invoice).name, Toast.LENGTH_SHORT).show() },
                    onShare = { DocumentPdfActions.share(context, DocumentPdfActions.createTaxInvoicePdf(context, invoice)) },
                    onPrint = { DocumentPdfActions.print(context, DocumentPdfActions.createTaxInvoicePdf(context, invoice)) }
                )
            }
        }
    }

    if (editing != null) {
        DocumentEditorSheet(
            title = if (editing?.id.isNullOrBlank()) "Create Tax Invoice" else "Edit Tax Invoice",
            initial = editing!!,
            ui = ui,
            includeDispatch = false,
            includePayment = true,
            onDismiss = { editing = null },
            onSave = {
                vm.saveTaxInvoice(it)
                editing = null
            }
        )
    }
}

@Composable
private fun DocumentScreenFrame(
    title: String,
    subtitle: String,
    loading: Boolean,
    saving: Boolean,
    message: String?,
    error: String?,
    onClearMessage: () -> Unit,
    onRefresh: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(message, error) {
        val text = message ?: error
        if (text != null) {
            snackbarHostState.showSnackbar(text)
            onClearMessage()
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New") },
                containerColor = ExecutiveBlue,
                contentColor = PureWhite
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
                }
                IconButton(onClick = { scope.launch { onRefresh() } }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
            if (loading || saving) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(if (saving) "Saving ERP transaction..." else "Loading Firestore records...")
                }
            }
            content()
        }
    }
}

@Composable
private fun DocumentSearchFilter(
    query: String,
    onQuery: (String) -> Unit,
    selected: String,
    filters: List<String>,
    onFilter: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach {
                FilterChip(selected = selected == it, onClick = { onFilter(it) }, label = { Text(it) })
            }
        }
    }
}

@Composable
private fun EmptyDocumentState(title: String, subtitle: String) {
    Card(colors = CardDefaults.cardColors(containerColor = PureWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SlateTextDark)
        }
    }
}

@Composable
private fun DeliveryChallanCard(
    challan: BackendDeliveryChallan,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onApprove: () -> Unit,
    onDispatch: () -> Unit,
    onConvert: () -> Unit,
    onPdf: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit
) {
    DocumentCardShell(challan.id, challan.companyName, challan.status, challan.grandTotal) {
        InfoLine("Dispatch", "${challan.deliveryDate}  ${challan.transportName}  ${challan.vehicleNumber}")
        InfoLine("LR / Driver", "${challan.lrNumber.ifBlank { "No LR" }}  ${challan.driverName}")
        ActionRow {
            SmallAction(Icons.Default.Edit, "Edit", onEdit)
            SmallAction(Icons.Default.CheckCircle, "Approve", onApprove)
            SmallAction(Icons.Default.LocalShipping, "Dispatch", onDispatch)
            SmallAction(Icons.Default.SwapHoriz, "Proforma", onConvert)
            SmallAction(Icons.Default.PictureAsPdf, "PDF", onPdf)
            SmallAction(Icons.Default.Share, "Share", onShare)
            SmallAction(Icons.Default.Print, "Print", onPrint)
            SmallAction(Icons.Default.Delete, "Delete", onDelete, danger = true)
        }
    }
}

@Composable
private fun ProformaCard(
    invoice: BackendProformaInvoice,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConvert: () -> Unit,
    onPdf: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit
) {
    DocumentCardShell(invoice.id, invoice.companyName, invoice.status, invoice.grandTotal) {
        InfoLine("Validity", invoice.validityDate)
        InfoLine("Reference", invoice.referenceNumber)
        ActionRow {
            SmallAction(Icons.Default.Edit, "Edit", onEdit)
            SmallAction(Icons.Default.SwapHoriz, "Tax", onConvert)
            SmallAction(Icons.Default.PictureAsPdf, "PDF", onPdf)
            SmallAction(Icons.Default.Share, "Share", onShare)
            SmallAction(Icons.Default.Print, "Print", onPrint)
            SmallAction(Icons.Default.Delete, "Delete", onDelete, danger = true)
        }
    }
}

@Composable
private fun TaxInvoiceCard(
    invoice: BackendTaxInvoice,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPdf: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit
) {
    DocumentCardShell(invoice.invoiceNumber.ifBlank { invoice.id }, invoice.companyName, invoice.status, invoice.grandTotal) {
        InfoLine("GST / Outstanding", "${invoice.gstNumber}  ${currency(invoice.balanceDue)} due")
        InfoLine("Payment", "${currency(invoice.amountPaid)} received")
        ActionRow {
            SmallAction(Icons.Default.Edit, "Edit", onEdit)
            SmallAction(Icons.Default.PictureAsPdf, "PDF", onPdf)
            SmallAction(Icons.Default.Share, "Share", onShare)
            SmallAction(Icons.Default.Print, "Print", onPrint)
            SmallAction(Icons.Default.Delete, "Delete", onDelete, danger = true)
        }
    }
}

@Composable
private fun DocumentCardShell(id: String, customer: String, status: String, amount: Double, body: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(id, color = ExecutiveBlue, fontWeight = FontWeight.Bold)
                    Text(customer, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    AssistChip(onClick = {}, label = { Text(status) })
                    Text(currency(amount), fontWeight = FontWeight.Bold)
                }
            }
            body()
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Text("$label: $value", style = MaterialTheme.typography.bodyMedium, color = SlateTextDark, maxLines = 2, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun ActionRow(content: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        content()
    }
}

@Composable
private fun SmallAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit, danger: Boolean = false) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(icon, contentDescription = label, tint = if (danger) WarningAlert else ExecutiveBlue, modifier = Modifier.size(19.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentEditorSheet(
    title: String,
    initial: DocumentFormState,
    ui: DocumentWorkflowUiState,
    includeDispatch: Boolean,
    includePayment: Boolean,
    onDismiss: () -> Unit,
    onSave: (DocumentFormState) -> Unit
) {
    var form by remember(initial) { mutableStateOf(initial) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Totals update automatically from quantity, rate, GST, discount and transport charges.", color = SlateTextDark)
            }
            item {
                SelectorField(
                    label = "Customer",
                    value = ui.customers.firstOrNull { it.id == form.customerId }?.companyName ?: "Select customer",
                    values = ui.customers.map { it.id to it.companyName },
                    onSelected = { form = form.copy(customerId = it) }
                )
            }
            item {
                SelectorField(
                    label = "Fabric",
                    value = ui.fabrics.firstOrNull { it.code == form.fabricCode }?.name ?: "Select fabric",
                    values = ui.fabrics.map { it.code to "${it.name}  ${it.gstPercentage}% GST" },
                    onSelected = { code ->
                        val fabric = ui.fabrics.firstOrNull { it.code == code }
                        form = form.copy(fabricCode = code, gstPercent = fabric?.gstPercentage?.toString() ?: form.gstPercent, ratePerKg = fabric?.ratePerKg?.takeIf { it > 0.0 }?.toString() ?: form.ratePerKg)
                    }
                )
            }
            item {
                SelectorField(
                    label = "Roll",
                    value = ui.rolls.firstOrNull { it.id == form.stockEntryId }?.let { "${it.rollNumber} - ${it.netWeight} kg" } ?: "Select available roll",
                    values = ui.rolls.filter { form.fabricCode.isBlank() || it.fabricCode == form.fabricCode }.map { it.id to "${it.rollNumber}  ${it.netWeight} kg  ${it.status}" },
                    onSelected = { id ->
                        val roll = ui.rolls.firstOrNull { it.id == id }
                        form = form.copy(stockEntryId = id, quantityKg = roll?.netWeight?.takeIf { it > 0.0 }?.toString() ?: form.quantityKg)
                    }
                )
            }
            item { NumberRow("Quantity Kg", form.quantityKg, { form = form.copy(quantityKg = it) }, "Rate / Kg", form.ratePerKg, { form = form.copy(ratePerKg = it) }) }
            item { NumberRow("GST %", form.gstPercent, { form = form.copy(gstPercent = it) }, "Discount", form.discountValue, { form = form.copy(discountValue = it) }) }
            item { NumberRow("Freight", form.freightCharges, { form = form.copy(freightCharges = it) }, "Packing", form.packingCharges, { form = form.copy(packingCharges = it) }) }
            item { TradeText("Other Charges", form.otherCharges) { form = form.copy(otherCharges = it) } }
            if (includeDispatch) {
                item { TradeText("Dispatch Date", form.dispatchDate) { form = form.copy(dispatchDate = it) } }
                item { TradeText("Transport", form.transportName) { form = form.copy(transportName = it) } }
                item { NumberRow("Vehicle", form.vehicleNumber, { form = form.copy(vehicleNumber = it) }, "LR Number", form.lrNumber, { form = form.copy(lrNumber = it) }) }
                item { NumberRow("Driver", form.driverName, { form = form.copy(driverName = it) }, "Driver Mobile", form.driverMobile, { form = form.copy(driverMobile = it) }) }
            } else {
                item { TradeText("Transport", form.transportName) { form = form.copy(transportName = it) } }
            }
            if (includePayment) {
                item { TradeText("Payment Received", form.paymentReceived) { form = form.copy(paymentReceived = it) } }
            }
            item { TradeText("Remarks", form.remarks) { form = form.copy(remarks = it) } }
            item {
                Surface(color = OffWhite, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Taxable: ${currency(form.taxable)}")
                        Text("GST: ${currency(form.gstAmount)}")
                        Text("Grand Total: ${currency(form.grandTotal)}", fontWeight = FontWeight.Bold, color = PositiveGrowth)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(onClick = { onSave(form) }, modifier = Modifier.weight(1f)) { Text("Save") }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SelectorField(label: String, value: String, values: List<Pair<String, String>>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            label = { Text(label) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            values.forEach { (id, text) ->
                DropdownMenuItem(text = { Text(text) }, onClick = {
                    onSelected(id)
                    expanded = false
                })
            }
            if (values.isEmpty()) {
                DropdownMenuItem(text = { Text("No records available") }, onClick = { expanded = false })
            }
        }
    }
}

@Composable
private fun NumberRow(labelA: String, valueA: String, onA: (String) -> Unit, labelB: String, valueB: String, onB: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TradeText(labelA, valueA, onA, Modifier.weight(1f))
        TradeText(labelB, valueB, onB, Modifier.weight(1f))
    }
}

@Composable
private fun TradeText(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, modifier = modifier.fillMaxWidth(), singleLine = true)
}

private fun BackendDeliveryChallan.toForm() = DocumentFormState(
    id = id,
    customerId = customerId,
    quantityKg = totalWeight.toString(),
    ratePerKg = if (totalWeight > 0) (subTotal / totalWeight).toString() else "",
    transportName = transportName,
    vehicleNumber = vehicleNumber,
    driverName = driverName,
    driverMobile = driverMobile,
    lrNumber = lrNumber,
    dispatchDate = deliveryDate,
    discountValue = discountAmount.toString(),
    freightCharges = freightCharges.toString(),
    packingCharges = packingCharges.toString(),
    otherCharges = otherCharges.toString(),
    remarks = remarks
)

private fun BackendProformaInvoice.toForm() = DocumentFormState(
    id = id,
    customerId = customerId,
    quantityKg = totalWeight.toString(),
    ratePerKg = if (totalWeight > 0) (subTotal / totalWeight).toString() else "",
    transportName = transportName,
    discountValue = discountAmount.toString(),
    freightCharges = freightCharges.toString(),
    packingCharges = packingCharges.toString(),
    otherCharges = otherCharges.toString(),
    remarks = remarks
)

private fun BackendTaxInvoice.toForm() = DocumentFormState(
    id = id,
    customerId = customerId,
    quantityKg = totalWeight.toString(),
    ratePerKg = if (totalWeight > 0) (subTotal / totalWeight).toString() else "",
    transportName = transportName,
    discountValue = discountAmount.toString(),
    freightCharges = freightCharges.toString(),
    packingCharges = packingCharges.toString(),
    otherCharges = otherCharges.toString(),
    remarks = remarks,
    paymentReceived = amountPaid.toString()
)

private fun currency(value: Double) = "Rs. ${String.format("%,.2f", value)}"
