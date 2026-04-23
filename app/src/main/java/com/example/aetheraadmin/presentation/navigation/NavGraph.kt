package com.example.aetheraadmin.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.aetheraadmin.presentation.Screen.AddCategoryScreen
import com.example.aetheraadmin.presentation.Screen.AddProductScreen
import com.example.aetheraadmin.presentation.Screen.AnalyticsScreen
import com.example.aetheraadmin.presentation.Screen.CategoryListScreen
import com.example.aetheraadmin.presentation.Screen.CustomersScreen
import com.example.aetheraadmin.presentation.Screen.DashboardScreen
import com.example.aetheraadmin.presentation.Screen.EditProductScreen
import com.example.aetheraadmin.presentation.Screen.OrdersScreen
import com.example.aetheraadmin.presentation.Screen.ProductListScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Dashboard::class,    Route.Dashboard.serializer())
                    subclass(Route.ProductList::class,  Route.ProductList.serializer())
                    subclass(Route.CategoryList::class, Route.CategoryList.serializer())
                    subclass(Route.Orders::class,       Route.Orders.serializer())
                    subclass(Route.Customers::class,    Route.Customers.serializer())
                    subclass(Route.Analytics::class,    Route.Analytics.serializer())
                    subclass(Route.AddProduct::class,   Route.AddProduct.serializer())
                    subclass(Route.AddCategory::class,  Route.AddCategory.serializer())
                    subclass(Route.EditProduct::class,  Route.EditProduct.serializer())
                }
            }
        },
        Route.Dashboard
    )

    val currentRoute = backStack.last()

    val showBottomBar = currentRoute is Route.Dashboard
            || currentRoute is Route.ProductList
            || currentRoute is Route.CategoryList

    BackHandler(enabled = backStack.size > 1) { backStack.removeLastOrNull() }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute as Route,
                    onNavigate   = { route ->
                        backStack.removeAll { it == route }
                        backStack.add(route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is Route.Dashboard -> NavEntry(key) {
                        DashboardScreen(
                            innerPadding     = innerPadding,
                            onAddProduct     = { backStack.add(Route.AddProduct) },
                            onAddCategory    = { backStack.add(Route.AddCategory) },
                            onViewProducts   = {
                                backStack.removeAll { it == Route.ProductList }
                                backStack.add(Route.ProductList)
                            },
                            onViewCategories = {
                                backStack.removeAll { it == Route.CategoryList }
                                backStack.add(Route.CategoryList)
                            },
                            onViewOrders     = {
                                backStack.removeAll { it == Route.Orders }
                                backStack.add(Route.Orders)
                            },
                            onViewCustomers  = {
                                backStack.removeAll { it == Route.Customers }
                                backStack.add(Route.Customers)
                            }
                        )
                    }
                    is Route.ProductList -> NavEntry(key) {
                        ProductListScreen(
                            innerPadding = innerPadding,
                            onAddProduct = { backStack.add(Route.AddProduct) },
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.CategoryList -> NavEntry(key) {
                        CategoryListScreen(
                            innerPadding  = innerPadding,
                            onAddCategory = { backStack.add(Route.AddCategory) },
                            onBack        = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.Orders -> NavEntry(key) {
                        OrdersScreen(
                            innerPadding = innerPadding,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.Customers -> NavEntry(key) {
                        CustomersScreen(
                            innerPadding = innerPadding,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.Analytics -> NavEntry(key) {
                        AnalyticsScreen(
                            innerPadding = innerPadding,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.AddProduct -> NavEntry(key) {
                        AddProductScreen(
                            innerPadding = innerPadding,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.AddCategory -> NavEntry(key) {
                        AddCategoryScreen(
                            innerPadding = innerPadding,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    is Route.EditProduct -> NavEntry(key) {
                        EditProductScreen(
                            innerPadding = innerPadding,
                            productId    = key.productId,
                            onBack       = { backStack.removeLastOrNull() }
                        )
                    }
                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}
