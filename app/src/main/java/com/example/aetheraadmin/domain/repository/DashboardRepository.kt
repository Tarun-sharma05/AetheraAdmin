package com.example.aetheraadmin.domain.repository

import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.ChartPoint
import com.example.aetheraadmin.domain.models.DashboardStats
import com.example.aetheraadmin.domain.models.RecentActivityItem
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardStats(): Flow<ResultState<DashboardStats>>
    fun getWeeklySalesChart(): Flow<ResultState<List<ChartPoint>>>
    fun getRecentActivity(): Flow<ResultState<List<RecentActivityItem>>>
}
