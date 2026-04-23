package com.example.aetheraadmin.domain.repository

import android.net.Uri
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun addProduct(product: Product): Flow<ResultState<String>>
    fun getProducts(): Flow<ResultState<List<Product>>>
    fun getProductById(id: String): Flow<ResultState<Product?>>
    fun updateProduct(product: Product): Flow<ResultState<String>>
    fun deleteProduct(id: String): Flow<ResultState<String>>
    fun getLowStockProducts(threshold: Int): Flow<ResultState<List<Product>>>
}
