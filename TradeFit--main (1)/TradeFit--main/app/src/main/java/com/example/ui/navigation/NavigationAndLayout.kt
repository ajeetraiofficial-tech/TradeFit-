package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.CardBorder
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Navigation Routes
object TradeFitRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val CUSTOMER_MASTER = "customer_master"
    const val FABRIC_MASTER = "fabric_master"
    const val STOCK = "stock"
    const val DELIVERY_CHALLAN = "delivery_challan"
    const val PROFORMA_INVOICE = "proforma_invoice"
    const val TAX_INVOICE = "tax_invoice"
    const val PAYMENTS = "payments"
    const val LEDGER = "ledger"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
}

// Navigation Item Definition
data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

val ERPNavigationItems = listOf(
    NavigationItem(TradeFitRoutes.DASHBOARD, "Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    NavigationItem(TradeFitRoutes.CUSTOMER_MASTER, "Customers", Icons.Default.Group, "nav_customers"),
    NavigationItem(TradeFitRoutes.FABRIC_MASTER, "Fabric Master", Icons.Default.Layers, "nav_fabrics"),
    NavigationItem(TradeFitRoutes.STOCK, "Stock", Icons.Default.Inventory, "nav_stock"),
    NavigationItem(TradeFitRoutes.DELIVERY_CHALLAN, "Delivery Challan", Icons.Default.LocalShipping, "nav_challans"),
    NavigationItem(TradeFitRoutes.PROFORMA_INVOICE, "Proforma Invoice", Icons.Default.Receipt, "nav_proforma"),
    NavigationItem(TradeFitRoutes.TAX_INVOICE, "Tax Invoice", Icons.Default.ReceiptLong, "nav_tax_invoices"),
    NavigationItem(TradeFitRoutes.PAYMENTS, "Payments", Icons.Default.Payment, "nav_payments"),
    NavigationItem(TradeFitRoutes.LEDGER, "Ledger", Icons.Default.Description, "nav_ledger"),
    NavigationItem(TradeFitRoutes.REPORTS, "Reports", Icons.Default.Assessment, "nav_reports"),
    NavigationItem(TradeFitRoutes.SETTINGS, "Settings", Icons.Default.Settings, "nav_settings")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeFitAppShell() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: TradeFitRoutes.LOGIN
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    
    // Check screen size for responsiveness. Width >= 720dp is classified as Tablet / Wide Screen
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 720

    // Hide top bar and drawer navigation for standalone login screen
    val isLoginScreen = currentRoute == TradeFitRoutes.LOGIN

    if (isLoginScreen) {
        // Simple fullscreen login without shell borders
        ERPRouter(navController = navController)
    } else {
        // Authenticated responsive shell layout
        if (isWideScreen) {
            // Tablet/Desktop Layout: Persistent Elegant Sidebar Drawer
            Row(modifier = Modifier.fillMaxSize()) {
                // Sidebar panel
                PersistentSidebarPanel(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onLogout = {
                        navController.navigate(TradeFitRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
                
                // Vertical divider line
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(DarkSlateBorder)
                )

                // Main Content area (with Top Navbar inside)
                Column(modifier = Modifier.fillMaxSize()) {
                    TopNavbar(
                        title = getScreenTitle(currentRoute),
                        showMenuIcon = false,
                        onMenuClick = {},
                        onLogout = {
                            navController.navigate(TradeFitRoutes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                    
                    Box(modifier = Modifier.weight(1f)) {
                        ERPRouter(navController = navController)
                    }
                }
            }
        } else {
            // Mobile Layout: Slide-out Hamburg Modal Drawer Navigation
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = PureWhite,
                        modifier = Modifier.width(300.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                // Profile/Branding Header inside drawer
                                DrawerBrandingHeader()
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Nav items
                                ERPNavigationItems.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = if (selected) ExecutiveBlue else SlateTextDark
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.title,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (selected) ExecutiveBlue else MatteBlack
                                            )
                                        },
                                        selected = selected,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .testTag(item.testTag),
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = LightBlueAccent,
                                            unselectedContainerColor = Color.Transparent
                                        )
                                    )
                                }
                            }

                            // Drawer Footer Logout action
                            TextButton(
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate(TradeFitRoutes.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("nav_logout_drawer"),
                                colors = ButtonDefaults.textButtonColors(contentColor = WarningAlert)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Logout Account", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            ) {
                // Screen Content scaffold
                Scaffold(
                    topBar = {
                        TopNavbar(
                            title = getScreenTitle(currentRoute),
                            showMenuIcon = true,
                            onMenuClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            onLogout = {
                                navController.navigate(TradeFitRoutes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ERPRouter(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ERPRouter(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TradeFitRoutes.LOGIN
    ) {
        composable(TradeFitRoutes.LOGIN) {
            // Safe guard fallback
            LoginScreen(onLoginSuccess = {
                navController.navigate(TradeFitRoutes.DASHBOARD) {
                    popUpTo(TradeFitRoutes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(TradeFitRoutes.DASHBOARD) {
            DashboardScreen(onNavigateTo = { route ->
                navController.navigate(route) {
                    launchSingleTop = true
                }
            })
        }
        composable(TradeFitRoutes.CUSTOMER_MASTER) {
            CustomerMasterScreen()
        }
        composable(TradeFitRoutes.FABRIC_MASTER) {
            FabricMasterScreen()
        }
        composable(TradeFitRoutes.STOCK) {
            StockScreen()
        }
        composable(TradeFitRoutes.DELIVERY_CHALLAN) {
            DeliveryChallanScreen(
                onNavigateToProforma = {
                    navController.navigate(TradeFitRoutes.PROFORMA_INVOICE) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(TradeFitRoutes.PROFORMA_INVOICE) {
            ProformaInvoiceScreen()
        }
        composable(TradeFitRoutes.TAX_INVOICE) {
            TaxInvoiceScreen()
        }
        composable(TradeFitRoutes.PAYMENTS) {
            PaymentsScreen()
        }
        composable(TradeFitRoutes.LEDGER) {
            LedgerScreen()
        }
        composable(TradeFitRoutes.REPORTS) {
            ReportsScreen()
        }
        composable(TradeFitRoutes.SETTINGS) {
            SettingsScreen()
        }
    }
}

@Composable
fun PersistentSidebarPanel(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(PureWhite)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            DrawerBrandingHeader()
            Spacer(modifier = Modifier.height(28.dp))

            ERPNavigationItems.forEach { item ->
                val selected = currentRoute == item.route
                val backgroundTint = if (selected) LightBlueAccent else Color.Transparent
                val textColor = if (selected) ExecutiveBlue else MatteBlack
                val iconColor = if (selected) ExecutiveBlue else SlateTextDark

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundTint)
                        .clickable { onItemClick(item.route) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag(item.testTag),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = textColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Account / Logout card at bottom of persistent panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sidebar_logout_panel"),
            colors = CardDefaults.cardColors(containerColor = OffWhite),
            border = CardBorder()
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ExecutiveBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AD", fontWeight = FontWeight.Bold, color = PureWhite, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Admin Root", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MatteBlack)
                        Text("Online", style = MaterialTheme.typography.labelSmall, color = PositiveGrowth, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = WarningAlert,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerBrandingHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ExecutiveBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BusinessCenter,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "TradeFit ERP",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MatteBlack
            )
            Text(
                text = "FABRIC SOLUTIONS",
                style = MaterialTheme.typography.labelSmall,
                color = ExecutiveBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavbar(
    title: String,
    showMenuIcon: Boolean,
    onMenuClick: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MatteBlack
                )
                Text(
                    text = "Consolidated Dispatch Ledger",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextLight
                )
            }
        },
        navigationIcon = {
            if (showMenuIcon) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.testTag("navbar_menu_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Sidebar",
                        tint = MatteBlack
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
        },
        actions = {
            // High Density circular Search button
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(1.dp, DarkSlateBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = SlateTextDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Notification Badge Icon
            IconButton(onClick = {}) {
                BadgedBox(
                    badge = { Badge(containerColor = WarningAlert) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "System notifications",
                        tint = SlateTextDark
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Profile Picture Circle with Double border
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(LightBlueAccent)
                    .border(2.dp, ExecutiveBlue, CircleShape)
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FX",
                    fontWeight = FontWeight.Bold,
                    color = ExecutiveBlue,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PureWhite,
            scrolledContainerColor = PureWhite
        ),
        modifier = Modifier
            .border(width = 1.dp, color = DarkSlateBorder)
            .testTag("top_navbar")
    )
}

fun getScreenTitle(route: String): String {
    return when (route) {
        TradeFitRoutes.DASHBOARD -> "TradeFit Operations Dashboard"
        TradeFitRoutes.CUSTOMER_MASTER -> "Customer Database Control"
        TradeFitRoutes.FABRIC_MASTER -> "Fabric Quality Index"
        TradeFitRoutes.STOCK -> "Roll Inventory Registry"
        TradeFitRoutes.DELIVERY_CHALLAN -> "Delivery Dispatch Note (Challan)"
        TradeFitRoutes.PROFORMA_INVOICE -> "Proforma Quotation Statement"
        TradeFitRoutes.TAX_INVOICE -> "Tax Invoice Statement"
        TradeFitRoutes.PAYMENTS -> "Payment Transactions Log"
        TradeFitRoutes.LEDGER -> "General Finance Ledger"
        TradeFitRoutes.REPORTS -> "Visual Analytics & Surcharge Reports"
        TradeFitRoutes.SETTINGS -> "ERP System Configurations"
        else -> "TradeFit ERP Console"
    }
}
