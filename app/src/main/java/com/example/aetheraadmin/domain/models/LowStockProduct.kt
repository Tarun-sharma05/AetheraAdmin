package com.example.aetheraadmin.domain.models

data class LowStockProduct(
    val productId: String = "",
    val name: String = "",
    val stockQuantity: Int = 0,
    val imageUrl: String = ""
)
