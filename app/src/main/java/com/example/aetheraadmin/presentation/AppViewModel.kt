package com.example.aetheraadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
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

    fun addCategory(category: category){
        viewModelScope.launch {
            repo.addCategory(category).collectLatest{
                when(it){
                    is ResultState.Loading -> {
                        _addCategoryState.value = addCategoryState.value.copy(isLoading = true)
                    }

                    is ResultState.Success ->{
                        _addCategoryState.value = addCategoryState.value.copy(
//                            isLoading = false,
                            success = it.data
                        )
                    }

                    is ResultState.Error -> {
                        _addCategoryState.value = addCategoryState.value.copy(
//                            isLoading = false,
                            error = it.error
                        )
                    }
                }
        }
    }
    }

}

data class AddCategoryState(
    val isLoading: Boolean = false,
    val error : String = "",
    val success : String = ""
)