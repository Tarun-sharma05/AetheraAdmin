package com.example.aetheraadmin.data.repository

import com.example.aetheraadmin.common.CATEGORY
import com.example.aetheraadmin.common.LOW_STOCK_THRESHOLD
import com.example.aetheraadmin.common.ORDERS
import com.example.aetheraadmin.common.PRODUCTS
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.ActivityPriority
import com.example.aetheraadmin.domain.models.ActivityType
import com.example.aetheraadmin.domain.models.ChartPoint
import com.example.aetheraadmin.domain.models.DashboardStats
import com.example.aetheraadmin.domain.models.OrderStatus
import com.example.aetheraadmin.domain.models.RecentActivityItem
import com.example.aetheraadmin.domain.repository.DashboardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DashboardRepository {

    override fun getDashboardStats(): Flow<ResultState<DashboardStats>> = callbackFlow {
        trySend(ResultState.Loading)
        var productCount = 0
        var categoryCount = 0
        var orderCount = 0
        var revenue = 0.0
        var lowStock = 0
        var pendingOrders = 0
        var errors = 0
        var done = 0
        val total = 3

        fun checkDone() {
            done++
            if (done == total) {
                trySend(ResultState.Success(DashboardStats(
                    totalProducts    = productCount,
                    totalCategories  = categoryCount,
                    totalOrders      = orderCount,
                    totalRevenue     = revenue,
                    lowStockCount    = lowStock,
                    pendingOrdersCount = pendingOrders
                )))
            }
        }

        firestore.collection(PRODUCTS).get()
            .addOnSuccessListener { snap ->
                productCount = snap.size()
                lowStock = snap.documents.count { doc ->
                    (doc.getLong("stockQuantity") ?: 0L) <= LOW_STOCK_THRESHOLD
                }
                checkDone()
            }
            .addOnFailureListener { errors++; checkDone() }

        firestore.collection(CATEGORY).get()
            .addOnSuccessListener { snap -> categoryCount = snap.size(); checkDone() }
            .addOnFailureListener { errors++; checkDone() }

        firestore.collection(ORDERS).get()
            .addOnSuccessListener { snap ->
                orderCount = snap.size()
                revenue = snap.documents.sumOf { doc -> doc.getDouble("totalAmount") ?: 0.0 }
                pendingOrders = snap.documents.count { doc ->
                    doc.getString("status") == OrderStatus.PENDING.name
                }
                checkDone()
            }
            .addOnFailureListener { errors++; checkDone() }

        awaitClose { close() }
    }

    override fun getWeeklySalesChart(): Flow<ResultState<List<ChartPoint>>> = callbackFlow {
        trySend(ResultState.Loading)
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        // Build last 7 days labels
        val days = (6 downTo 0).map { offset ->
            cal.timeInMillis = System.currentTimeMillis() - offset * 86_400_000L
            dayFormat.format(cal.time)
        }
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 86_400_000L

        firestore.collection(ORDERS)
            .whereGreaterThanOrEqualTo("createdAt", sevenDaysAgo)
            .get()
            .addOnSuccessListener { snap ->
                // Group revenue by day label
                val revenueMap = mutableMapOf<String, Double>()
                snap.documents.forEach { doc ->
                    val ts = doc.getLong("createdAt") ?: 0L
                    cal.timeInMillis = ts
                    val label = dayFormat.format(cal.time)
                    revenueMap[label] = (revenueMap[label] ?: 0.0) + (doc.getDouble("totalAmount") ?: 0.0)
                }
                val points = days.map { label -> ChartPoint(label = label, value = revenueMap[label] ?: 0.0) }
                trySend(ResultState.Success(points))
            }
            .addOnFailureListener {
                // Return empty chart on failure rather than crashing
                val points = days.map { label -> ChartPoint(label = label, value = 0.0) }
                trySend(ResultState.Success(points))
            }
        awaitClose { close() }
    }

    override fun getRecentActivity(): Flow<ResultState<List<RecentActivityItem>>> = callbackFlow {
        trySend(ResultState.Loading)
        val items = mutableListOf<RecentActivityItem>()
        var done = 0

        // Pending orders count
        firestore.collection(ORDERS)
            .whereEqualTo("status", OrderStatus.PENDING.name)
            .get()
            .addOnSuccessListener { snap ->
                val count = snap.size()
                if (count > 0) {
                    items.add(RecentActivityItem(
                        id       = "pending_orders",
                        title    = "Pending Orders",
                        subtitle = "$count orders require fulfillment",
                        type     = ActivityType.ORDER,
                        priority = ActivityPriority.ACTION
                    ))
                }
                done++
                if (done == 3) trySend(ResultState.Success(items.sortedBy { it.priority.ordinal }))
            }
            .addOnFailureListener { done++; if (done == 3) trySend(ResultState.Success(items)) }

        // Low stock products
        firestore.collection(PRODUCTS)
            .whereLessThanOrEqualTo("stockQuantity", LOW_STOCK_THRESHOLD)
            .get()
            .addOnSuccessListener { snap ->
                val count = snap.size()
                if (count > 0) {
                    items.add(RecentActivityItem(
                        id       = "low_stock",
                        title    = "Low Stock Alerts",
                        subtitle = "$count products below threshold",
                        type     = ActivityType.LOW_STOCK,
                        priority = ActivityPriority.HIGH
                    ))
                }
                done++
                if (done == 3) trySend(ResultState.Success(items.sortedBy { it.priority.ordinal }))
            }
            .addOnFailureListener { done++; if (done == 3) trySend(ResultState.Success(items)) }

        // Most recent order
        firestore.collection(ORDERS)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                snap.documents.firstOrNull()?.let { doc ->
                    val orderId = doc.getString("orderId") ?: doc.id
                    val ts = doc.getLong("createdAt") ?: 0L
                    items.add(RecentActivityItem(
                        id        = "recent_order",
                        title     = "Recent Orders",
                        subtitle  = "Order #$orderId just placed",
                        timestamp = ts,
                        type      = ActivityType.RECENT_ORDER,
                        priority  = ActivityPriority.NORMAL
                    ))
                }
                done++
                if (done == 3) trySend(ResultState.Success(items.sortedBy { it.priority.ordinal }))
            }
            .addOnFailureListener { done++; if (done == 3) trySend(ResultState.Success(items)) }

        awaitClose { close() }
    }
}
