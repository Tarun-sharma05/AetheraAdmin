package com.example.aetheraadmin.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    // Bottom Nav Screens
    @Serializable data object Dashboard    : Route, NavKey
    @Serializable data object ProductList  : Route, NavKey
    @Serializable data object CategoryList : Route, NavKey

    // Full screens (no bottom bar)
    @Serializable data object Orders       : Route, NavKey
    @Serializable data object Customers    : Route, NavKey
    @Serializable data object Analytics    : Route, NavKey

    // Detail / form screens
    @Serializable data object AddProduct   : Route, NavKey
    @Serializable data object AddCategory  : Route, NavKey
    @Serializable data class  EditProduct(val productId: String) : Route, NavKey
}