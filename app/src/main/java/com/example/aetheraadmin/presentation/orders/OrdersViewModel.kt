package com.example.aetheraadmin.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Order
import com.example.aetheraadmin.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState = _uiState.asStateFlow()

    init { loadOrders() }

    fun loadOrders() {
        viewModelScope.launch {
            orderRepository.getOrders().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, error = "")
                    is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, orders = result.data)
                    is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }
}
