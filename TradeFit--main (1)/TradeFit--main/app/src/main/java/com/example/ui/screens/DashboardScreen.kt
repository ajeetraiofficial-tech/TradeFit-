package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock chart data for stock levels
    val stockLevelData = remember { listOf(40f, 45f, 42f, 55f, 52f, 60f, 58f, 72f, 68f, 85f, 80f, 95f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("dashboard_root"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcoming & Date row
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Welcome Back, Administrator",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MatteBlack,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TradeFit ERP - Fabric Supplier HQ • Live Operations Status",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextLight
                )
            }
        }

        // Stats grid
        item {
            // Stats grid layout showing high density 8-card matrix
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Today's Sales",
                        value = "₹1,45,000",
                        icon = Icons.Default.CurrencyRupee,
                        modifier = Modifier.weight(1f),
                        trend = "+8.2%",
                        isPositive = true,
                        testTagStr = "stat_todays_sales",
                        containerColor = PureWhite,
                        contentColor = MatteBlack,
                        iconBgColor = LightBlueAccent,
                        iconColor = ExecutiveBlue
                    )
                    StatCard(
                        title = "Today's Challans",
                        value = "12 Active",
                        icon = Icons.Default.LocalShipping,
                        modifier = Modifier.weight(1f),
                        trend = "+2.4%",
                        isPositive = true,
                        testTagStr = "stat_todays_challans",
                        containerColor = LightBlueAccent,
                        contentColor = DeepSlateBlue,
                        iconBgColor = PureWhite,
                        iconColor = ExecutiveBlue
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Today's Invoices",
                        value = "8 Generated",
                        icon = Icons.Default.ReceiptLong,
                        modifier = Modifier.weight(1f),
                        trend = "+12.1%",
                        isPositive = true,
                        testTagStr = "stat_todays_invoices",
                        containerColor = LavenderContainer,
                        contentColor = DeepLavenderText,
                        iconBgColor = PureWhite,
                        iconColor = DeepLavenderText
                    )
                    StatCard(
                        title = "Pending Payments",
                        value = "₹3,20,000",
                        icon = Icons.Default.Payments,
                        modifier = Modifier.weight(1f),
                        trend = "-1.8%",
                        isPositive = true,
                        testTagStr = "stat_pending_payments",
                        containerColor = PureWhite,
                        contentColor = MatteBlack,
                        iconBgColor = LightBlueAccent,
                        iconColor = ExecutiveBlue
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Customers",
                        value = "124 Active",
                        icon = Icons.Default.Group,
                        modifier = Modifier.weight(1f),
                        trend = "+4.3%",
                        isPositive = true,
                        testTagStr = "stat_total_customers",
                        containerColor = PureWhite,
                        contentColor = MatteBlack,
                        iconBgColor = LightBlueAccent,
                        iconColor = ExecutiveBlue
                    )
                    StatCard(
                        title = "Total Fabric Stock",
                        value = "8,500 Kg",
                        icon = Icons.Default.Layers,
                        modifier = Modifier.weight(1f),
                        trend = "+10.5%",
                        isPositive = true,
                        testTagStr = "stat_fabric_stock_kg",
                        containerColor = LightBlueAccent,
                        contentColor = DeepSlateBlue,
                        iconBgColor = PureWhite,
                        iconColor = ExecutiveBlue
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Rolls",
                        value = "340 Rolls",
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f),
                        trend = "+15.0%",
                        isPositive = true,
                        testTagStr = "stat_total_rolls",
                        containerColor = LavenderContainer,
                        contentColor = DeepLavenderText,
                        iconBgColor = PureWhite,
                        iconColor = DeepLavenderText
                    )
                    StatCard(
                        title = "Monthly Revenue",
                        value = "₹34,80,000",
                        icon = Icons.Default.AccountBalance,
                        modifier = Modifier.weight(1f),
                        trend = "+18.2%",
                        isPositive = true,
                        testTagStr = "stat_monthly_revenue",
                        containerColor = PureWhite,
                        contentColor = MatteBlack,
                        iconBgColor = LightBlueAccent,
                        iconColor = ExecutiveBlue
                    )
                }
            }
        }

        // Stock Flow Chart section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                TradeFitSectionHeader(title = "Stock & Roll Movements")
                Text(
                    text = "In-out balance trend of fabric rolls (Meters x 100) over past 12 days",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CanvasStockChart(dataPoints = stockLevelData)
            }
        }

        // Quick Actions section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                TradeFitSectionHeader(title = "Quick Operational Actions")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionItem(
                        title = "New Challan",
                        icon = Icons.Default.AddBox,
                        color = ExecutiveBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTo("delivery_challan") }
                    )
                    QuickActionItem(
                        title = "New Customer",
                        icon = Icons.Default.PersonAdd,
                        color = ElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTo("customer_master") }
                    )
                    QuickActionItem(
                        title = "Stock Entry",
                        icon = Icons.Default.LibraryAdd,
                        color = MatteBlack,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTo("stock") }
                    )
                }
            }
        }

        // Recent Activity List
        item {
            TradeFitSectionHeader(
                title = "Recent Active Dispatch Queue",
                actionText = "See Reports",
                onActionClick = { onNavigateTo("reports") }
            )
        }

        val recentDispatchItems = listOf(
            DispatchItem("DC-2026-042", "Evergreen Textiles Ltd.", "Pure Cotton Denim", "3,400 meters", "Dispatched", PositiveGrowth),
            DispatchItem("DC-2026-043", "Maharani Silks & Fabrics", "Premium Mulberry Silk", "450 meters", "Pending Load", PendingOrange),
            DispatchItem("DC-2026-044", "Vardhman Hosiery Co.", "Linen Cotton Blend", "1,850 meters", "Authorized", ExecutiveBlue),
            DispatchItem("DC-2026-045", "Royal Garments House", "Viscose Rayon Printed", "1,200 meters", "Draft", SlateTextLight)
        )

        items(recentDispatchItems) { dispatch ->
            DispatchRow(dispatch = dispatch)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class DispatchItem(
    val id: String,
    val customer: String,
    val fabric: String,
    val quantity: String,
    val status: String,
    val statusColor: Color
)

@Composable
fun DispatchRow(dispatch: DispatchItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dispatch_row_${dispatch.id}"),
        shape = RoundedCornerShape(24.dp),
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dispatch.id,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ExecutiveBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(dispatch.statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = dispatch.status.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = dispatch.statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dispatch.customer,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MatteBlack,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fabric: ${dispatch.fabric}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextDark
                )
            }
            Text(
                text = dispatch.quantity,
                style = MaterialTheme.typography.titleMedium,
                color = ExecutiveBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("quick_action_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardBorder()
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MatteBlack,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
