package com.example.aetheraadmin.domain.repository

import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Order
import com.example.aetheraadmin.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<ResultState<List<Order>>>
    fun getOrderById(orderId: String): Flow<ResultState<Order?>>
    fun updateOrderStatus(orderId: String, status: OrderStatus): Flow<ResultState<String>>
}
