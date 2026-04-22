package com.example.aetheraadmin.presentation.Screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aetheraadmin.presentation.AppViewModel
import com.example.aetheraadmin.ui.theme.AetheraAdminTheme

// ── Stateful entry point — wires ViewModel; NavGraph calls this ───────────────

@Composable
fun DashboardScreen(
    viewModel: AppViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onAddProduct: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onViewProducts: () -> Unit = {},
    onViewCategories: () -> Unit = {}
) {
    val categoryState by viewModel.getCategoryState.collectAsState()
    val productState  by viewModel.getProductState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCategory()
        viewModel.getProducts()
    }

    DashboardScreenContent(
        productCount  = productState.success.size,
        categoryCount = categoryState.success.size,
        innerPadding  = innerPadding,
        onAddProduct  = onAddProduct,
        onAddCategory = onAddCategory,
        onViewProducts   = onViewProducts,
        onViewCategories = onViewCategories
    )
}

// ── Stateless content — no ViewModel; safe for @Preview ──────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    productCount: Int = 0,
    categoryCount: Int = 0,
    innerPadding: PaddingValues = PaddingValues(),
    onAddProduct: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onViewProducts: () -> Unit = {},
    onViewCategories: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Aethera Admin", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { scaffoldPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Overview stats ────────────────────────────────────────────────
            item {
                DashSectionTitle("Overview")
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashStatCard(
                        modifier = Modifier.weight(1f),
                        title    = "Products",
                        count    = productCount,
                        icon     = Icons.Outlined.Inventory2,
                        onClick  = onViewProducts
                    )
                    DashStatCard(
                        modifier = Modifier.weight(1f),
                        title    = "Categories",
                        count    = categoryCount,
                        icon     = Icons.Outlined.Category,
                        onClick  = onViewCategories
                    )
                }
            }

            // ── Quick actions ─────────────────────────────────────────────────
            item {
                DashSectionTitle("Quick Actions")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashActionCard(
                        title    = "Add Product",
                        subtitle = "Add a new product to inventory",
                        icon     = Icons.Outlined.AddBox,
                        onClick  = onAddProduct
                    )
                    DashActionCard(
                        title    = "Add Category",
                        subtitle = "Create a new product category",
                        icon     = Icons.Outlined.Category,
                        onClick  = onAddCategory
                    )
                    DashActionCard(
                        title    = "View All Products",
                        subtitle = "Browse and manage your products",
                        icon     = Icons.Outlined.Inventory2,
                        onClick  = onViewProducts
                    )
                    DashActionCard(
                        title    = "View All Categories",
                        subtitle = "Browse and manage categories",
                        icon     = Icons.Outlined.ShoppingCart,
                        onClick  = onViewCategories
                    )
                }
            }

            // ── Coming soon ───────────────────────────────────────────────────
            item {
                DashSectionTitle("Coming Soon")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashComingSoonCard("Orders Management",   Icons.Outlined.ShoppingCart)
                    DashComingSoonCard("Analytics & Reports", Icons.Outlined.Analytics)
                    DashComingSoonCard("Customers",           Icons.Outlined.People)
                    DashComingSoonCard("Discounts & Offers",  Icons.Outlined.LocalOffer)
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Dashboard – Light", showBackground = true, showSystemUi = true)
@Composable
private fun DashboardScreenPreviewLight() {
    AetheraAdminTheme(darkTheme = false) {
        DashboardScreenContent(
            productCount  = 24,
            categoryCount = 6
        )
    }
}

@Preview(name = "Dashboard – Dark", showBackground = true, showSystemUi = true)
@Composable
private fun DashboardScreenPreviewDark() {
    AetheraAdminTheme(darkTheme = true) {
        DashboardScreenContent(
            productCount  = 24,
            categoryCount = 6
        )
    }
}

@Preview(name = "Dashboard – Empty State", showBackground = true, showSystemUi = true)
@Composable
private fun DashboardScreenPreviewEmpty() {
    AetheraAdminTheme {
        DashboardScreenContent(
            productCount  = 0,
            categoryCount = 0
        )
    }
}

// ── Internal composables (prefixed Dash to avoid name clashes) ─────────────────

@Composable
private fun DashSectionTitle(text: String) {
    Text(
        text       = text,
        style      = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color      = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun DashStatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        onClick   = onClick,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(24.dp)
            )
            Text(
                text       = count.toString(),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text  = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun DashActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DashComingSoonCard(title: String, icon: ImageVector) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier           = Modifier.size(24.dp)
            )
            Text(
                text     = title,
                style    = MaterialTheme.typography.bodyLarge,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text  = "Soon",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            )
        }
    }
}
