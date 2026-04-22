package com.example.aetheraadmin.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.ProductsModels
import com.example.aetheraadmin.domain.models.category
import com.example.aetheraadmin.domain.repo.repo
import com.example.aetheraadmin.domain.usecases.GetAllCategoryUseCase
import com.example.aetheraadmin.domain.usecases.GetAllProductUseCase
import com.example.aetheraadmin.domain.usecases.GetCategoryInLimitUseCase
import com.example.aetheraadmin.domain.usecases.GetProductInLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repo: repo,
    private val GetAllCategory: GetAllCategoryUseCase,
    private val GetCategoryInLimit: GetCategoryInLimitUseCase,
    private val GetProductInLimit: GetProductInLimitUseCase,
    private val GetAllProduct: GetAllProductUseCase
    ) : ViewModel(){

    private val _addCategoryState = MutableStateFlow(AddCategoryState())
    val addCategoryState = _addCategoryState.asStateFlow()

    private val _getCategoryState = MutableStateFlow(GetCategoryState())
    val getCategoryState = _getCategoryState.asStateFlow()

    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState = _addProductState.asStateFlow()

    private val _getProductState = MutableStateFlow(GetProductState())
    val getProductState = _getProductState.asStateFlow()

    private val _uploadProductImageState = MutableStateFlow(UploadProductImageState())
    val uploadProductImageState = _uploadProductImageState.asStateFlow()


    fun addProduct(product: ProductsModels){
        viewModelScope.launch {
            repo.addProduct(product).collectLatest{
                when(it){
                    is ResultState.Loading -> {
                        _addProductState.value = addProductState.value.copy(isLoading = true)
                    }

                    is ResultState.Success ->{
                        _addProductState.value = addProductState.value.copy(
                            isLoading = false,
                            success = it.data
                        )
                    }

                    is ResultState.Error -> {
                        _addProductState.value = addProductState.value.copy(
                            isLoading = false,
                            error = it.error
                        )
                    }
                }
            }
        }

    }
    fun uploadProductImage(imageUri: Uri){
        viewModelScope.launch {
           repo.uplaodImage(image = imageUri).collectLatest {
               when(it){
                   is ResultState.Loading -> {
                       _uploadProductImageState.value = UploadProductImageState(isLoading = true)
                   }

                   is ResultState.Success -> {
                       _uploadProductImageState.value = UploadProductImageState(
                           isLoading = false,
                           success = it.data
                       )
                   }

                   is ResultState.Error -> {
                       _uploadProductImageState.value = UploadProductImageState(
                           isLoading = false,
                           error = it.error
                       )
                   }
               }
           }
        }

    }

    fun addCategory(category: category){
        viewModelScope.launch {
            repo.addCategory(category).collectLatest{
                when(it){
                    is ResultState.Loading -> {
                        _addCategoryState.value = addCategoryState.value.copy(isLoading = true)
                    }

                    is ResultState.Success ->{
                        _addCategoryState.value = addCategoryState.value.copy(
                            isLoading = false,
                            success = it.data
                        )
                    }

                    is ResultState.Error -> {
                        _addCategoryState.value = addCategoryState.value.copy(
                            isLoading = false,
                            error = it.error
                        )
                    }
                }
        }
    }
    }


    fun getCategory(){
        viewModelScope.launch {
            repo.getCategories().collectLatest{
                when(it){
                    is ResultState.Loading -> {
                        _getCategoryState.value = GetCategoryState(isLoading = true)
                    }

                    is ResultState.Success ->{
                        _getCategoryState.value = GetCategoryState(success = it.data)
                    }

                    is ResultState.Error -> {
                        _getCategoryState.value = GetCategoryState(error = it.error)
                    }
                }
            }
        }
    }

    fun getProducts(){
        viewModelScope.launch {
            repo.getProduct().collectLatest {
                when(it){
                    is ResultState.Loading -> {
                        _getProductState.value = GetProductState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getProductState.value = GetProductState(success = it.data)
                    }
                    is ResultState.Error -> {
                        _getProductState.value = GetProductState(error = it.error)
                    }
                }
            }
        }
    }

    fun resetAddProductState(){
        _addProductState.value = AddProductState()
    }


}

data class AddCategoryState(
    val isLoading: Boolean = false,
    val error : String = "",
    val success : String = ""
)

data class GetCategoryState(
    val isLoading: Boolean = false,
    val error : String = "",
    val success : List<category> = emptyList()
)


data class AddProductState(
    val isLoading: Boolean = false,
    val error : String = "",
    val success : String = ""
)


data class GetProductState(
    val isLoading: Boolean = false,
    val error : String = "",
    val success : List<ProductsModels> = emptyList()
)
data class UploadProductImageState(
    val isLoading: Boolean = false,
    val error : String = "",
    val success : String = ""
)