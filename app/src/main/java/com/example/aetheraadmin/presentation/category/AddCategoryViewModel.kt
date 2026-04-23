package com.example.aetheraadmin.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Category
import com.example.aetheraadmin.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddCategoryUiState(
    val isLoading: Boolean = false,
    val success: String = "",
    val error: String = ""
)

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCategoryUiState())
    val uiState = _uiState.asStateFlow()

    fun addCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.addCategory(category).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, error = "")
                    is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, success = result.data)
                    is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    fun resetState() { _uiState.value = AddCategoryUiState() }
}
