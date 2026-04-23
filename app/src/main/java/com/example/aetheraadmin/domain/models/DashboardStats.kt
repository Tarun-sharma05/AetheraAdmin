package com.example.aetheraadmin.domain.models

data class DashboardStats(
    val totalProducts: Int = 0,
    val totalCategories: Int = 0,
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val lowStockCount: Int = 0,
    val pendingOrdersCount: Int = 0,
    val deliveredToday: Int = 0,
    val shippedToday: Int = 0,
    val pendingToday: Int = 0
)
