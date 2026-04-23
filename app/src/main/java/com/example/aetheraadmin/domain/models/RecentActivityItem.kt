package com.example.aetheraadmin.domain.models

enum class ActivityType { ORDER, LOW_STOCK, RECENT_ORDER, CATEGORY, PRODUCT }
enum class ActivityPriority { NORMAL, HIGH, ACTION }

data class RecentActivityItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val timestamp: Long = 0L,
    val type: ActivityType = ActivityType.ORDER,
    val priority: ActivityPriority = ActivityPriority.NORMAL
)
