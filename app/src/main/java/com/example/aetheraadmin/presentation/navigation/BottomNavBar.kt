package com.example.aetheraadmin.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

// --- Data Model ---

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Route
)

// --- Items List ---

val bottomNavItems = listOf(
    BottomNavItem(
        label = "Dashboard",
        icon = Icons.Outlined.Dashboard,
        route = Route.Dashboard
    ),
    BottomNavItem(
        label = "Products",
        icon = Icons.Outlined.Inventory2,
        route = Route.ProductList
    ),
    BottomNavItem(
        label = "Categories",
        icon = Icons.Outlined.Category,
        route = Route.CategoryList
    )
)

// --- Composable ---

@Composable
fun BottomNavBar(
    currentRoute: Route,
    onNavigate: (Route) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}