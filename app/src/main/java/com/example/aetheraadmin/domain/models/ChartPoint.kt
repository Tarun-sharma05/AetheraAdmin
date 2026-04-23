package com.example.aetheraadmin.domain.models

data class ChartPoint(
    val label: String = "",   // e.g. "Mon", "Tue"
    val value: Double = 0.0   // revenue / order count for that period
)
