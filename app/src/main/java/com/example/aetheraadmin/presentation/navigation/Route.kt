package com.example.aetheraadmin.presentation.navigation

// Route.kt
sealed class Route {
    data object Dashboard : Route()
    data object AddProduct : Route()
    data object ProductList : Route()
    data object AddCategory : Route()
}