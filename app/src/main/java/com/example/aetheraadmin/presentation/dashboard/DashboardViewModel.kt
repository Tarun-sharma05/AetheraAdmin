package com.example.aetheraadmin.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.ChartPoint
import com.example.aetheraadmin.domain.models.DashboardStats
import com.example.aetheraadmin.domain.models.RecentActivityItem
import com.example.aetheraadmin.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats = DashboardStats(),
    val weeklySalesChart: List<ChartPoint> = emptyList(),
    val recentActivity: List<RecentActivityItem> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        loadStats()
        loadChart()
        loadActivity()
    }

    private fun loadStats() {
        viewModelScope.launch {
            dashboardRepository.getDashboardStats().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, error = "")
                    is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, stats = result.data)
                    is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    private fun loadChart() {
        viewModelScope.launch {
            dashboardRepository.getWeeklySalesChart().collectLatest { result ->
                if (result is ResultState.Success) {
                    _uiState.value = _uiState.value.copy(weeklySalesChart = result.data)
                }
            }
        }
    }

    private fun loadActivity() {
        viewModelScope.launch {
            dashboardRepository.getRecentActivity().collectLatest { result ->
                if (result is ResultState.Success) {
                    _uiState.value = _uiState.value.copy(recentActivity = result.data)
                }
            }
        }
    }
}
