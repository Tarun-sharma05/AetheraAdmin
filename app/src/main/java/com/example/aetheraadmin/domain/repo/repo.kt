package com.example.aetheraadmin.domain.repo

import android.net.Uri
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.ProductsModels
import com.example.aetheraadmin.domain.models.category
import kotlinx.coroutines.flow.Flow

interface repo {

    suspend fun addCategory(category: category): Flow<ResultState<String>>

    suspend fun getCategories(): Flow<ResultState<List<category>>>

    suspend fun addProduct(productsModels: ProductsModels): Flow<ResultState<String>>
    suspend fun getProduct(): Flow<ResultState<List<ProductsModels>>>

    suspend fun uplaodImage(image: Uri): Flow<ResultState<String>>
}