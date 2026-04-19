package com.example.aetheraadmin.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Route {

    // Bottom Nav Screens
    @Serializable
    data object Dashboard : Route()

    @Serializable
    data object ProductList : Route()

    @Serializable
    data object CategoryList : Route()

    @Serializable
    data object Orders : Route()

    // Detail Screens (no bottom bar)
    @Serializable
    data object AddProduct : Route()

    @Serializable
    data object AddCategory : Route()

    @Serializable
    data class EditProduct(val productId: String) : Route()
}