package com.example.aetheraadmin.presentation.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.category
import com.example.aetheraadmin.domain.repo.repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val repo: repo) : ViewModel(){

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