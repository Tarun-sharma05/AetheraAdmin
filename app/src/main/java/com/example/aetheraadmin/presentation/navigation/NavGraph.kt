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
import com.example.aetheraadmin.presentation.Screen.CategoryListScreen
import com.example.aetheraadmin.presentation.Screen.DashboardScreen
import com.example.aetheraadmin.presentation.Screen.EditProductScreen
import com.example.aetheraadmin.presentation.Screen.ProductListScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavGraph() {

    // 1. Back stack — SavedStateConfiguration enables state restoration across process death
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Dashboard::class,     Route.Dashboard.serializer())
                    subclass(Route.ProductList::class,   Route.ProductList.serializer())
                    subclass(Route.CategoryList::class,  Route.CategoryList.serializer())
                    subclass(Route.Orders::class,        Route.Orders.serializer())
                    subclass(Route.AddProduct::class,    Route.AddProduct.serializer())
                    subclass(Route.AddCategory::class,   Route.AddCategory.serializer())
                    subclass(Route.EditProduct::class,   Route.EditProduct.serializer())
                }
            }
        },
        Route.Dashboard
    )

    // 2. Current top of the back stack
    val currentRoute = backStack.last()

    // 3. Bottom bar visible only on the three main tab destinations
    val showBottomBar = currentRoute is Route.Dashboard
            || currentRoute is Route.ProductList
            || currentRoute is Route.CategoryList

    // 4. System back pops the stack; no-op when already at root
    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    // 5. Scaffold — bottom bar injected conditionally
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    // Safe cast: showBottomBar guarantees currentRoute is a Route subtype
                    currentRoute = currentRoute as Route,
                    onNavigate = { route ->
                        // Clear any existing copy of the tab then push it (clears forward history)
                        backStack.removeAll { it == route }
                        backStack.add(route)
                    }
                )
            }
        }
    ) { innerPadding ->

        // 6. NavDisplay — Philip Lackner's pattern:
        //    raw when → NavEntry(key) { } + entryDecorators for ViewModel & rememberSaveable
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                // Preserves rememberSaveable state when navigating back to a screen
                rememberSaveableStateHolderNavEntryDecorator(),
                // Scopes ViewModels to each individual back-stack entry
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {

                    is Route.Dashboard -> NavEntry(key) {
                        DashboardScreen(
                            innerPadding = innerPadding,
                            onAddProduct = { backStack.add(Route.AddProduct) },
                            onAddCategory = { backStack.add(Route.AddCategory) },
                            onViewProducts = {
                                backStack.removeAll { it == Route.ProductList }
                                backStack.add(Route.ProductList)
                            },
                            onViewCategories = {
                                backStack.removeAll { it == Route.CategoryList }
                                backStack.add(Route.CategoryList)
                            }
                        )
                    }

                    is Route.ProductList -> NavEntry(key) {
                        ProductListScreen(
                            innerPadding = innerPadding,
                            onAddProduct = { backStack.add(Route.AddProduct) },
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    is Route.CategoryList -> NavEntry(key) {
                        CategoryListScreen(
                            innerPadding = innerPadding,
                            onAddCategory = { backStack.add(Route.AddCategory) },
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    is Route.AddProduct -> NavEntry(key) {
                        AddProductScreen(
                            innerPadding = innerPadding,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    is Route.AddCategory -> NavEntry(key) {
                        AddCategoryScreen(
                            innerPadding = innerPadding,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    is Route.EditProduct -> NavEntry(key) {
                        EditProductScreen(
                            innerPadding = innerPadding,
                            productId = key.productId,
                            onBack = { backStack.removeLastOrNull() }
                        )
                    }

                    // Route.Orders screen not yet implemented
                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}
