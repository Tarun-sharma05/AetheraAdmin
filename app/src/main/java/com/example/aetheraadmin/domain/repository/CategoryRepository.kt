package com.example.aetheraadmin.domain.repository

import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun addCategory(category: Category): Flow<ResultState<String>>
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun getCategoryById(id: String): Flow<ResultState<Category?>>
    fun updateCategory(category: Category): Flow<ResultState<String>>
    fun deleteCategory(id: String): Flow<ResultState<String>>
}
