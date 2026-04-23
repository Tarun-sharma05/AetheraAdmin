package com.example.aetheraadmin.presentation.product

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Product
import com.example.aetheraadmin.domain.repository.ProductRepository
import com.example.aetheraadmin.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddProductUiState(
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val imageUploadUrl: String = "",
    val success: String = "",
    val error: String = ""
)

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState = _uiState.asStateFlow()

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            storageRepository.uploadImage(uri).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.value = _uiState.value.copy(isUploadingImage = true, error = "")
                    is ResultState.Success -> _uiState.value = _uiState.value.copy(isUploadingImage = false, imageUploadUrl = result.data)
                    is ResultState.Error   -> _uiState.value = _uiState.value.copy(isUploadingImage = false, error = result.error)
                }
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.addProduct(product).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, error = "")
                    is ResultState.Success -> _uiState.value = _uiState.value.copy(isLoading = false, success = result.data)
                    is ResultState.Error   -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AddProductUiState()
    }
}
