package com.example.aetheraadmin.presentation.Screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aetheraadmin.domain.models.ActivityPriority
import com.example.aetheraadmin.domain.models.ActivityType
import com.example.aetheraadmin.domain.models.ChartPoint
import com.example.aetheraadmin.domain.models.RecentActivityItem
import com.example.aetheraadmin.presentation.dashboard.DashboardUiState
import com.example.aetheraadmin.presentation.dashboard.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

// ── Entry point ───────────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onAddProduct: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onViewProducts: () -> Unit = {},
    onViewCategories: () -> Unit = {},
    onViewOrders: () -> Unit = {},
    onViewCustomers: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    DashboardContent(
        state            = state,
        innerPadding     = innerPadding,
        onAddProduct     = onAddProduct,
        onAddCategory    = onAddCategory,
        onViewProducts   = onViewProducts,
        onViewCategories = onViewCategories,
        onViewOrders     = onViewOrders,
        onViewCustomers  = onViewCustomers,
        onRefresh        = viewModel::loadDashboard
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DashboardUiState,
    innerPadding: PaddingValues,
    onAddProduct: () -> Unit,
    onAddCategory: () -> Unit,
    onViewProducts: () -> Unit,
    onViewCategories: () -> Unit,
    onViewOrders: () -> Unit,
    onViewCustomers: () -> Unit,
    onRefresh: () -> Unit
) {
    val indigo    = MaterialTheme.colorScheme.primary
    val violet    = MaterialTheme.colorScheme.tertiary
    val secondary = MaterialTheme.colorScheme.secondary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Aethera Admin",
                            style     = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color     = indigo
                        )
                        Text(
                            "Manage your store at a glance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { scaffoldPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(scaffoldPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── KPI Cards ─────────────────────────────────────────────────────
            item {
                val fmt = NumberFormat.getNumberInstance(Locale.getDefault())
                val revStr = run {
                    val r = state.stats.totalRevenue
                    if (r >= 1000) "₹${String.format("%.1f", r / 1000)}k" else "₹${fmt.format(r.toLong())}"
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KpiCard(Modifier.weight(1f), "Total Products",    state.stats.totalProducts.toString(),
                        Icons.Outlined.Inventory2, MaterialTheme.colorScheme.primaryContainer, indigo, "+12%")
                    KpiCard(Modifier.weight(1f), "Total Categories",  state.stats.totalCategories.toString(),
                        Icons.Outlined.Category,  Color(0xFFE3F2FD),  Color(0xFF1565C0), "+4%")
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KpiCard(Modifier.weight(1f), "Total Orders",   state.stats.totalOrders.toString(),
                        Icons.Outlined.ShoppingCart, Color(0xFFEDE7F6), violet, "+28%")
                    KpiCard(Modifier.weight(1f), "Total Revenue",  revStr,
                        Icons.Outlined.Payments, MaterialTheme.colorScheme.primaryContainer, indigo, "+15%")
                }
            }

            // ── Weekly Sales Chart ────────────────────────────────────────────
            item {
                DashCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Weekly Sales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        AssistChip(onClick = {}, label = { Text("Last 7 Days", fontSize = 11.sp) })
                    }
                    Spacer(Modifier.height(12.dp))
                    if (state.weeklySalesChart.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                            Text("No sales data yet", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        WeeklySalesChart(points = state.weeklySalesChart, barColor = indigo)
                    }
                }
            }

            // ── Quick Actions ─────────────────────────────────────────────────
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionButton(Modifier.weight(1f), "Add Product",       Icons.Outlined.AddBox,
                        MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, onAddProduct)
                    QuickActionButton(Modifier.weight(1f), "Add Category",      Icons.Outlined.NewLabel,
                        MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, onAddCategory)
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionButton(Modifier.weight(1f), "View Orders",       Icons.Outlined.ListAlt,
                        MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface, onViewOrders)
                    QuickActionButton(Modifier.weight(1f), "Manage Customers",  Icons.Outlined.People,
                        MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface, onViewCustomers)
                }
            }

            // ── Order Status Donut ────────────────────────────────────────────
            item {
                DashCard {
                    Text("Order Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    val today = state.stats.deliveredToday + state.stats.shippedToday + state.stats.pendingToday
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        DonutChart(
                            delivered = state.stats.deliveredToday.toFloat(),
                            shipped   = state.stats.shippedToday.toFloat(),
                            pending   = state.stats.pendingToday.toFloat(),
                            centerLabel = "$today",
                            modifier = Modifier.size(100.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DonutLegend(Color(0xFF2D4ADD), "Delivered")
                            DonutLegend(Color(0xFF34D399), "Shipped")
                            DonutLegend(Color(0xFFFBBF24), "Pending")
                        }
                    }
                }
            }

            // ── Business Activity ─────────────────────────────────────────────
            item {
                DashCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Business Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = {}) { Icon(Icons.Outlined.MoreHoriz, contentDescription = null) }
                    }
                    if (state.recentActivity.isEmpty()) {
                        Text("No recent activity", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        state.recentActivity.forEach { item ->
                            ActivityRow(item = item)
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = onViewOrders, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("View All Activity", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // ── Upcoming Features ─────────────────────────────────────────────
            item {
                Text(
                    "🔒  Upcoming Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    UpcomingCard(Modifier.weight(1f), "Analytics & Reports",  Icons.Outlined.BarChart)
                    UpcomingCard(Modifier.weight(1f), "Discounts",            Icons.Outlined.LocalOffer)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    UpcomingCard(Modifier.weight(1f), "Delivery Tracking",    Icons.Outlined.LocalShipping)
                    UpcomingCard(Modifier.weight(1f), "Notifications",        Icons.Outlined.Notifications)
                }
            }
        }
    }
}

// ── Sub-composables ────────────────────────────────────────────────────────────

@Composable
private fun DashCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content   = { Column(modifier = Modifier.padding(16.dp)) { content() } }
    )
}

@Composable
private fun KpiCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    trend: String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
                    contentAlignment = Alignment.Center
                ) { Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp)) }
                Surface(
                    shape  = RoundedCornerShape(100.dp),
                    color  = Color(0xFFD1FAE5)
                ) {
                    Text(
                        trend,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color    = Color(0xFF065F46),
                        style    = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.8.sp)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier.height(96.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick   = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = contentColor)
        }
    }
}

@Composable
private fun WeeklySalesChart(points: List<ChartPoint>, barColor: Color) {
    val maxVal = points.maxOfOrNull { it.value } ?: 1.0
    val primary   = barColor
    val barLight  = barColor.copy(alpha = 0.25f)
    val barMid    = barColor.copy(alpha = 0.5f)

    Column {
        Row(
            Modifier.fillMaxWidth().height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            points.forEachIndexed { i, point ->
                val ratio  = if (maxVal > 0) (point.value / maxVal).toFloat() else 0f
                val isPeak = point.value == maxVal
                val color  = when {
                    isPeak      -> primary
                    ratio > 0.5 -> barMid
                    else        -> barLight
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight(max(ratio, 0.05f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(color)
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            points.forEach { point ->
                Text(point.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f).wrapContentWidth())
            }
        }
    }
}

@Composable
private fun DonutChart(delivered: Float, shipped: Float, pending: Float, centerLabel: String, modifier: Modifier = Modifier) {
    val total = delivered + shipped + pending
    val deliveredColor = Color(0xFF2D4ADD)
    val shippedColor   = Color(0xFF34D399)
    val pendingColor   = Color(0xFFFBBF24)
    val bgColor        = Color(0xFFE6E8EA)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = size.minDimension * 0.18f, cap = StrokeCap.Round)
            val rect   = androidx.compose.ui.geometry.Rect(Offset(stroke.width / 2, stroke.width / 2), Size(size.width - stroke.width, size.height - stroke.width))
            drawArc(color = bgColor, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = stroke, topLeft = rect.topLeft, size = rect.size)
            if (total > 0) {
                var startAngle = -90f
                listOf(delivered to deliveredColor, shipped to shippedColor, pending to pendingColor).forEach { (value, color) ->
                    val sweep = (value / total) * 360f
                    if (sweep > 0f) {
                        drawArc(color = color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, style = stroke, topLeft = rect.topLeft, size = rect.size)
                        startAngle += sweep
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Today", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DonutLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActivityRow(item: RecentActivityItem) {
    val (iconBg, iconTint, badgeText, badgeColor, badgeTextColor) = when (item.type) {
        ActivityType.LOW_STOCK    -> listOf(Color(0xFFFEF2F2), Color(0xFFDC2626), "High",   Color(0xFFFEF2F2), Color(0xFFDC2626))
        ActivityType.ORDER        -> listOf(Color(0xFFFFFBEB), Color(0xFFD97706), "Action", Color(0xFFFFFBEB), Color(0xFFD97706))
        ActivityType.RECENT_ORDER -> listOf(Color(0xFFECFDF5), Color(0xFF059669), "",       Color.Transparent, Color.Transparent)
        else                      -> listOf(Color(0xFFF1F5F9), Color(0xFF64748B), "",       Color.Transparent, Color.Transparent)
    }
    @Suppress("UNCHECKED_CAST")
    val c = iconBg as Color; val t = iconTint as Color; val b = badgeText as String
    val bc = badgeColor as Color; val btc = badgeTextColor as Color

    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(c),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (item.type) {
                ActivityType.LOW_STOCK    -> Icons.Outlined.Warning
                ActivityType.ORDER        -> Icons.Outlined.HourglassEmpty
                ActivityType.RECENT_ORDER -> Icons.Outlined.ShoppingBag
                else                      -> Icons.Outlined.Info
            }
            Icon(icon, contentDescription = null, tint = t, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title,    style = MaterialTheme.typography.labelLarge,  fontWeight = FontWeight.Medium)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall,   color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (b.isNotBlank()) {
            Surface(shape = RoundedCornerShape(4.dp), color = bc) {
                Text(b, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = btc, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Text("2m ago", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun UpcomingCard(modifier: Modifier, label: String, icon: ImageVector) {
    Card(
        modifier  = modifier.height(80.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}
