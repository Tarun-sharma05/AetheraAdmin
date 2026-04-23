package com.example.aetheraadmin.data.repository

import com.example.aetheraadmin.common.PRODUCTS
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.common.LOW_STOCK_THRESHOLD
import com.example.aetheraadmin.domain.models.Product
import com.example.aetheraadmin.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override fun addProduct(product: Product): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(PRODUCTS).add(product)
            .addOnSuccessListener { trySend(ResultState.Success("Product added successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to add product")) }
        awaitClose { close() }
    }

    override fun getProducts(): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(PRODUCTS).get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                trySend(ResultState.Success(products))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to load products")) }
        awaitClose { close() }
    }

    override fun getProductById(id: String): Flow<ResultState<Product?>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(PRODUCTS).document(id).get()
            .addOnSuccessListener { doc ->
                val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
                trySend(ResultState.Success(product))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Product not found")) }
        awaitClose { close() }
    }

    override fun updateProduct(product: Product): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(PRODUCTS).document(product.id)
            .set(product.copy(updatedAt = System.currentTimeMillis()))
            .addOnSuccessListener { trySend(ResultState.Success("Product updated successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to update product")) }
        awaitClose { close() }
    }

    override fun deleteProduct(id: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(PRODUCTS).document(id).delete()
            .addOnSuccessListener { trySend(ResultState.Success("Product deleted")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to delete product")) }
        awaitClose { close() }
    }

    override fun getLowStockProducts(threshold: Int): Flow<ResultState<List<Product>>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(PRODUCTS)
            .whereLessThanOrEqualTo("stockQuantity", threshold)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                trySend(ResultState.Success(products))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to load low stock products")) }
        awaitClose { close() }
    }
}
