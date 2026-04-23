package com.example.aetheraadmin.data.repository

import com.example.aetheraadmin.common.ORDERS
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Order
import com.example.aetheraadmin.domain.models.OrderStatus
import com.example.aetheraadmin.domain.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrderRepository {

    override fun getOrders(): Flow<ResultState<List<Order>>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(ORDERS)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                val orders = snap.documents.mapNotNull { doc ->
                    try {
                        val statusStr = doc.getString("status") ?: "PENDING"
                        val status = runCatching { OrderStatus.valueOf(statusStr) }.getOrDefault(OrderStatus.PENDING)
                        Order(
                            orderId       = doc.getString("orderId") ?: doc.id,
                            userId        = doc.getString("userId") ?: "",
                            totalAmount   = doc.getDouble("totalAmount") ?: 0.0,
                            status        = status,
                            paymentStatus = doc.getString("paymentStatus") ?: "",
                            createdAt     = doc.getLong("createdAt") ?: 0L
                        )
                    } catch (e: Exception) { null }
                }
                trySend(ResultState.Success(orders))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to load orders")) }
        awaitClose { close() }
    }

    override fun getOrderById(orderId: String): Flow<ResultState<Order?>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(ORDERS).document(orderId).get()
            .addOnSuccessListener { doc ->
                trySend(ResultState.Success(doc.toObject(Order::class.java)))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Order not found")) }
        awaitClose { close() }
    }

    override fun updateOrderStatus(orderId: String, status: OrderStatus): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(ORDERS).document(orderId)
            .update("status", status.name)
            .addOnSuccessListener { trySend(ResultState.Success("Order status updated")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to update status")) }
        awaitClose { close() }
    }
}
