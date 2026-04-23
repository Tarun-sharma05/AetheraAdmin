package com.example.aetheraadmin.domain.models

enum class OrderStatus { PENDING, SHIPPED, DELIVERED, CANCELLED }

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentStatus: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
